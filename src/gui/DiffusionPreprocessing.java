package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import utils.GUIUtilities;
import utils.NoSuchFileException;

@SuppressWarnings("serial")
public class DiffusionPreprocessing extends JApplet {
	private JTextField textField;
	private JCheckBox chckbxBedpostx;

	/**
	 * Create the applet.
	 */
	public DiffusionPreprocessing() {

		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
		getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Input directory");
		lblNewLabel.setBounds(6, 12, 91, 15);
		getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(103, 6, 307, 27);
		getContentPane().add(textField);
		textField.setColumns(10);
		JButton btnOpen = new JButton("Open");
		btnOpen.setBounds(430, 6, 61, 27);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buildLoadDiag();
			}
		});
		getContentPane().add(btnOpen);

		chckbxBedpostx = new JCheckBox("Bedpostx");
		chckbxBedpostx.setBounds(6, 44, 80, 18);
		getContentPane().add(chckbxBedpostx);

		final JFrame errors = new JFrame();
		JButton btnNewButton = new JButton("Go");
		btnNewButton.setBounds(365, 72, 45, 27);
		errors.pack();
		errors.setVisible(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String inputData = textField.getText();

				if (inputData.trim().isEmpty()) {

					JOptionPane.showMessageDialog(errors, "No input specified");

					return;
				}

				if (!GUIUtilities.checkInput(inputData, ".nii.gz")) {

					JOptionPane.showMessageDialog(errors, "This is not an image file!");
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
							script += "; read'";
						}
					}
					String[] cmdArray = { "gnome-terminal", "-e", "bash -c " + script };

					Runtime.getRuntime().exec(cmdArray);
				} catch (NoSuchFileException e) {

					// Create an error pop up
					JOptionPane.showMessageDialog(errors, e.getMessage() + " not found!");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private String getTextFile(String workingdir, String filename) throws NoSuchFileException {

				List<String> possibleFiles = GUIUtilities.searchFile(workingdir, "*" + filename + "*");
				if (possibleFiles.isEmpty()) {
					throw new NoSuchFileException(filename);
				} else {
					return possibleFiles.get(0);
				}
			}

		});
		getContentPane().add(btnNewButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(421, 72, 70, 27);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		getContentPane().add(btnCancel);

	}

	private void buildLoadDiag() {
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
			textField.setText(filePath);
		} else {
			// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
		}
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame();
		JApplet applet = new DiffusionPreprocessing();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 100, 500, 136);
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
