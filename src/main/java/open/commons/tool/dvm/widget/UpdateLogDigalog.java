/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 26. 오후 5:03:42
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm.widget;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class UpdateLogDigalog extends TitleAreaDialog {
    private ScrolledComposite scrolledComposite;
    private Text text;

    private final int MAX_LENGTH = 0xFFFFF;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public UpdateLogDigalog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
        setBlockOnOpen(false);
    }

    public void ready() {
        create();
        constrainShellSize();
        getShell().setVisible(false);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        setMessage("문서 업데이트 로그창");
        setTitle("문서 업데이트 로그창");
        setTitleImage(SWTResourceManager.getImage(UpdateLogDigalog.class, "/images/ic_action_go_to_today.png"));
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        this.scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        this.scrolledComposite.setExpandHorizontal(true);
        this.scrolledComposite.setExpandVertical(true);

        this.text = new Text(this.scrolledComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        this.text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                scrolledComposite.setMinSize(text.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });
        this.scrolledComposite.setContent(this.text);
        this.scrolledComposite.setMinSize(this.text.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return area;
    }

    int curLogLen = 0;

    public void addLog(List<String> logs) {
        StringBuffer sb = new StringBuffer();
        for (String log : logs) {
            sb.append(log);
            sb.append("\n");
        }

        int ll = sb.length();

        int rl = (curLogLen + ll) - MAX_LENGTH;

        if (rl > 0) {
            text.setSelection(curLogLen - ll, curLogLen);
            text.clearSelection();
        }

        text.append(sb.toString());
    }

    @Override
    protected void okPressed() {
        getShell().setVisible(false);
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Close", true);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(868, 404);
    }

}
