/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 24. 오전 10:33:27
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import open.commons.tool.dvm.json.Application;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.tool.dvm.ui.IDocConfigChangeListener;
import open.commons.tool.dvm.util.SWTUtils;

public class DocConfigView extends Composite {

    private ToolBar tbTitle;
    private ToolItem tltmProject;
    private ToolItem tltmConc;
    private ToolItem tltmDoc;

    private DocConfigComposite cpDocConfig;
    private Set<IDocConfigChangeListener> listeners = new HashSet<>();

    public DocConfigView(Composite parent, int style) {
        this(parent, style, null);
    }

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     * @param applications
     *            TODO
     */
    public DocConfigView(Composite parent, int style, List<Application> applications) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        Composite cpMenu = new Composite(this, SWT.BORDER);
        cpMenu.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_cpMenu = new GridLayout(2, false);
        gl_cpMenu.verticalSpacing = 0;
        gl_cpMenu.marginWidth = 0;
        gl_cpMenu.marginHeight = 0;
        gl_cpMenu.horizontalSpacing = 0;
        cpMenu.setLayout(gl_cpMenu);

        tbTitle = new ToolBar(cpMenu, SWT.FLAT | SWT.RIGHT);

        ToolBar tbMenus = new ToolBar(cpMenu, SWT.FLAT | SWT.RIGHT);
        tbMenus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        ToolItem tltmImportDoc = new ToolItem(tbMenus, SWT.NONE);
        tltmImportDoc.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String filepath = SWTUtils.selectFile(getShell(), null);
                if (filepath == null) {
                    return;
                }

                cpDocConfig.updateDocumentFile(filepath);

            }
        });
        tltmImportDoc.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/ic_action_attachment.png"));
        tltmImportDoc.setText("Import Doc.");

        ToolItem tltmApplyChanged = new ToolItem(tbMenus, SWT.NONE);
        tltmApplyChanged.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DocConfig docConfig = cpDocConfig.getDocConfig();

                tltmDoc.setText(docConfig.getDocKind());

                for (IDocConfigChangeListener l : listeners) {
                    l.change(tltmProject.getText(), docConfig, null);
                }
            }
        });
        tltmApplyChanged.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/ic_action_labels.png"));
        tltmApplyChanged.setText("Apply");

        ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        cpDocConfig = new DocConfigComposite(scrolledComposite, SWT.FILL, false, null, null, applications);
        scrolledComposite.setContent(cpDocConfig);

    }

    public void addDocConfigChangeListener(IDocConfigChangeListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setConfig(String project, DocConfig docConfig) {

        cpDocConfig.setConfig(project, docConfig);

        // for Project
        if (project != null) {
            tltmProject = new ToolItem(tbTitle, SWT.NONE);
            tltmProject.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/project.png"));
            tltmProject.setText(project);

            tltmConc = new ToolItem(tbTitle, SWT.NONE);
            tltmConc.setEnabled(false);
            tltmConc.setDisabledImage(SWTResourceManager.getImage(DocConfigView.class, "/images/ic_action_send_now.png"));
            tltmConc.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/ic_action_send_now.png"));
        }

        if (tltmDoc != null) {
            tltmDoc.dispose();
        }

        // for Doc Kind
        tltmDoc = new ToolItem(tbTitle, SWT.NONE);
        tltmDoc.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/doc.png"));
        tltmDoc.setText(docConfig.getDocKind());
    }
}
