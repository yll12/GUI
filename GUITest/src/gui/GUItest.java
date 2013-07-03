package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class GUItest extends JApplet {
    private JTable table;

    /**
     * Create the applet.
     */
    public GUItest() {

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

        JMenuBar menuBar = new JMenuBar();
        menuBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
            }
        });
        setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        menuBar.add(panel);

        JSplitPane splitPane = new JSplitPane();
        panel.add(splitPane);

        Button button_2 = new Button("Click me");
        button_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println(".. you");
            }
        });
        splitPane.setLeftComponent(button_2);

        Checkbox checkbox = new Checkbox("GUI");
        menuBar.add(checkbox);

        JLayeredPane layeredPane = new JLayeredPane();
        menuBar.add(layeredPane);

        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setBounds(31, 43, 1, -31);
        layeredPane.add(desktopPane);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JLabel label = new JLabel("");
        getContentPane().add(label);

        TextField checkbox_1 = new TextField("jikj");
        getContentPane().add(checkbox_1);

        JPopupMenu popupMenu = new JPopupMenu();
        addPopup(checkbox_1, popupMenu);

        JComboBox comboBox = new JComboBox();
        popupMenu.add(comboBox);

        Button button = new Button("ok");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Do Something Elses");
            }
        });
        getContentPane().add(button);

        Component horizontalGlue = Box.createHorizontalGlue();
        getContentPane().add(horizontalGlue);

        table = new JTable();
        getContentPane().add(table);

        JButton button_1 = new JButton(".");
        getContentPane().add(button_1);

        Box verticalBox = Box.createVerticalBox();
        getContentPane().add(verticalBox);

        JButton btnNewButton = new JButton("New button");
        verticalBox.add(btnNewButton);

    }

    private static void addPopup(Component component, final JPopupMenu popup) {
    }
}
