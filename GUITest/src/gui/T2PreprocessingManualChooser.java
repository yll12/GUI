package gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

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

import utils.GUIUtilities;

@SuppressWarnings("serial")
public class T2PreprocessingManualChooser extends JApplet {
	private JTextField textField_1;
	private JTextField textField;
	private final String dummyToolTip = "JUST TYPE ANY AGE!";

	/**
	 * Create the applet.
	 */
	public T2PreprocessingManualChooser() {
		GUIUtilities.initLookAndFeel("Nimbus");
		getContentPane().setLayout(null);

		JLabel lblInputDirectory = new JLabel("Input directory");
		lblInputDirectory.setBounds(6, 12, 105, 15);
		getContentPane().add(lblInputDirectory);

		textField = new JTextField();
		textField.setBounds(123, 8, 293, 27);
		getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildLoadDiag(0);
			}
		});
		btnOpen.setBounds(427, 7, 72, 25);
		getContentPane().add(btnOpen);

		final JSlider slider = new JSlider();
		slider.setBounds(150, 46, 214, 15);
		slider.setMaximum(44);
		slider.setMinimum(28);
		slider.setValue(36);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				textField_1.setText(String.valueOf(slider.getValue()));
			}

		});
		getContentPane().add(slider);

		final JFrame errors = new JFrame();
		errors.setVisible(false);
		errors.pack();

		textField_1 = new JTextField();
		textField_1.setBounds(376, 41, 40, 27);
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
					double x = Double.parseDouble(tf.getText());
					if (isDouble(x)) {
						JOptionPane.showMessageDialog(errors,
								"Non-integer value " + x
										+ " will be rounded to "
										+ checkValue(x) + " when start");

					} else if (x > 44) {
						JOptionPane.showMessageDialog(errors, x
								+ " will default to 44 when start");
					} else if (x < 28) {
						JOptionPane.showMessageDialog(errors, x
								+ " will default to 28 when start");
					}
					return true;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(errors,
							"Please type an integer between 28 to 44, thanks!");
					return false;
				}
			}

			private boolean isDouble(double x) {
				return x != Math.floor(x);
			}

			private <T extends Number> String checkValue(T x) {
				if (x.doubleValue() < 28) {
					return "28";
				} else if (x.doubleValue() > 44) {
					return "44";
				}
				return String.valueOf((int) Math.round(x.doubleValue()));
			}

		});
		textField_1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				String typed = textField_1.getText();
				if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE
						|| !typed.matches("\\d+") || typed.length() > 3
						|| typed.length() == 1) {
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
		lblquestion.setBounds(427, 41, 32, 27);
		lblquestion.setHorizontalAlignment(SwingConstants.CENTER);
		lblquestion.setToolTipText(dummyToolTip);
		getContentPane().add(lblquestion);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setBounds(429, 125, 81, 27);
		getContentPane().add(btnCancel);

		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Run Script here
			}
		});
		btnGo.setBounds(346, 125, 72, 27);
		getContentPane().add(btnGo);

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(246, 126, 88, 27);
		getContentPane().add(btnHelp);

		JCheckBox chckbxParcellationAndSegmentation =
				new JCheckBox("Parcellation and segmentation");
		chckbxParcellationAndSegmentation.setBounds(6, 64, 260, 50);
		getContentPane().add(chckbxParcellationAndSegmentation);

		JLabel lblAgeAtScan = new JLabel("Age at scan (weeks)");
		lblAgeAtScan.setBounds(6, 45, 150, 15);
		getContentPane().add(lblAgeAtScan);

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
			textField.setText(filePath);
		} else {
			// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
		}
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame();
		JApplet applet = new T2PreprocessingManualChooser();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(920, 100, 520, 160);
		f.setTitle("T2 Pre-processing");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
