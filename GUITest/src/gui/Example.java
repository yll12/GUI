package gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Example extends JApplet {

    /**
     * Create the applet.
     */
    public Example() {
        Container contentPane = getContentPane();
        Icon icon = new ImageIcon("~/pic/haiz.png", "Swing!");
        JLabel label = new JLabel("Swing!", icon, JLabel.CENTER);
        contentPane.add(label, BorderLayout.CENTER);
    }
}
