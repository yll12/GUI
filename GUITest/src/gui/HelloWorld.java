package gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class HelloWorld extends Composite {
    private Text txtHello;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public HelloWorld(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        
        Button btnNewButton_1 = new Button(this, SWT.NONE);
        btnNewButton_1.setText("New Button");
        new Label(this, SWT.NONE);
        
        Button btnNewButton = new Button(this, SWT.NONE);
        btnNewButton.setText("New Button");
        new Label(this, SWT.NONE);
        
        Label lblNewLabel = new Label(this, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("New Label");
        
        txtHello = new Text(this, SWT.BORDER);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
