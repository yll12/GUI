package gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import sun.java2d.xr.MutableInteger;
import utils.GUIUtilities;

@SuppressWarnings("serial")
public class CorticalAnalysisAutoChooser extends JApplet {

	private static JApplet applet;
	private JTextField textField_1;
	private static JFrame f;
	private JTextField textField_fileToSearch;
	private int numOfFiles = 0;
	private final int heightDifference = 30;
	private List<String> texts = new LinkedList<String>();
	private List<JTextField> textfields = new LinkedList<JTextField>();
	private JTextField textField_age;
	private final String inputToolTip = "Specify the parent directory that contains all the image files";
	private final String ageToolTip = "Specify age text file(Contains ages by line)";
	private final String openToolTip = "Open File";
	private final String nextToolTip = "Search for all image files that matches the file name to search and populates it in manual view";
	private final String helpToolTip = "Help";
	private final String cancelToolTip = "Cancel";
	private final String switchViewToolTip = "This view allows manual selection of image files";
	private final String addBtnToolTip = "Add more file name to search";
	private final String fileNameToolTip = "Specifiy the name pattern of image files to search for";

	/**
	 * Create the applet.
	 */
	public CorticalAnalysisAutoChooser() {
	}

	public CorticalAnalysisAutoChooser(int numOfFiles, List<String> texts) {
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
		lblInputDirectory.setToolTipText(inputToolTip);
		getContentPane().add(lblInputDirectory);

		textField_1 = new JTextField();
		textField_1.setBounds(115, 8, 300, 27);
		textField_1.setColumns(30);
		textField_1.setToolTipText(inputToolTip);
		getContentPane().add(textField_1);

		JButton btnOpenButton = new JButton("Open");
		btnOpenButton.addActionListener(new ActionListener() {
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
		btnOpenButton.setToolTipText(openToolTip);
		btnOpenButton.setBounds(431, 7, 61, 27);

		getContentPane().add(btnOpenButton);
		JLabel lblAgeFile = new JLabel("Age file");
		lblAgeFile.setBounds(8, 44, 105, 15);
		lblAgeFile.setToolTipText(ageToolTip);
		getContentPane().add(lblAgeFile);

		textField_age = new JTextField();
		textField_age.setBounds(115, 40, 300, 27);
		textField_age.setColumns(30);
		textField_age.setToolTipText(ageToolTip);
		getContentPane().add(textField_age);

		JButton btnAgeOpenButton = new JButton("Open");
		btnAgeOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int returnVal = fileChooser.showOpenDialog(new JFrame("load"));

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// This is where a real application would open the file.
					// JOptionPane.showMessageDialog(frame,"Opening: " +
					// file.getName() + ".");
					String filePath = file.getPath();
					textField_age.setText(filePath);
				} else {
					// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
				}
			}
		});
		btnAgeOpenButton.setToolTipText(openToolTip);
		btnAgeOpenButton.setBounds(431, 39, 61, 27);
		getContentPane().add(btnAgeOpenButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setToolTipText(cancelToolTip);
		btnCancel.setBounds(411, 148 + numOfFiles * heightDifference, 81, 25);
		getContentPane().add(btnCancel);

		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
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
					String ageFileLocation = textField_age.getText();
					if (!ageFileLocation.trim().isEmpty()) {
						arguments += " " + ageFileLocation;
					} else {
						arguments += " NotSpecified";
					}
					Runtime.getRuntime().exec("java -jar CorticalAnalysisManual.jar" + arguments);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		btnNext.setToolTipText(nextToolTip);
		btnNext.setBounds(338, 148 + numOfFiles * heightDifference, 66, 25);
		getContentPane().add(btnNext);

		JButton btnSwitchView = new JButton("Switch View to Manual");
		btnSwitchView.setBounds(8, 148 + numOfFiles * heightDifference, 194, 25);
		btnSwitchView.setToolTipText(switchViewToolTip);
		getContentPane().add(btnSwitchView);
		btnSwitchView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null, "Switch to Manual file chooser?", "Switch view", JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					try {
						Runtime.getRuntime().exec("java -jar CorticalAnalysisManual.jar");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}

		});

		if (texts.isEmpty()) {
			createFile(0, "");
		} else {
			int size = texts.size();
			for (int i = 0; i < numOfFiles + 1; i++) {
				if (i < size) {
					createFile(i, texts.get(0));
					texts.remove(0);
				} else {
					createFile(i, "");
				}
			}
		}

		final JButton btnAddMoreFiles = new JButton("Add more files to search");
		if (numOfFiles == 4) {
			btnAddMoreFiles.setEnabled(false);
		}
		btnAddMoreFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (numOfFiles < 4) {
					numOfFiles++;
					f.setSize(510, 208 + numOfFiles * heightDifference);
					for (int i = 0; i < textfields.size(); i++) {
						texts.add(textfields.get(i).getText());
					}
					applet = new CorticalAnalysisAutoChooser(numOfFiles, texts);
					applet.init();
					f.setContentPane(applet.getContentPane());
				}
			}
		});
		btnAddMoreFiles.setBounds(8, 79, 187, 25);
		btnAddMoreFiles.setToolTipText(addBtnToolTip);
		getContentPane().add(btnAddMoreFiles);

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(267, 148 + numOfFiles * heightDifference, 61, 25);
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
						GUIUtilities.createLine(lineNumber, content, c,
								"Input directory : Specify the parent folder of all the image files to be processed.");
						GUIUtilities.createLine(lineNumber, content, c,
								"       Example : image to process are : 1) /staff/yl13/Testing/NNU123/NNU123_T2.nii.gz");
						GUIUtilities.createLine(lineNumber, content, c, "                              "
								+ "                              2) /staff/yl13/Testing/NNU456/NNU456_T2.nii.gz");
						GUIUtilities.createLine(lineNumber, content, c, "                              "
								+ "                              3) /staff/yl13/Testing/NNU789/NNU789_T2.nii.gz");
						GUIUtilities.createLine(lineNumber, content, c, "                              "
								+ "                              4) /staff/yl13/Testing/NNU1000/NNU1000_T2.nii.gz");
						GUIUtilities.createLine(lineNumber, content, c,
								"       Specifying /staff/yl13/Testing in input directory will search for these files"
										+ " and populates them at manual view.");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Age file : Specify any text file that contains the age information.");
						GUIUtilities.createLine(lineNumber, content, c, "       Example : Using the above example, specify a text file, such as "
								+ "staff/yl13/Testing/Age, in which opening it will look like : ");
						GUIUtilities.createLine(lineNumber, content, c, "                       28");
						GUIUtilities.createLine(lineNumber, content, c, "                       34");
						GUIUtilities.createLine(lineNumber, content, c, "                       44");
						GUIUtilities.createLine(lineNumber, content, c, "                       37");
						GUIUtilities.createLine(lineNumber, content, c, "       in which every new line corresponding to the age at scan.");
						GUIUtilities.createLine(lineNumber, content, c,
								"       Emptying this field will default all age to 36 at manual view, which can be "
										+ "further modified manually at the manual view.");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"File name to search : Specify the pattern of the name of the file to search, "
										+ "in which * matches everything(so *T2.* will matches everything that contains \"T2.\" in the file name.");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Switch View to Manual : In this view, images and the"
								+ " corresponding age at scan can be manually selected.");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Next : Proceed to the manual view, which populates all the input image files "
										+ "and age at scan for a final check before processing.");
					}
				};
				applet.init();
				frame.setContentPane(applet.getContentPane());
				frame.setBounds(getX(), getY(), 50 + (int) frame.getPreferredSize().getWidth(), 50 + (int) frame.getPreferredSize().getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setTitle("Cortical Analysis(Auto) Help");

				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});

	}

	private void createFile(int index, String textToSet) {
		JLabel lblFileName_1 = new JLabel("File name to search (" + (index + 1) + ")");
		lblFileName_1.setToolTipText(fileNameToolTip);
		textField_fileToSearch = new JTextField();
		textField_fileToSearch.setColumns(20);
		textField_fileToSearch.setText(textToSet);
		textField_fileToSearch.setToolTipText(fileNameToolTip);
		textfields.add(textField_fileToSearch);
		JPanel panel1 = new JPanel();
		panel1.add(lblFileName_1);
		panel1.add(textField_fileToSearch);
		panel1.setBounds(-61, 107 + index * heightDifference, 525, 39);
		getContentPane().add(panel1);
	}

	public static void main(String[] args) {
		f = new JFrame();
		applet = new CorticalAnalysisAutoChooser();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 400, 510, 208);
		f.setTitle("Cortical Analysis(Auto)");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
