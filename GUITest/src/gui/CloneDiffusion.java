package gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.DoubleOccurrenceException;
import utils.GUIUtilities;
import utils.NoSuchFileException;
import utils.Pair;
import utils.Triple;

@SuppressWarnings("serial")
public class CloneDiffusion extends JApplet {
	private JCheckBox chckbxBedpostx;
	private JTextField textField_1;
	private final String dummyToolTip = "Hi There";
	private int dataIndex;
	private final int heightDifference = 30;
	private static JFrame f;
	private final List<Triple<JLabel, JTextField, JButton>> inputs = new LinkedList<Triple<JLabel, JTextField, JButton>>();
	private static JApplet applet;
	private JPanel panel;

	/**
	 * Create the applet.
	 */
	public CloneDiffusion() {
		this.dataIndex = 0;
	}

	public CloneDiffusion(Integer num) {
		this.dataIndex = num;
	}

	@Override
	public void init() {
		GUIUtilities.initLookAndFeel("Nimbus");
		panel = new JPanel();
		getContentPane().setPreferredSize(new Dimension((int) (Math.random() * 2), 2));
		getContentPane().setLayout(null);
		getContentPane().setBackground(panel.getBackground());

		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(510, (dataIndex + 1) * heightDifference));
		initInputDirectory(dataIndex);
		if (dataIndex > 9) {
			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(6, 37, 500, 305);
			scrollPane.setAutoscrolls(true);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			getContentPane().add(scrollPane);
		} else {
			panel.setBounds(6, 37, 500, (dataIndex + 1) * heightDifference);
			getContentPane().add(panel);
		}

		chckbxBedpostx = new JCheckBox("Bedpostx");
		chckbxBedpostx.setBounds(6, 76 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 80, 18);
		getContentPane().add(chckbxBedpostx);
		JButton btnNewButton = new JButton("Go");
		btnNewButton.setBounds(364, 103 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 45, 27);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// int preferredNumberOfConcurrentProcess =
				// Integer.parseInt(textField.getText());
				int preferredNumberOfConcurrentProcess = 2; // Testing

				Deque<Pair<String, String[]>> listOfInputs = new LinkedList<Pair<String, String[]>>();
				List<Thread> listOfObserverThreads = new LinkedList<Thread>();
				for (int i = 0; i <= dataIndex; i++) {
					String inputData = inputs.get(i).getB().getText();
					populateInputListForProcessing(inputData, i, listOfInputs);
				}
				int numberOfConcurrentProcess = GUIUtilities.getNumberOfConcurrentProcess(listOfInputs.size(), preferredNumberOfConcurrentProcess);
				for (int i = 0; i < numberOfConcurrentProcess; i++) {
					listOfObserverThreads.add(null);
				}
				do {
					for (int i = 0; i < numberOfConcurrentProcess; i++) {
						Thread t = listOfObserverThreads.get(i);
						if (!listOfInputs.isEmpty()) {
							if (t == null) {
								addThread(i, listOfInputs, listOfObserverThreads);
								continue;
							} else if ((!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								addThread(i, listOfInputs, listOfObserverThreads);
								continue;
							}
						} else {
							if (t != null && (!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								listOfObserverThreads.remove(i);
								listOfObserverThreads.add(i, null);
							}
						}
					}
				} while (!isObserversDead(listOfObserverThreads));

			}

			private <T> boolean isObserversDead(List<T> list) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null) {
						return false;
					}
				}
				return true;
			}

			private void addThread(int index, Deque<Pair<String, String[]>> listOfInputs, List<Thread> listOfObserverThreads) {
				final Thread execute = GUIUtilities.createExecutingThread(listOfInputs.removeFirst().getB());
				execute.start();
				Thread observer = GUIUtilities.createObserverThread(execute, listOfInputs.removeFirst().getA());
				observer.start();
				listOfObserverThreads.remove(index);
				listOfObserverThreads.add(index, observer);
			}

			private void populateInputListForProcessing(String inputData, final int index, Deque<Pair<String, String[]>> listOfInputs) {
				if (inputData.trim().isEmpty()) {

					return;
				}

				if (!GUIUtilities.checkInput(inputData, ".nii.gz") && !GUIUtilities.checkInput(inputData, "nii")) {
					Thread t = new Thread(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Input " + (index + 1) + ": " + "this is not an image file!");
						}
					});
					t.start();

					return;
				}

				try {
					String workingdir = GUIUtilities.getWorkingDirectory(inputData);
					String bvecs = getTextFile(workingdir, "bvecs");
					String bval = getTextFile(workingdir, "bval");
					String[] args = { inputData, String.valueOf(chckbxBedpostx.isSelected()), workingdir, bvecs, bval };

					String script = "'cd ../ScriptsRunByGUI; " + "./DiffusionPreprocessingScripts.sh ";
					for (int i = 0; i < args.length; i++) {
						script += args[i];
						if (i < args.length - 1) {
							script += " ";
						} else {
							script += ";'";
						}
					}
					String[] cmdArray = { "gnome-terminal", "--disable-factory", "-e", "bash -c " + script };
					listOfInputs.add(new Pair<String, String[]>(inputData, cmdArray));
				} catch (NoSuchFileException e) {

					final String message = e.getMessage();
					// Create an error pop up
					Thread t = new Thread(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Input " + (index + 1) + ": " + message + " not found!");
						}
					});
					t.start();

					return;

				} catch (DoubleOccurrenceException e) {

					final String message = e.getMessage();
					// Create an error pop up
					Thread t = new Thread(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Input " + (index + 1) + ": " + "Multiple occurrence of " + message);
						}
					});
					t.start();

					return;

				}
			}

			private String getTextFile(String workingdir, String filename) throws NoSuchFileException {

				List<String> possibleFiles = GUIUtilities.searchFile(workingdir, "*" + filename + "*");
				if (possibleFiles.isEmpty()) {
					throw new NoSuchFileException(filename);
				} else if (possibleFiles.size() > 1) {
					throw new DoubleOccurrenceException(filename);
				} else {
					return possibleFiles.get(0);
				}
			}

		});
		getContentPane().add(btnNewButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(420, 103 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 70, 27);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		getContentPane().add(btnCancel);

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(266, 104 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 88, 25);
		getContentPane().add(btnHelp);

		JLabel lblNumberOfDatasets = new JLabel("Number of datasets");
		lblNumberOfDatasets.setBounds(6, 12, 171, 15);
		getContentPane().add(lblNumberOfDatasets);

		final JSlider slider = new JSlider();
		slider.setMinimum(1);
		slider.setMaximum(100);
		slider.setBounds(154, 12, 200, 16);
		slider.setValue(dataIndex + 1);
		getContentPane().add(slider);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				textField_1.setText(String.valueOf(slider.getValue()));
			}

		});

		textField_1 = new JTextField();
		textField_1.setBounds(362, 8, 50, 27);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setText(String.valueOf(slider.getValue()));
		textField_1.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField) input;
				if (tf.getText().isEmpty()) {
					tf.setText(String.valueOf(slider.getValue()));
					return true;
				}
				try {
					Integer.parseInt(tf.getText());
					return true;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Please type an integer, thanks!");
					return false;
				}
			}

		});
		textField_1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				String typed = textField_1.getText();
				try {
					int x = Integer.parseInt(typed);
					if (!typed.matches("\\d+") || typed.length() > 3 || x > 10 || x < 1) {
						return;
					}
					int value = Integer.parseInt(typed);
					slider.setValue(value);
				} catch (NumberFormatException e) {

				}

			}

		});

		Icon icn = UIManager.getIcon("OptionPane.questionIcon");

		BufferedImage image = new BufferedImage(icn.getIconWidth(), icn.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		icn.paintIcon(null, image.getGraphics(), 0, 0);

		ImageIcon icon = new ImageIcon(image.getScaledInstance(23, 23, Image.SCALE_AREA_AVERAGING));

		JButton lblquestion = new JButton(icon);
		lblquestion.setBounds(420, 8, 22, 22);
		lblquestion.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblquestion);
		lblquestion.setToolTipText(dummyToolTip);

		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataIndex = Integer.parseInt(textField_1.getText()) - 1;

				f.setBounds(400, 100, 510, 136 + GUIUtilities.increaseByHeight(dataIndex, heightDifference));

				applet = new CloneDiffusion(dataIndex);

				applet.init();
				f.setContentPane(applet.getContentPane());

			}
		});
		btnGo.setBounds(451, 7, 50, 25);
		getContentPane().add(btnGo);

	}

	private void initInputDirectory(final int index) {
		for (int i = 0; i <= index; i++) {
			final int x = i;
			inputs.add(new Triple<JLabel, JTextField, JButton>(new JLabel("Input data " + (i + 1)), new JTextField(), new JButton("Open")));
			inputs.get(i).getA().setBounds(6, 7 + i * heightDifference, 111, 15);
			panel.add(inputs.get(i).getA());
			inputs.get(i).getB().setBounds(100, 1 + i * heightDifference, 308, 27);
			panel.add(inputs.get(i).getB());
			inputs.get(i).getB().setColumns(10);
			inputs.get(i).getC().setBounds(415, 2 + i * heightDifference, 61, 27);
			inputs.get(i).getC().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					buildLoadDiag(x);
				}
			});
			panel.add(inputs.get(i).getC());
		}
	}

	private void buildLoadDiag(int index) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);

		int returnVal = fileChooser.showOpenDialog(new JFrame("load"));

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// This is where a real application would open the file.
			// JOptionPane.showMessageDialog(frame,"Opening: " +
			// file.getName() + ".");
			String filePath = file.getPath();
			inputs.get(index).getB().setText(filePath);
		} else {
			// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
		}
	}

	public static void main(String[] args) {
		f = new JFrame();
		applet = new CloneDiffusion();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 100, 510, 136);
		f.setTitle("Diffusion Pre-processing");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
