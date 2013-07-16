package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import utils.GUIUtilities;

@SuppressWarnings("serial")
public class T2PreprocessingAutoChooser extends JApplet {
	private JTextField textField;

	/**
	 * Create the applet.
	 */
	public T2PreprocessingAutoChooser() {
		GUIUtilities.initLookAndFeel("Nimbus");
		getContentPane().setLayout(null);
		
		JLabel lblInputDirectory = new JLabel("Input Directory");
		lblInputDirectory.setBounds(12, 12, 106, 15);
		getContentPane().add(lblInputDirectory);
		
		textField = new JTextField();
		textField.setBounds(146, 10, 354, 27);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNext = new JButton("Next");
		btnNext.setBounds(320, 80, 80, 25);
		getContentPane().add(btnNext);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(411, 80, 89, 25);
		getContentPane().add(btnCancel);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.setBounds(512, 12, 89, 25);
		getContentPane().add(btnOpen);

	}

	public static void main(String[] args) {
		final JFrame f = new JFrame();
		JApplet applet = new T2PreprocessingAutoChooser();

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
