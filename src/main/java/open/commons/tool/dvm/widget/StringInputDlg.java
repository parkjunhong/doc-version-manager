/*
 *
 * This file is generated under this project, "JsonToJavaClassGenerator".
 *
 * Date  : 2014. 10. 27. 오전 11:09:40
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.widget;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class StringInputDlg extends TitleAreaDialog {

    private String string;
    private Label lblString;
    private Text txtString;

    private String label = "string";
    private String message = "Input a message.";
    private String title = "Title";

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public StringInputDlg(Shell parentShell, String latestName) {
        super(parentShell);

        this.string = latestName;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceManager.getImage(StringInputDlg.class, "/images/ic_action_labels.png"));
        setMessage(this.message);
        setTitle(this.title);

        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.lblString = new Label(container, SWT.NONE);
        this.lblString.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        this.lblString.setText(label);

        this.txtString = new Text(container, SWT.BORDER);
        this.txtString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        if (string != null) {
            this.txtString.setText(string);
            this.txtString.selectAll();
        }

        return area;
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 186);
    }

    public String getString() {
        return this.string != null ? this.string.trim() : "";
    }

    @Override
    protected void okPressed() {
        try {
            String jcn = this.txtString.getText();

            char[] chars = jcn.toCharArray();

            if (chars.length < 1) {
                return;
            }
            
            this.string = jcn;

        } finally {
            super.okPressed();
        }
    }

    public void setDlgMessage(String message) {
        this.message = message;
    }

    public void setDlgTitle(String title) {
        this.title = title;
    }

    public void setStringLabel(String label) {
        this.label = label;
    }

}
