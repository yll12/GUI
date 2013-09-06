package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import sun.java2d.xr.MutableInteger;
import utils.GUIUtilities;
import utils.NoSuchFileException;
import utils.Pair;
import utils.Triple;

@SuppressWarnings("serial")
public class DiffusionPreprocessingManualChooser extends JApplet {
	private JCheckBox chckbxBedpostx;
	private JCheckBox chckbxEddyCorrect;
	private JTextField textField_numberOfDatasets;
	private static int dataIndex;
	private final static int heightDifference = 30;
	private static JFrame f;
	private final List<Triple<JLabel, JTextField, JButton>> inputs = new LinkedList<Triple<JLabel, JTextField, JButton>>();
	private List<String> textFields = new ArrayList<String>();
	private static JApplet applet;
	private JPanel panel;
	private JTextField textField_numberOfConcurrentProcess;
	private final String questionToolTip = "Specify the total number of data(Click for more details)";
	private final String dataToolTip = "Specify the total number of data";
	private final String inputToolTip = "Specify the full path to the image file. Check data - ensure number of volumes are equal to number of "
			+ "bvec or bval entries, i.e: no trace image.";
	private final String clearToolTip = "Clear all inputs";
	private final String openToolTip = "Open File";
	private final String go1ToolTip = "Click to set the total number of data to the specified number";
	private final String go2ToolTip = "Starts Diffusion Pre-processing";
	private final String helpToolTip = "Help";
	private final String cancelToolTip = "Cancel";
	private final String switchViewToolTip = "This view automatically allocates datas";
	private final String bedpostxToolTip = "Enable this to run bedpostx process";
	private final String eddyCorrectToolTip = "Enable this to run eddy_correct process";
	private final String processToolTip = "Specify the number of process to be run at the same time";

	/**
	 * Create the applet.
	 */
	public DiffusionPreprocessingManualChooser() {
		dataIndex = 0;
	}

	public DiffusionPreprocessingManualChooser(Integer num, List<String> textfields) {
		dataIndex = num;
		this.textFields = textfields;
		GUIUtilities.rearrangeList(textFields);
	}

	@Override
	public void init() {
		GUIUtilities.initLookAndFeel("Nimbus");
		panel = new JPanel();
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
		chckbxBedpostx.setToolTipText(bedpostxToolTip);
		getContentPane().add(chckbxBedpostx);

		chckbxEddyCorrect = new JCheckBox("Eddy_correct");
		chckbxEddyCorrect.setBounds(6, 106 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 150, 18);
		chckbxEddyCorrect.setToolTipText(eddyCorrectToolTip);
		getContentPane().add(chckbxEddyCorrect);

		JLabel lblNumberOfConcurrentProcess = new JLabel("Number of Concurrent Process");
		lblNumberOfConcurrentProcess.setBounds(8, 130 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 200, 25);
		lblNumberOfConcurrentProcess.setToolTipText(processToolTip);
		getContentPane().add(lblNumberOfConcurrentProcess);

		textField_numberOfConcurrentProcess = new JTextField();
		textField_numberOfConcurrentProcess.setColumns(5);
		textField_numberOfConcurrentProcess.setBounds(206, 130 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 40, 27);
		textField_numberOfConcurrentProcess.setText("4");
		textField_numberOfConcurrentProcess.setToolTipText(processToolTip);
		getContentPane().add(textField_numberOfConcurrentProcess);

		JButton btnStartProcess = new JButton("Go");
		btnStartProcess.setBounds(364, 163 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 45, 27);
		btnStartProcess.setToolTipText(go2ToolTip);
		btnStartProcess.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int preferredNumberOfConcurrentProcess;
				try {
					preferredNumberOfConcurrentProcess = Integer.parseInt(textField_numberOfConcurrentProcess.getText());
					if (preferredNumberOfConcurrentProcess <= 0) {
						JOptionPane.showMessageDialog(null, "The number of concurrent process must be positive integer");
						return;
					}
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Please specify the number of concurrent process(must be positive integer)");
					return;
				}

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
								GUIUtilities.addThread(i, listOfInputs, listOfObserverThreads);
								continue;
							} else if ((!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								GUIUtilities.addThread(i, listOfInputs, listOfObserverThreads);
								continue;
							}
						} else {
							if (t != null && (!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								listOfObserverThreads.remove(i);
								listOfObserverThreads.add(i, null);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
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

			private void populateInputListForProcessing(String inputData, final int index, Deque<Pair<String, String[]>> listOfInputs) {
				if (inputData.trim().isEmpty()) {

					return;
				}

				if (!GUIUtilities.checkInput(inputData, ".nii.gz") && !GUIUtilities.checkInput(inputData, ".nii")) {
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
					String bvecs = getTextFile(workingdir, "bvec", index);
					String bval = getTextFile(workingdir, "bval", index);
					String[] args =
							{ inputData, String.valueOf(chckbxBedpostx.isSelected()), workingdir, bvecs, bval,
									String.valueOf(chckbxEddyCorrect.isSelected()) };

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

				}

			}

			private String getTextFile(String workingdir, String filename, int index) throws NoSuchFileException {

				List<String> possibleFiles = GUIUtilities.searchFileWithoutFullPath(workingdir, "*" + filename + "*");
				if (possibleFiles.isEmpty()) {
					throw new NoSuchFileException(filename);
				} else if (possibleFiles.size() > 1) {
					return GUIUtilities.askUserForText(possibleFiles, index, filename);
				} else {
					return possibleFiles.get(0);
				}
			}

		});
		getContentPane().add(btnStartProcess);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(420, 163 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 70, 27);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnCancel.setToolTipText(cancelToolTip);
		;
		getContentPane().add(btnCancel);

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(286, 164 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 66, 25);
		getContentPane().add(btnClear);
		btnClear.setToolTipText(clearToolTip);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (Triple<JLabel, JTextField, JButton> input : inputs) {
					input.getB().setText(null);
				}
			}

		});

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(206, 164 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 66, 25);
		getContentPane().add(btnHelp);
		btnHelp.setToolTipText(helpToolTip);
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JApplet applet = new JApplet() {
					@Override
					public void init() {
						GUIUtilities.initLookAndFeel("Nimbus");
						Container content = getContentPane();
						content.setLayout(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();
						GUIUtilities.initializeConstraints(c);
						MutableInteger lineNumber = new MutableInteger(0);
						GUIUtilities.createLine(lineNumber, content, c, "Number of datasets : Total number of image data to be processed."
								+ " Specify the number either using the text field or the slider and click Go. ");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Input data : Specify the full path to the image file either by typing it or using the Open button");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Bedpostx : Check this box to allow bedpostx" + " for all image files");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Number of Concurrent Process : Specify the number of process to be run "
								+ "at any given time(default as 4)");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Switch View to Auto : A convenient way to find all the image data by specifying "
										+ "the parent folder of all the image files");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Clear : Clear all input data text fields");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Go : Starts the Diffusion preprocessing");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "**NOTE: Check data - ensure number of volumes are equal to number of "
								+ "bvec or bval entries, i.e: no trace image.");
					}
				};
				applet.init();
				frame.setContentPane(applet.getContentPane());
				frame.setBounds(getX(), getY(), 50 + (int) frame.getPreferredSize().getWidth(), 50 + (int) frame.getPreferredSize().getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setTitle("Diffusion Pre-processing Help");

				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});
		JButton btnSwitch = new JButton("Switch View to Auto");
		btnSwitch.setBounds(6, 164 + GUIUtilities.increaseByHeight(dataIndex, heightDifference), 150, 25);
		getContentPane().add(btnSwitch);
		btnSwitch.setToolTipText(switchViewToolTip);
		btnSwitch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null, "Switch to Auto file chooser?", "Switch view", JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					try {
						Runtime.getRuntime().exec("java -jar DiffusionAuto.jar");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}

		});

		JLabel lblNumberOfDatasets = new JLabel("Number of datasets");
		lblNumberOfDatasets.setBounds(6, 12, 151, 15);
		lblNumberOfDatasets.setToolTipText(dataToolTip);
		getContentPane().add(lblNumberOfDatasets);

		final JSlider slider = new JSlider();
		slider.setMinimum(1);
		slider.setMaximum(100);
		slider.setToolTipText(dataToolTip);
		slider.setBounds(154, 12, 200, 16);
		slider.setValue(dataIndex + 1);
		getContentPane().add(slider);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				textField_numberOfDatasets.setText(String.valueOf(slider.getValue()));
			}

		});

		textField_numberOfDatasets = new JTextField();
		textField_numberOfDatasets.setBounds(362, 8, 50, 27);
		getContentPane().add(textField_numberOfDatasets);
		textField_numberOfDatasets.setColumns(10);
		textField_numberOfDatasets.setToolTipText(dataToolTip);
		textField_numberOfDatasets.setText(String.valueOf(slider.getValue()));
		textField_numberOfDatasets.setInputVerifier(new InputVerifier() {

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
		textField_numberOfDatasets.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				String typed = textField_numberOfDatasets.getText();
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
		lblquestion.setToolTipText(questionToolTip);
		lblquestion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JApplet applet = new JApplet() {
					@Override
					public void init() {
						GUIUtilities.initLookAndFeel("Nimbus");
						Container content = getContentPane();
						content.setLayout(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();
						JLabel line1 = new JLabel("Type in any number of data to process in the given text field");
						GUIUtilities.setFont(line1);
						c.gridx = 0;
						c.gridy = 0;
						content.add(line1, c);
						JLabel line2 =
								new JLabel("Alternatively, use the slider for convinience, but note that"
										+ " there's no restriction on maximum number of data");
						GUIUtilities.setFont(line2);
						c.gridy = 1;
						content.add(line2, c);
						JLabel line3 = new JLabel("Click go to proceed to increasing/decreasing the total number of data");
						c.gridy = 2;
						GUIUtilities.setFont(line3);
						content.add(line3, c);

					}
				};
				applet.init();
				frame.setContentPane(applet.getContentPane());
				frame.setBounds(getX(), getY(), 50 + (int) frame.getPreferredSize().getWidth(), 50 + (int) frame.getPreferredSize().getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setTitle("Diffusion Pre-processing Help");

				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});

		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataIndex = Integer.parseInt(textField_numberOfDatasets.getText()) - 1;

				f.setSize(510, 226 + GUIUtilities.increaseByHeight(dataIndex, heightDifference));
				for (int i = 0; i < inputs.size(); i++) {
					String inputText = inputs.get(i).getB().getText();
					if (i < textFields.size()) {
						textFields.set(i, inputText);
					} else {
						textFields.add(inputText);
					}
				}
				applet = new DiffusionPreprocessingManualChooser(dataIndex, textFields);

				applet.init();
				f.setContentPane(applet.getContentPane());

			}
		});
		btnGo.setBounds(451, 7, 50, 25);
		btnGo.setToolTipText(go1ToolTip);
		getContentPane().add(btnGo);

	}

	private void initInputDirectory(final int index) {
		for (int i = 0; i <= index; i++) {
			final int x = i;
			/*
			 * ImageIcon icon = new ImageIcon("../resources/openFolder.jpg");
			 * BufferedImage image = new BufferedImage(icon.getIconWidth(),
			 * icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			 * icon.paintIcon(null, image.getGraphics(), 0, 0); icon = new
			 * ImageIcon(image.getScaledInstance(25, 25,
			 * Image.SCALE_AREA_AVERAGING));
			 */
			inputs.add(new Triple<JLabel, JTextField, JButton>(new JLabel("Input data " + (i + 1)), new JTextField(), new JButton("Open")));
			JLabel inputLabel = inputs.get(i).getA();
			final JTextField inputText = inputs.get(i).getB();
			JButton inputButton = inputs.get(i).getC();
			inputLabel.setToolTipText(inputToolTip);
			inputText.setToolTipText(inputToolTip);
			inputButton.setToolTipText(openToolTip);
			inputLabel.setBounds(6, 7 + i * heightDifference, 111, 15);
			panel.add(inputLabel);
			inputText.setBounds(100, 1 + i * heightDifference, 308, 27);
			if (i < textFields.size()) {
				inputText.setText(textFields.get(i));
			}
			panel.add(inputText);
			inputText.setColumns(10);
			// inputButton.setBounds(415, 2 + i * heightDifference, 25, 25);
			inputButton.setBounds(415, 2 + i * heightDifference, 61, 27);
			inputButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					buildLoadDiag(x);
				}
			});
			panel.add(inputButton);
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
		if (args.length == 0) {
			applet = new DiffusionPreprocessingManualChooser();
		} else {
			List<String> textField = new LinkedList<String>();
			for (int i = 0; i < args.length; i++) {
				textField.add(args[i]);
				applet = new DiffusionPreprocessingManualChooser(textField.size() - 1, textField);
			}
		}

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 100, 510, 226 + (dataIndex <= 9 ? (dataIndex * heightDifference) : (9 * heightDifference)));
		f.setTitle("Diffusion Pre-processing(Manual)");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
