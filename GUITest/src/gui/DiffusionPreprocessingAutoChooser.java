package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import utils.GUIUtilities;

@SuppressWarnings("serial")
public class DiffusionPreprocessingAutoChooser extends JApplet {

	private static JApplet applet;
	private JTextField textField_1;
	private static JFrame f;
	private JTextField textField_2;
	private int numOfFiles = 0;
	private final int heightDifference = 30;
	private List<String> texts = new LinkedList<String>();
	private List<JTextField> textfields = new LinkedList<JTextField>();

	/**
	 * Create the applet.
	 */
	public DiffusionPreprocessingAutoChooser() {
	}

	public DiffusionPreprocessingAutoChooser(int numOfFiles, List<String> texts) {
		this.numOfFiles = numOfFiles;
		this.texts = texts;
	}

	@Override
	public void init() {
		GUIUtilities.initLookAndFeel("Nimbus");
		JPanel panel = new JPanel();
		getContentPane().setLayout(null);
		getContentPane().setBackground(panel.getBackground());

		JLabel lblInputDirectory = new JLabel("Input directory");
		lblInputDirectory.setBounds(8, 12, 105, 15);
		getContentPane().add(lblInputDirectory);

		textField_1 = new JTextField();
		textField_1.setBounds(115, 8, 300, 27);
		textField_1.setColumns(30);
		getContentPane().add(textField_1);

		JButton btnNewButton = new JButton("Open");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fileChooser.showOpenDialog(new JFrame("load"));

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// This is where a real application would open the file.
					// JOptionPane.showMessageDialog(frame,"Opening: " +
					// file.getName() + ".");
					String filePath = file.getPath();
					textField_1.setText(filePath);
				} else {
					// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
				}
			}
		});
		btnNewButton.setBounds(431, 7, 61, 27);
		getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnNewButton_1.setBounds(411, 146 + numOfFiles * heightDifference, 81, 25);
		getContentPane().add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("Next");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String inputDir = textField_1.getText();
					List<String> fileNameToLook = new LinkedList<String>();
					for (int i = 0; i < textfields.size(); i++) {
						fileNameToLook.add(textfields.get(i).getText());
					}
					String arguments = "";
					for (int i = 0; i < fileNameToLook.size(); i++) {
						String pattern = fileNameToLook.get(i);
						if (pattern.trim().isEmpty()) {
							continue;
						}
						List<String> inputs = GUIUtilities.searchAllFile(inputDir, pattern);
						if (!inputs.isEmpty()) {
							arguments += " ";
							for (int j = 0; j < inputs.size(); j++) {
								arguments += inputs.get(j);
								if (j < inputs.size() - 1) {
									arguments += " ";
								}
							}
						}

					}
					Runtime.getRuntime().exec("java -jar DiffusionManual.jar" + arguments);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		btnNewButton_2.setBounds(328, 146 + numOfFiles * heightDifference, 76, 25);
		getContentPane().add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("Switch View to Manual");
		btnNewButton_3.setBounds(8, 146 + numOfFiles * heightDifference, 194, 25);
		getContentPane().add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null, "Switch to Manual file chooser?", "Switch view", JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					try {
						Runtime.getRuntime().exec("java -jar DiffusionManual.jar");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}

		});

		if (texts.isEmpty()) {
			createFile(0, "unreg-data.nii.gz");
			createFile(1, "unreg-data.nii");
		} else {
			int size = texts.size();
			for (int i = 0; i < numOfFiles + 2; i++) {
				if (i < size) {
					createFile(i, texts.get(0));
					texts.remove(0);
				} else {
					createFile(i, "");
				}
			}
		}

		final JButton btnAddMoreFiles = new JButton("Add more files");
		if (numOfFiles == 3) {
			btnAddMoreFiles.setEnabled(false);
		}
		btnAddMoreFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (numOfFiles < 3) {
					numOfFiles++;
					f.setSize(510, 205 + numOfFiles * heightDifference);
					for (int i = 0; i < textfields.size(); i++) {
						texts.add(textfields.get(i).getText());
					}
					applet = new DiffusionPreprocessingAutoChooser(numOfFiles, texts);
					applet.init();
					f.setContentPane(applet.getContentPane());
				}
			}
		});
		btnAddMoreFiles.setBounds(8, 47, 117, 25);
		getContentPane().add(btnAddMoreFiles);

	}

	private void createFile(int index, String textToSet) {
		JLabel lblFileName_1 = new JLabel("File name " + (index + 1));
		textField_2 = new JTextField();
		textField_2.setColumns(20);
		textField_2.setText(textToSet);
		textfields.add(textField_2);
		JPanel panel1 = new JPanel();
		panel1.add(lblFileName_1);
		panel1.add(textField_2);
		panel1.setBounds(-71, 75 + index * heightDifference, 475, 39);
		getContentPane().add(panel1);
	}

	public static void main(String[] args) {
		f = new JFrame();
		applet = new DiffusionPreprocessingAutoChooser();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 100, 510, 205);
		f.setTitle("Diffusion Pre-processing(Auto)");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
