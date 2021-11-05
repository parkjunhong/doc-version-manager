/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 25. 오후 12:47:14
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import open.commons.text.NamedTemplate;
import open.commons.tool.dvm.core.FileReplacer;
import open.commons.tool.dvm.core.RevisionFile;
import open.commons.tool.dvm.json.Application;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.utils.DateUtil;
import open.commons.utils.IOUtils;

public class DocConfigInputDlg extends TitleAreaDialog {

    static NamedTemplate tpl = new NamedTemplate("{filename}_v{version}-{hd}-{td}.{ext}");

    private DocConfigComposite docConfigComposite;
    private DocConfig docConfig;
    
    private String project;
    private String[] projects;
    private List<Application> applications;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     * @param project
     *            현재 선택된 프로젝트
     * @param projects
     *            프로젝트 목록
     * @param applications
     *            TODO
     */
    public DocConfigInputDlg(Shell parentShell, String project, String[] projects, List<Application> applications) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);

        this.project = project;
        this.projects = projects;
        this.applications = applications;
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("문서 관리 정보");
        setMessage("문서 관리 정보를 입력하세요.");
        setTitleImage(SWTResourceManager.getImage(DocConfigInputDlg.class, "/images/osa_server_file.png"));
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout(3, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        this.docConfigComposite = new DocConfigComposite(container, SWT.NONE, true, this.project, projects, applications);
        this.docConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

        return area;
    }

    public DocConfig getDocConfig() {
        return docConfig;
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(634, 465);
    }

    public String getProject() {
        return project;
    }

    @Override
    protected void okPressed() {
        this.docConfig = this.docConfigComposite.getDocConfig();
        this.project = this.docConfigComposite.getProject();

        if (project == null || project.trim().isEmpty()) {
            MessageDialog.openInformation(getShell(), "데이타 누락", "프로젝트 이름을 꼭~ 입력하시기 바랍니다.");
            return;
        }

        if (docConfig.getDocKind() == null || docConfig.getDocKind().trim().isEmpty()) {
            MessageDialog.openInformation(getShell(), "데이타 누락", "문서 종류를 꼭~ 입력하시기 바랍니다.");
            return;
        }

        File file = null;
        RevisionFile revfile = FileReplacer.getTargetFile(docConfig.getFileDir(), docConfig.getFilename(), docConfig.getFileExt(), docConfig.getDatePattern());
        if (revfile != null && revfile.getSource() != null) {
            super.okPressed();
        } else {
            file = new File(docConfig.getFileDir(), docConfig.getFilename() + "." + docConfig.getFileExt());
            if (file.exists()) {
                // create a new managed file.
                tpl.clear();

                Calendar cal = Calendar.getInstance();
                int dow = cal.get(Calendar.DAY_OF_WEEK);
                switch (dow) {
                    case Calendar.SUNDAY: // 1
                        cal.add(Calendar.DAY_OF_WEEK, 1);
                        break;
                    case Calendar.SATURDAY: // 7
                        cal.add(Calendar.DAY_OF_WEEK, 2);
                        break;
                    case Calendar.MONDAY: // 2
                    case Calendar.TUESDAY: // 3
                    case Calendar.WEDNESDAY: // 4
                    case Calendar.THURSDAY: // 5
                    case Calendar.FRIDAY: // 6
                        cal.add(Calendar.DAY_OF_WEEK, 2 - dow);
                        break;
                }

                Calendar tailCal = Calendar.getInstance();
                tailCal.setTimeInMillis(cal.getTimeInMillis());
                tailCal.add(Calendar.DAY_OF_WEEK, 4);

                tpl.addValue("filename", docConfig.getFilename())//
                        .addValue("version", "0.1.0")//
                        .addValue("hd", DateUtil.toString(cal.getTime(), "yyyyMMdd"))//
                        .addValue("td", DateUtil.toString(tailCal.getTime(), "MMdd"))//
                        .addValue("ext", docConfig.getFileExt());

                File newFile = new File(docConfig.getFileDir(), tpl.format());

                try {
                    IOUtils.transfer(new FileInputStream(file), new FileOutputStream(newFile));

                    super.okPressed();
                } catch (Exception e) {
                    MessageDialog.openInformation(getShell(), "파일 생성 오류", "초기 문서를 생성하는 도중 오류가 발생하였습니다.");

                    this.project = null;
                    this.docConfig = null;

                    super.cancelPressed();
                }

            } else {
                MessageDialog.openInformation(getShell(), "데이터 누락", "문서가 존재하지 않습니다.");

                this.project = null;
                this.docConfig = null;

                super.cancelPressed();
            }
        }
    }

}
