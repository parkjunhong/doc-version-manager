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

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import open.commons.tool.dvm.json.Application;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.tool.dvm.util.SWTUtils;
import open.commons.tool.dvm.util.Utils;
import open.commons.utils.ArrayUtils;
import open.commons.utils.FileUtils;

public class DocConfigComposite extends Composite {

    private Combo cbxProject;
    private Text textDocKind;
    private Text textDir;
    private Text textName;
    private Text textFileExtension;
    private Combo cbxExeCmd;
    private Text textBackupDir;
    private Combo cbxDatePattern;
    private Combo cbxBatchFileEncoding;

    private DocConfig docConfig = new DocConfig();

    private ConcurrentSkipListMap<String, ConcurrentSkipListSet<String>> applications = new ConcurrentSkipListMap<>();

    private String initApp;

    public DocConfigComposite(Composite parent, int style) {
        this(parent, style, false, null, null, null);
    }

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     * @param newProject
     *            새로운 프로젝트 여부
     * @param parentProject
     *            상위 프로젝트명
     * @param projects
     *            프로젝트 목록
     * @param applications
     *            파일 타입에 따른 실행 프로그램
     */
    public DocConfigComposite(Composite parent, int style, boolean newProject, String parentProject, String[] projects, List<Application> applications) {
        super(parent, style);

        if (applications != null) {
            ConcurrentSkipListSet<String> appPaths = null;
            for (Application app : applications) {
                if (app.getAppPath() == null || app.getAppPath().isEmpty()) {
                    continue;
                }

                for (String fileExt : app.getFileExts()) {
                    appPaths = this.applications.get(fileExt);

                    if (appPaths == null) {
                        appPaths = new ConcurrentSkipListSet<>();
                        this.applications.put(fileExt, appPaths);
                    }

                    appPaths.add(app.getAppPath());
                }
            }
        }

        GridLayout gridLayout = new GridLayout(4, false);
        setLayout(gridLayout);

        if (newProject) {
            Label lblProject = new Label(this, SWT.NONE);
            lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            lblProject.setText("Project: ");

            this.cbxProject = new Combo(this, SWT.BORDER);

            int parentProjectIdx = -1;
            if (projects != null && projects.length > 0) {
                this.cbxProject.setItems(projects);
                parentProjectIdx = ArrayUtils.getIndex(projects, parentProject);
            }

            this.cbxProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            this.cbxProject.setItems(projects);
            this.cbxProject.select(parentProjectIdx);

            new Label(this, SWT.NONE);
            new Label(this, SWT.NONE);
        }

        Label lblType = new Label(this, SWT.NONE);
        lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblType.setText("Doc. &Kind: ");

        this.textDocKind = new Text(this, SWT.BORDER);
        this.textDocKind.setToolTipText("문서 종류");
        this.textDocKind.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.textDocKind.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        new Label(this, SWT.NONE);
        new Label(this, SWT.NONE);

        Label lblDirectory = new Label(this, SWT.NONE);
        lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDirectory.setSize(56, 15);
        lblDirectory.setText("File &Directory: ");

        this.textDir = new Text(this, SWT.BORDER);
        this.textDir.setBackground(SWTResourceManager.getColor(224, 255, 255));
        this.textDir.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.getSource();
                String dir = text.getText().trim();
                if (dir.isEmpty()) {
                    return;
                }

                if (validate(dir, false)) {
                    text.setBackground(SWTResourceManager.getColor(224, 255, 255));
                } else {
                    text.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                }
            }
        });

        int textDirDropTargetOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget textDirDropTarget = new DropTarget(this.textDir, textDirDropTargetOperations);
        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        textDirDropTarget.setTransfer(new Transfer[] { textTransfer, fileTransfer });

        textDirDropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (!fileTransfer.isSupportedType(event.currentDataType) //
                        || event.data == null) {
                    return;
                }

                String filepath = event.data.getClass().isArray() ? ((Object[]) event.data)[0].toString() : event.data.toString();

                updateDocumentFile(filepath);
            }
        });

        this.textDir.setToolTipText("문서 경로");
        this.textDir.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.textDir.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        this.textDir.setSize(155, 25);
        new Label(this, SWT.NONE);

        Button btnSelectDocDir = new Button(this, SWT.NONE);
        btnSelectDocDir.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        btnSelectDocDir.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String dir = SWTUtils.selectDirectory(getShell(), textDir.getText());
                if (dir != null) {
                    textDir.setText(dir);
                }
            }
        });
        btnSelectDocDir.setSize(57, 25);
        btnSelectDocDir.setText("Select ...");

        Label lblName = new Label(this, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblName.setText("&Name: ");

        this.textName = new Text(this, SWT.BORDER);
        this.textName.setToolTipText("버전,날짜 패턴를 제외한 문서이름");
        this.textName.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.textName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        new Label(this, SWT.NONE);
        new Label(this, SWT.NONE);

        Label lblDatePattern = new Label(this, SWT.NONE);
        lblDatePattern.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDatePattern.setText("Date &Pattern: ");

        this.cbxDatePattern = new Combo(this, SWT.READ_ONLY);
        this.cbxDatePattern.setItems(new String[] { "\\d{8}-\\d{4}", "\\d{8}-\\d{8}", "\\d{8}-\\d{4}|\\d{8}-\\d{8}" });
        this.cbxDatePattern.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.cbxDatePattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        this.cbxDatePattern.select(0);
        new Label(this, SWT.NONE);
        new Label(this, SWT.NONE);

        Label lblFileExt = new Label(this, SWT.NONE);
        lblFileExt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFileExt.setText("File &Ext.: ");

        this.textFileExtension = new Text(this, SWT.BORDER);
        this.textFileExtension.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.getSource();

                Set<String> apps = getApplications(text.getText().split("[|]"));

                if (initApp == null || initApp.isEmpty()) {
                    cbxExeCmd.setItems(apps.toArray(new String[0]));
                } else {
                    if (apps.contains(initApp)) {
                        apps.remove(initApp);
                    }

                    cbxExeCmd.setItems(ArrayUtils.prepend(apps.toArray(new String[0]), initApp));
                    cbxExeCmd.select(0);
                }
            }
        });
        this.textFileExtension.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.textFileExtension.setToolTipText("Bar('|') is a delimiter.");
        this.textFileExtension.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        new Label(this, SWT.NONE);
        new Label(this, SWT.NONE);

        Label lblExeCmd = new Label(this, SWT.NONE);
        lblExeCmd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblExeCmd.setText("&Command: ");

        this.cbxExeCmd = new Combo(this, SWT.BORDER);
        this.cbxExeCmd.setBackground(SWTResourceManager.getColor(224, 255, 255));
        this.cbxExeCmd.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Combo text = (Combo) e.getSource();
                String file = text.getText().trim();
                if (file.isEmpty()) {
                    return;
                }

                if (validate(file, true)) {
                    text.setBackground(SWTResourceManager.getColor(224, 255, 255));
                } else {
                    if (file.equals(FileUtils.getFileName(file))) {
                        text.setBackground(SWTResourceManager.getColor(224, 255, 255));
                    } else {
                        text.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    }

                }
            }
        });
        this.cbxExeCmd.setToolTipText("해당 파일 편집기의 실행파일을 입력하기 바랍니다.");
        this.cbxExeCmd.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.cbxExeCmd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        int textExeCmdDropTargetOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget textExeCmdDirDropTarget = new DropTarget(this.cbxExeCmd, textExeCmdDropTargetOperations);
        final TextTransfer exeCmdTextTransfer = TextTransfer.getInstance();
        final FileTransfer exeCmdFileTransfer = FileTransfer.getInstance();
        textExeCmdDirDropTarget.setTransfer(new Transfer[] { exeCmdTextTransfer, exeCmdFileTransfer });

        textExeCmdDirDropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (!exeCmdFileTransfer.isSupportedType(event.currentDataType) //
                        || event.data == null) {
                    return;
                }

                String filepath = event.data.getClass().isArray() ? ((Object[]) event.data)[0].toString() : event.data.toString();

                File f = new File(filepath);

                if (f.exists() && f.isFile()) {
                    cbxExeCmd.setText(filepath);
                }
            }
        });

        Button btnCheckButton = new Button(this, SWT.CHECK);
        btnCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                String exeCmd = (String) cbxExeCmd.getData();
                if (exeCmd == null) {
                    return;
                }

                cbxExeCmd.setText(((Button) e.getSource()).getSelection() ? FileUtils.getFileName(exeCmd) : exeCmd);
            }
        });
        btnCheckButton.setToolTipText("시스템 등록명령어인 경우 체크");

        Button btnSelectExecutionCommand = new Button(this, SWT.NONE);
        btnSelectExecutionCommand.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String filepath = SWTUtils.selectFile(getShell(), cbxExeCmd.getText());
                if (filepath == null) {
                    return;
                }

                cbxExeCmd.setData(filepath);
                cbxExeCmd.setText(filepath);
            }
        });
        btnSelectExecutionCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnSelectExecutionCommand.setText("Select ...");

        Label lblBackupDir = new Label(this, SWT.NONE);
        lblBackupDir.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblBackupDir.setText("&Backup Dir.: ");

        this.textBackupDir = new Text(this, SWT.BORDER);
        this.textBackupDir.setBackground(SWTResourceManager.getColor(224, 255, 255));
        this.textBackupDir.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.getSource();
                String dir = text.getText().trim();
                if (dir.isEmpty()) {
                    return;
                }

                if (validate(dir, false)) {
                    text.setBackground(SWTResourceManager.getColor(224, 255, 255));
                } else {
                    text.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                }
            }
        });

        int textBackupDirDropTargetOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget textBackupDirDirDropTarget = new DropTarget(this.textBackupDir, textBackupDirDropTargetOperations);
        final TextTransfer textBackupDirTextTransfer = TextTransfer.getInstance();
        final FileTransfer textBackupDirFileTransfer = FileTransfer.getInstance();
        textBackupDirDirDropTarget.setTransfer(new Transfer[] { textBackupDirTextTransfer, textBackupDirFileTransfer });

        textBackupDirDirDropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (!textBackupDirFileTransfer.isSupportedType(event.currentDataType) //
                        || event.data == null) {
                    return;
                }

                String filepath = event.data.getClass().isArray() ? ((Object[]) event.data)[0].toString() : event.data.toString();

                File f = new File(filepath);

                if (f.exists() && f.isDirectory()) {
                    textBackupDir.setText(filepath);
                }
            }
        });

        this.textBackupDir.setToolTipText("백업 경로");
        this.textBackupDir.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        this.textBackupDir.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        new Label(this, SWT.NONE);

        Button btnSelectBackupDir = new Button(this, SWT.NONE);
        btnSelectBackupDir.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String backupDir = SWTUtils.selectDirectory(getShell(), textBackupDir.getText());
                if (backupDir != null) {
                    textBackupDir.setText(backupDir);
                }
            }
        });
        btnSelectBackupDir.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnSelectBackupDir.setText("Select ...");

        Label lblBatchFileEncoding = new Label(this, SWT.NONE);
        lblBatchFileEncoding.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblBatchFileEncoding.setText("Batch Encoding: ");

        cbxBatchFileEncoding = new Combo(this, SWT.READ_ONLY);
        this.cbxBatchFileEncoding.setToolTipText("UTF-8을 사용할 것을 강추합니다.");
        this.cbxBatchFileEncoding.setItems(new String[] { "UTF-8", "EUC-KR" });
        this.cbxBatchFileEncoding.setFont(SWTResourceManager.getFont("나눔고딕코딩", 9, SWT.NORMAL));
        cbxBatchFileEncoding.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.cbxBatchFileEncoding.select(0);
        new Label(this, SWT.NONE);
        new Label(this, SWT.NONE);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private Set<String> getApplications(String... fileExts) {
        ConcurrentSkipListSet<String> applications = new ConcurrentSkipListSet<>();

        Set<String> apps = null;
        for (String fileExt : fileExts) {
            apps = this.applications.get(fileExt);
            if (apps != null) {
                applications.addAll(apps);
            }
        }

        return applications;
    }

    public DocConfig getDocConfig() {

        docConfig.setBackupDir(Utils.nullIfEmpty(textBackupDir.getText().trim()));
        docConfig.setBatchFileEndcoding(cbxBatchFileEncoding.getText().trim());
        docConfig.setDatePattern(cbxDatePattern.getText().trim());
        docConfig.setDocKind(Utils.nullIfEmpty(textDocKind.getText().trim()));

        docConfig.setExeCmd(Utils.nullIfEmpty(cbxExeCmd.getText().trim()));
        docConfig.setFileDir(textDir.getText().trim());
        docConfig.setFileExt(Utils.nullIfEmpty(textFileExtension.getText().trim()));
        docConfig.setFilename(Utils.nullIfEmpty(textName.getText().trim()));

        return this.docConfig;
    }

    public String getProject() {
        return cbxProject == null || cbxProject.isDisposed() ? "" : cbxProject.getText().trim();
    }

    public void setConfig(String project, DocConfig docConfig) {
        if (docConfig == null) {
            Utils.clearText(textDocKind, textDir, textName, textFileExtension, textBackupDir);
            if (!cbxExeCmd.isDisposed()) {
                cbxExeCmd.setText("");
            }
            docConfig = new DocConfig();

        } else {
            if (cbxProject != null && !cbxProject.isDisposed()) {
                cbxProject.setText(project != null ? project : "");
            }

            if (!cbxExeCmd.isDisposed()) {
                // (start) [BUG-FIX]: 파일 확장자 미확인으로 인한 UI 에러처리 / Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 12. 15. 오후
                // 5:48:40
                if (docConfig.getFileExt() != null) {
                    // (end): 2019. 12. 15. 오후 5:48:40
                    String[] fileExts = docConfig.getFileExt().split("[|]");
                    Set<String> apps = getApplications(fileExts);

                    cbxExeCmd.setItems(apps.toArray(new String[0]));

                    initApp = docConfig.getExeCmd();
                    if (initApp == null) {
                        initApp = "";
                    }
                    cbxExeCmd.setText(initApp);
                }
            }

            Utils.setText(new Text[] { textDocKind, textDir, textName, textFileExtension, textBackupDir }, //
                    new String[] { docConfig.getDocKind(), docConfig.getFileDir(), docConfig.getFilename(), docConfig.getFileExt(), docConfig.getBackupDir() });

            cbxDatePattern.setText(docConfig.getDatePattern());
            cbxBatchFileEncoding.setText(docConfig.getBatchFileEndcoding());
        }

        this.docConfig = docConfig;
    }

    void setFileDir(String dir) {
        textDir.setText(dir);
    }

    void setFilename(String filename) {
        textName.setText(filename);
    }

    void updateDocumentFile(String filepath) {
        File f = new File(filepath);

        if (f.exists()) {
            String dir = FileUtils.getFilePath(filepath);
            String filename = FileUtils.getFileName(filepath);

            if (new File(filepath).isFile()) {

                String regex = "(.*)_[v|V]\\d+(.\\d+){0,2}\\-(\\d{8}\\-\\d{4}|\\d{8}\\-\\{8})\\..*";
                // 파일명 포맷이 설정된 것과 동일한 경우
                boolean matched = false;
                if (filename.matches(regex)) {
                    filename = filename.replaceAll(regex, "$1");
                    matched = true;
                }
                textDir.setText(dir);

                // 파일명 설정
                textName.setText(matched ? filename : FileUtils.getFileNameNoExtension(filename));
                // 파일 확장자 설정
                String ext = FileUtils.getFileExtension(filepath);
                textFileExtension.setText(ext);
            } else {
                textDir.setText(dir + File.separator + filename);
            }
        }
    }

    private boolean validate(String filepath, boolean isFile) {
        File file = new File(filepath);

        return isFile ? file.isFile() : file.isDirectory();
    }

}
