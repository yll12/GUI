package gui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
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
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 163, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 30, 0 };
        gbl_panel.columnWeights =
                new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JSplitPane splitPane = new JSplitPane();
        GridBagConstraints gbc_splitPane = new GridBagConstraints();
        gbc_splitPane.insets = new Insets(0, 0, 0, 5);
        gbc_splitPane.anchor = GridBagConstraints.NORTHWEST;
        gbc_splitPane.gridx = 0;
        gbc_splitPane.gridy = 0;
        panel.add(splitPane, gbc_splitPane);

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
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{1, 52, 84, 263, 0};
        gridBagLayout.rowHeights = new int[]{1, 18, 146, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
                        
                                JLabel label = new JLabel("");
                                GridBagConstraints gbc_label = new GridBagConstraints();
                                gbc_label.anchor = GridBagConstraints.NORTHWEST;
                                gbc_label.insets = new Insets(0, 0, 5, 5);
                                gbc_label.gridx = 0;
                                gbc_label.gridy = 0;
                                getContentPane().add(label, gbc_label);
                
                        Component horizontalGlue = Box.createHorizontalGlue();
                        GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
                        gbc_horizontalGlue.anchor = GridBagConstraints.NORTHWEST;
                        gbc_horizontalGlue.insets = new Insets(0, 0, 5, 5);
                        gbc_horizontalGlue.gridx = 0;
                        gbc_horizontalGlue.gridy = 0;
                        getContentPane().add(horizontalGlue, gbc_horizontalGlue);
        
                table = new JTable();
                GridBagConstraints gbc_table = new GridBagConstraints();
                gbc_table.anchor = GridBagConstraints.NORTHWEST;
                gbc_table.insets = new Insets(0, 0, 5, 5);
                gbc_table.gridx = 0;
                gbc_table.gridy = 0;
                getContentPane().add(table, gbc_table);
                
                        TextField checkbox_1 = new TextField("jikj");
                        GridBagConstraints gbc_checkbox_1 = new GridBagConstraints();
                        gbc_checkbox_1.anchor = GridBagConstraints.NORTHWEST;
                        gbc_checkbox_1.insets = new Insets(0, 0, 0, 5);
                        gbc_checkbox_1.gridx = 1;
                        gbc_checkbox_1.gridy = 2;
                        getContentPane().add(checkbox_1, gbc_checkbox_1);
                        
                                JPopupMenu popupMenu = new JPopupMenu();
                                addPopup(checkbox_1, popupMenu);
                                
                                        @SuppressWarnings("rawtypes")
                                        JComboBox comboBox = new JComboBox();
                                        popupMenu.add(comboBox);
                                        
                                                JButton button_1 = new JButton(".");
                                                popupMenu.add(button_1);
        
                Box verticalBox = Box.createVerticalBox();
                GridBagConstraints gbc_verticalBox = new GridBagConstraints();
                gbc_verticalBox.fill = GridBagConstraints.BOTH;
                gbc_verticalBox.gridx = 3;
                gbc_verticalBox.gridy = 2;
                getContentPane().add(verticalBox, gbc_verticalBox);
                
                        JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
                        verticalBox.add(chckbxNewCheckBox);
                        
                                JRadioButton rdbtnNewRadioButton =
                                        new JRadioButton("New radio button");
                                verticalBox.add(rdbtnNewRadioButton);
                                
                                        JCheckBox chckbxNewCheckBox_1 = new JCheckBox("New check box");
                                        verticalBox.add(chckbxNewCheckBox_1);
                                        
                                                JButton btnNewButton_1 = new JButton("New button");
                                                verticalBox.add(btnNewButton_1);
                                                
                                                        JLabel lblNewLabel = new JLabel("New label");
                                                        verticalBox.add(lblNewLabel);
                                                        
                                                                JRadioButton rdbtnNewRadioButton_1 =
                                                                        new JRadioButton("New radio button");
                                                                verticalBox.add(rdbtnNewRadioButton_1);
                                                                
                                                                        JButton btnNewButton = new JButton("New button");
                                                                        verticalBox.add(btnNewButton);
                                                                        
                                                                                Button button = new Button("ok");
                                                                                verticalBox.add(button);
                                                                                button.addActionListener(new ActionListener() {
                                                                                    public void actionPerformed(ActionEvent arg0) {
                                                                                        System.out.println("Do Something Elses");
                                                                                    }
                                                                                });
    }

    private static void addPopup(Component component, final JPopupMenu popup) {
    }

    public static void main(String[] args) {
        final JFrame f = new JFrame();
        JApplet applet = new GUItest();
        
        applet.init();
        
        f.setContentPane(applet.getContentPane());
        f.setBounds(100, 100, 308, 199);
        f.setTitle("Testing this Application");
        f.setVisible(true);
        
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
