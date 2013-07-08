package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class DiffusionAttempt extends JApplet {
    private JTextField textField;

    /**
     * Create the applet.
     */
    public DiffusionAttempt() {

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

        getContentPane().setLayout(
                new MigLayout("", "[72px][5px][18px][210px][45px][65px]",
                        "[20px][23px][23px]"));

        JLabel lblNewLabel = new JLabel("Input directory");
        getContentPane().add(lblNewLabel,
                "cell 0 0,alignx right,aligny center");

        textField = new JTextField();
        getContentPane().add(textField, "cell 2 0 3 1,growx,aligny center");
        textField.setColumns(10);

        JButton btnOpen = new JButton("Open");
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                buildLoadDiag();
            }
        });
        getContentPane().add(btnOpen, "cell 5 0,alignx center");

        JCheckBox chckbxBedpostx = new JCheckBox("Bedpostx");
        getContentPane().add(chckbxBedpostx,
                "cell 0 1 3 1,alignx center,aligny center");

        JButton btnNewButton = new JButton("Go");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Runtime.getRuntime().exec("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getContentPane().add(btnNewButton,
                "cell 4 2,alignx center,aligny center");

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        getContentPane().add(btnCancel,
                "cell 5 2,alignx center,aligny center");

    }

    public void buildLoadDiag() {
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
        JApplet applet = new DiffusionAttempt();

        applet.init();

        f.setContentPane(applet.getContentPane());
        f.setBounds(100, 100, 308, 199);
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
