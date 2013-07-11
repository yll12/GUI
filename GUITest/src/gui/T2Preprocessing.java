package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class T2Preprocessing extends JApplet {
	private final JTextField textField = new JTextField();
	private JTextField textField_1;

	/**
	 * Create the applet.
	 */
	public T2Preprocessing() {
		getContentPane().setLayout(null);

		JLabel lblInputDirectory = new JLabel("Input Directory");
		lblInputDirectory.setBounds(23, 10, 106, 15);
		lblInputDirectory.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblInputDirectory);
		textField.setBounds(139, 8, 223, 19);
		getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnOpen = new JButton("Open");
		btnOpen.setBounds(378, 5, 72, 25);
		getContentPane().add(btnOpen);

		JLabel lblAge = new JLabel("Age");
		lblAge.setBounds(92, 41, 27, 15);
		lblAge.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblAge);

		textField_1 = new JTextField();
		textField_1.setBounds(147, 35, 53, 19);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);

		Icon icon = UIManager.getIcon("OptionPane.questionIcon");

		JLabel lblquestion = new JLabel(icon);
		lblquestion.setBounds(218, 35, 32, 32);
		lblquestion.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblquestion);
		
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame();
		JApplet applet = new T2Preprocessing();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(400, 300, 500, 136);
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
