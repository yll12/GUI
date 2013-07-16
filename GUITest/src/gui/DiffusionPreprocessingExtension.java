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
import java.io.IOException;
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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.DoubleOccurrenceException;
import utils.GUIUtilities;
import utils.NoSuchFileException;
import utils.Triple;

@SuppressWarnings("serial")
public class DiffusionPreprocessingExtension extends JApplet {
	private JCheckBox chckbxBedpostx;
	private JTextField textField_1;
	private final String dummyToolTip = "Hi There";
	private int dataIndex;
	private final int heightDifference = 30;
	private static JFrame f;
	private final List<Triple<JLabel, JTextField, JButton>> inputs =
			new LinkedList<Triple<JLabel, JTextField, JButton>>();
	private static JApplet applet;

	/**
	 * Create the applet.
	 */
	public DiffusionPreprocessingExtension() {
		this.dataIndex = 0;
	}

	public DiffusionPreprocessingExtension(Integer num) {
		this.dataIndex = num;
	}

	@Override
	public void init() {
		GUIUtilities.initLookAndFeel("Nimbus");
		getContentPane().setPreferredSize(
				new Dimension((int) (Math.random() * 2), 2));
		getContentPane().setLayout(null);

		initInputDirectory(dataIndex);

		chckbxBedpostx = new JCheckBox("Bedpostx");
		chckbxBedpostx.setBounds(6, 76 + dataIndex * heightDifference, 80, 18);
		getContentPane().add(chckbxBedpostx);

		final JFrame errors = new JFrame();
		errors.pack();
		errors.setVisible(false);
		JButton btnNewButton = new JButton("Go");
		btnNewButton.setBounds(364, 103 + dataIndex * heightDifference, 45, 27);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i <= dataIndex; i++) {
					String inputData = inputs.get(i).getB().getText();
					executePreprocessing(errors, inputData, i);
				}
			}

			private void executePreprocessing(final JFrame errors,
					String inputData, int index) {
				if (inputData.trim().isEmpty()) {

					JOptionPane.showMessageDialog(errors, "Input "
							+ (index + 1) + ": " + "No input specified");

					return;
				}

				if (!GUIUtilities.checkInput(inputData, ".nii.gz")) {

					JOptionPane
							.showMessageDialog(errors, "Input " + (index + 1)
									+ ": " + "this is not an image file!");
					return;
				}

				try {

					String workingdir =
							GUIUtilities.getWorkingDirectory(inputData);
					String bvecs = getTextFile(workingdir, "bvecs");
					String bval = getTextFile(workingdir, "bval");
					String[] args =
							{
									inputData,
									String.valueOf(chckbxBedpostx.isSelected()),
									workingdir, bvecs, bval };

					String script =
							"'cd ../ScriptsRunByGUI; "
									+ "./DiffusionPreprocessingScripts.sh ";
					for (int i = 0; i < args.length; i++) {
						script += args[i];
						if (i < args.length - 1) {
							script += " ";
						} else {
							script += "; read'";
						}
					}
					String[] cmdArray =
							{ "gnome-terminal", "-e", "bash -c " + script };

					Runtime.getRuntime().exec(cmdArray);
				} catch (NoSuchFileException e) {

					// Create an error pop up
					JOptionPane.showMessageDialog(errors, "Input "
							+ (index + 1) + ": " + e.getMessage()
							+ " not found!");

				} catch (DoubleOccurrenceException e) {

					// Create an error pop up
					JOptionPane.showMessageDialog(errors, "Input "
							+ (index + 1) + ": " + "Multiple occurrence of "
							+ e.getMessage());

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private String getTextFile(String workingdir, String filename)
					throws NoSuchFileException {

				List<String> possibleFiles =
						GUIUtilities.searchFile(workingdir, "*" + filename
								+ "*");
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
		btnCancel.setBounds(420, 103 + dataIndex * heightDifference, 70, 27);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		getContentPane().add(btnCancel);

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(266, 104 + dataIndex * heightDifference, 88, 25);
		getContentPane().add(btnHelp);

		JLabel lblNumberOfDatasets = new JLabel("Number of datasets");
		lblNumberOfDatasets.setBounds(6, 12, 171, 15);
		getContentPane().add(lblNumberOfDatasets);

		final JSlider slider = new JSlider();
		slider.setMinimum(1);
		slider.setMaximum(10);
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
					int x = Integer.parseInt(tf.getText());
					if (x <= 10 && x >= 1) {
						return true;
					}
					// else case :
					JOptionPane.showMessageDialog(errors,
							"Please type an integer between 1 to 10");
					return false;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(errors,
							"Please type an integer between 1 to 10, thanks!");
					return false;
				}
			}

		});
		textField_1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				String typed = textField_1.getText();
				if (!typed.matches("\\d+") || typed.length() > 3) {
					return;
				}
				int value = Integer.parseInt(typed);
				slider.setValue(value);
			}

		});

		Icon icn = UIManager.getIcon("OptionPane.questionIcon");

		BufferedImage image =
				new BufferedImage(icn.getIconWidth(), icn.getIconHeight(),
						BufferedImage.TYPE_INT_ARGB);

		icn.paintIcon(null, image.getGraphics(), 0, 0);

		ImageIcon icon =
				new ImageIcon(image.getScaledInstance(20, 20,
						Image.SCALE_AREA_AVERAGING));

		JLabel lblquestion = new JLabel(icon);
		lblquestion.setBounds(420, 8, 27, 27);
		lblquestion.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblquestion);
		lblquestion.setToolTipText(dummyToolTip);

		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataIndex = Integer.parseInt(textField_1.getText()) - 1;

				f.setBounds(400, 100, 510, 136 + dataIndex * heightDifference);

				applet = new DiffusionPreprocessingExtension(dataIndex);

				applet.init();
				f.setContentPane(applet.getContentPane());

			}
		});
		btnGo.setBounds(451, 7, 50, 25);
		getContentPane().add(btnGo);

	}

	private void initInputDirectory(final int index) {
		for (int i = 0; i <=  index; i++) {
			final int x = i;
			inputs.add(new Triple<JLabel, JTextField, JButton>(new JLabel(
					"Input data " + (i + 1)), new JTextField(), new JButton(
					"Open")));
			inputs.get(i).getA()
					.setBounds(6, 43 + i * heightDifference, 111, 15);
			getContentPane().add(inputs.get(i).getA());
			inputs.get(i).getB()
					.setBounds(90, 37 + i * heightDifference, 318, 27);
			getContentPane().add(inputs.get(i).getB());
			inputs.get(i).getB().setColumns(10);
			inputs.get(i).getC()
					.setBounds(415, 36 + i * heightDifference, 61, 27);
			inputs.get(i).getC().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					buildLoadDiag(x);
				}
			});
			getContentPane().add(inputs.get(i).getC());
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
		applet = new DiffusionPreprocessingExtension();

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
