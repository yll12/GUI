package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

@SuppressWarnings("serial")
public class Main extends JApplet {

	/**
	 * Create the applet.
	 */
	public Main() {

		for (UIManager.LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}

		GridBagLayout gridBagLayout = new GridBagLayout();
		getContentPane().setLayout(gridBagLayout);

		JButton btnDiffusionPreprocessing = new JButton(
				"Diffusion Preprocessing");
		btnDiffusionPreprocessing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("java -jar DiffusionAuto.jar");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnDiffusionPreprocessing = new GridBagConstraints();
		gbc_btnDiffusionPreprocessing.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDiffusionPreprocessing.insets = new Insets(0, 0, 5, 0);
		gbc_btnDiffusionPreprocessing.gridx = 0;
		gbc_btnDiffusionPreprocessing.gridy = 0;
		getContentPane().add(btnDiffusionPreprocessing,
				gbc_btnDiffusionPreprocessing);

		JButton btnTPreprocessing = new JButton("T2 Pre-processing");
		btnTPreprocessing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("java -jar T2Preprocessing.jar");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnTPreprocessing = new GridBagConstraints();
		gbc_btnTPreprocessing.anchor = GridBagConstraints.NORTH;
		gbc_btnTPreprocessing.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTPreprocessing.insets = new Insets(0, 0, 5, 0);
		gbc_btnTPreprocessing.gridx = 0;
		gbc_btnTPreprocessing.gridy = 1;
		getContentPane().add(btnTPreprocessing, gbc_btnTPreprocessing);

		JButton btnEtcEtc = new JButton("ETC ETC");
		GridBagConstraints gbc_btnEtcEtc = new GridBagConstraints();
		gbc_btnEtcEtc.anchor = GridBagConstraints.NORTH;
		gbc_btnEtcEtc.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEtcEtc.insets = new Insets(0, 0, 5, 0);
		gbc_btnEtcEtc.gridx = 0;
		gbc_btnEtcEtc.gridy = 2;
		getContentPane().add(btnEtcEtc, gbc_btnEtcEtc);

		JButton btnCancel = new JButton("Exit");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.NORTH;
		gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCancel.gridx = 0;
		gbc_btnCancel.gridy = 4;
		getContentPane().add(btnCancel, gbc_btnCancel);

	}

	public static void main(String[] args) {
		final JFrame f = new JFrame();
		JApplet applet = new Main();

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(100, 100, 208, 170);
		f.setTitle("YLL 1.0.0");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

}
