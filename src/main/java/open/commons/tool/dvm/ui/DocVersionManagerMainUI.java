/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 21. 오후 5:28:28
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import open.commons.core.Result;
import open.commons.tool.dvm.Metadata;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.tool.dvm.json.DvmConfig;
import open.commons.tool.dvm.json.ProjectConfig;
import open.commons.tool.dvm.widget.DocConfigInputDlg;
import open.commons.tool.dvm.widget.DocConfigView;
import open.commons.tool.dvm.widget.ProjectConfigView;
import open.commons.tool.dvm.widget.StringInputDlg;
import open.commons.tool.dvm.widget.UpdateLogDigalog;
import open.commons.core.utils.ArrayUtils;
import open.commons.core.utils.FileUtils;
import open.commons.core.utils.IOUtils;

public class DocVersionManagerMainUI extends ApplicationWindow implements IDocConfigChangeListener, IProjectConfigChangeListener {

    private static UpdateLogDigalog logDlg;

    private Tree treeProjectNavigator;

    private TreeViewer treeViewer;
    private Group grpConfigFile;

    private Combo cbxConfigFile;
    private Composite cpProjectNavigation;
    private Composite cpNaviMenus;
    private ToolBar tbTree;
    private ToolBar tbConfig;
    private ToolItem tltmNewProject;
    private ToolItem tltmNewDoc;
    private ToolBar tbConfigFile;
    private ToolItem tltmOpenConfigFile;

    private ToolItem tltmLoadConfigFile;
    private Shell mainShell;
    private ToolItem tltmSeparator;

    private ToolItem tltmSaveFile;
    private String resourceFile;
    private SashForm sashForm;
    private ScrolledComposite scDetailedView;

    private Label lblLogo;

    private DvmConfig dvmConfig = new DvmConfig();

    /**
     * {@link DocConfig} or {@link ProjectConfig}
     */
    private Object currentDetailedItem;

    /**
     * Create the application window.
     */
    public DocVersionManagerMainUI() {
        super(null);
        createActions();
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();

        init();

        logDlg = new UpdateLogDigalog(new Shell(Display.getCurrent()));
        logDlg.ready();
    }

    private void addConfigFileList(String filepath) {

        if (!new File(filepath).exists()) {
            return;
        }

        String curfile = cbxConfigFile.getText();

        String[] files = cbxConfigFile.getItems();
        if (!ArrayUtils.contains(files, filepath)) {
            files = ArrayUtils.prepend(files, filepath);
        }

        cbxConfigFile.setItems(files);

        cbxConfigFile.setText(curfile);
    }

    private void addProjectConfig(ProjectConfig config) {
        this.dvmConfig.addProjectConfig(config);
    }

    @Override
    public void change(ProjectConfig newValue, ProjectConfig oldValue) {

        try {
            if (oldValue != null) {
                this.dvmConfig.removeProjectConfig(oldValue.getProject());
            }

            if (newValue != null) {
                this.dvmConfig.addProjectConfig(newValue);

                updateCurrentDetailedView(newValue.getProject(), newValue);
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void change(String project, DocConfig newValue, DocConfig oldValue) {

        this.dvmConfig.addApplication(newValue.getExeCmd(), newValue.getFileExt().split("[|]"));

        updateCurrentDetailedView(project, newValue);
    }

    private void clearDetailedView() {
        Control latestDocConfig = scDetailedView.getContent();
        if (latestDocConfig != null) {
            latestDocConfig.dispose();
        }
    }

    private void clearProjectConfig() {
        this.dvmConfig.clearProjectConfigs();
    }

    /**
     * Configure the shell.
     * 
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
        newShell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (logDlg != null) {
                    Shell shell = logDlg.getShell();
                    if (shell != null) {
                        shell.dispose();
                    }
                }
            }
        });

        newShell.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/osa_user_blue_tester.png"));
        super.configureShell(newShell);
        newShell.setText(Metadata.product() + " " + Metadata.version() + " - " + Metadata.update() + " (" + Metadata.author() + " | " + Metadata.email() + ")");

        this.mainShell = newShell;
    }

    /**
     * Create the actions.
     */
    private void createActions() {
    }

    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        this.grpConfigFile = new Group(container, SWT.NONE);
        this.grpConfigFile.setLayout(new GridLayout(4, false));
        this.grpConfigFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel = new Label(this.grpConfigFile, SWT.NONE);
        lblNewLabel.setText("Config File: ");
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        this.cbxConfigFile = new Combo(this.grpConfigFile, SWT.BORDER);// | SWT.WRAP | SWT.MULTI);
        this.cbxConfigFile.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                File file = new File(cbxConfigFile.getText());
                if (tltmLoadConfigFile != null && !tltmLoadConfigFile.isDisposed()) {
                    tltmLoadConfigFile.setEnabled(file.exists());
                }
            }
        });

        int dropTargetOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget dropTarget = new DropTarget(this.cbxConfigFile, dropTargetOperations);
        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        dropTarget.setTransfer(new Transfer[] { textTransfer, fileTransfer });

        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (!fileTransfer.isSupportedType(event.currentDataType) //
                        || event.data == null) {
                    return;
                }

                String filepath = null;
                filepath = event.data.getClass().isArray() ? ((Object[]) event.data)[0].toString() : event.data.toString();

                if (new File(filepath).isFile()) {
                    cbxConfigFile.setText(filepath);

                    addConfigFileList(filepath);
                } else {
                    MessageDialog.openWarning(mainShell, "설정 파일 선택", "파일만 선택할 수 있습니다.");
                }
            }
        });

        this.cbxConfigFile.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.ITALIC));
        this.cbxConfigFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        this.tbConfigFile = new ToolBar(this.grpConfigFile, SWT.FLAT | SWT.RIGHT);

        this.tltmOpenConfigFile = new ToolItem(this.tbConfigFile, SWT.NONE);
        this.tltmOpenConfigFile.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/directory_24x24.png"));
        this.tltmOpenConfigFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(getShell(), SWT.NONE);
                String filepath = dlg.open();

                if (filepath == null) {
                    return;
                }

                cbxConfigFile.setText(filepath);
                addConfigFileList(filepath);
            }
        });

        this.tltmLoadConfigFile = new ToolItem(this.tbConfigFile, SWT.NONE);
        this.tltmLoadConfigFile.setEnabled(new File(cbxConfigFile.getText()).isFile());
        this.tltmLoadConfigFile.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/project_configs.png"));
        this.tltmLoadConfigFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                try {
                    File resourceFile = new File(cbxConfigFile.getText().trim());

                    if (!resourceFile.exists()) {
                        MessageDialog.openInformation(mainShell, "No File", "선택된 파일이 없습니다.");
                        return;
                    }

                    byte[] bytes = IOUtils.readFully(new FileInputStream(resourceFile));
                    String jsonStr = new String(bytes, "UTF-8");

                    clearProjectConfig();

                    dvmConfig = new DvmConfig();
                    dvmConfig.mature(jsonStr);

                    setProjectConfig(treeViewer);

                    updateResourceFile(resourceFile.getAbsolutePath());

                    createDetailedLogo();
                } catch (Exception ex) {
                    MessageDialog.openWarning(mainShell, "데이터 선택 오류", "선택한 파일에 포함된 데이터가 JSON 형식이 아니거나 다른 이유로 로딩 중 오류가 발생했습니다.");
                    updateResourceFile(null);

                    ex.printStackTrace();
                }
            }
        });
        new Label(this.grpConfigFile, SWT.NONE);

        sashForm = new SashForm(container, SWT.SMOOTH);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createProjectNavigation(sashForm);

        this.scDetailedView = new ScrolledComposite(this.sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        this.scDetailedView.setExpandHorizontal(true);
        this.scDetailedView.setExpandVertical(true);
        this.sashForm.setWeights(new int[] { 298, 1029 });

        createDetailedLogo();

        return container;
    }

    private void createDetailedLogo() {

        this.lblLogo = new Label(this.scDetailedView, SWT.CENTER);
        this.lblLogo.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/ic_action_dock.png"));
        this.scDetailedView.setContent(this.lblLogo);
        this.scDetailedView.setMinSize(this.lblLogo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Create the menu manager.
     * 
     * @return the menu manager
     */
    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = new MenuManager("menu");
        return menuManager;
    }

    private void createNewDocConfig(String projectName) {

        DocConfigInputDlg dlg = new DocConfigInputDlg(mainShell, projectName, this.dvmConfig.getProjectNames(), this.dvmConfig.getApps());
        if (dlg.open() != Dialog.OK) {
            return;
        }

        String project = dlg.getProject();
        if (project.isEmpty()) {
            MessageDialog.openInformation(mainShell, "데이타 누락", "프로젝트 이름을 꼭~ 입력하시기 바랍니다.");
        }

        DocConfig docConfig = dlg.getDocConfig();

        ProjectConfig projectConfig = this.dvmConfig.getProjectConfig(project);
        if (projectConfig == null) {
            projectConfig = new ProjectConfig();
            projectConfig.setProject(project);

            this.dvmConfig.addProjectConfig(projectConfig);
        }

        if (!projectConfig.contains(docConfig.getDocKind())) {
            this.dvmConfig.addDocConfig(project, docConfig);
        }

        treeViewer.refresh();

        // detailed view 정보 갱신
        if (currentDetailedItem != null && ProjectConfig.class.isAssignableFrom(currentDetailedItem.getClass())) {
            String currentProject = ((ProjectConfig) currentDetailedItem).getProject();

            if (project.equals(currentProject)) {
                setDetailedView(projectConfig);
            }
        }
    }

    private void createProjectNavigation(SashForm sashForm) {

        this.cpProjectNavigation = new Composite(sashForm, SWT.NONE);
        GridLayout gl_cpProjectNavigation = new GridLayout(1, false);
        gl_cpProjectNavigation.marginWidth = 0;
        gl_cpProjectNavigation.marginHeight = 0;
        this.cpProjectNavigation.setLayout(gl_cpProjectNavigation);

        this.cpNaviMenus = new Composite(this.cpProjectNavigation, SWT.BORDER);
        GridLayout gl_cpNaviMenus = new GridLayout(2, false);
        gl_cpNaviMenus.marginWidth = 0;
        gl_cpNaviMenus.marginHeight = 0;
        gl_cpNaviMenus.verticalSpacing = 0;
        this.cpNaviMenus.setLayout(gl_cpNaviMenus);
        this.cpNaviMenus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        this.tbTree = new ToolBar(this.cpNaviMenus, SWT.RIGHT);
        this.tbTree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        ToolItem tltmExpandAll = new ToolItem(this.tbTree, SWT.NONE);
        tltmExpandAll.setToolTipText("Expand All.");
        tltmExpandAll.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/expandall.gif"));
        tltmExpandAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);

                Event event = new Event();
                event.display = getShell().getDisplay();
                event.doit = true;
                event.widget = treeProjectNavigator;

                for (TreeItem item : treeProjectNavigator.getItems()) {
                    event.item = item;

                    treeProjectNavigator.notifyListeners(SWT.Expand, event);
                    item.setExpanded(true);
                }
            }
        });

        ToolItem tltmCollapseAll = new ToolItem(this.tbTree, SWT.NONE);
        tltmCollapseAll.setToolTipText("Collapse All.");
        tltmCollapseAll.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/collapseall.gif"));
        tltmCollapseAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TreeItem item : treeProjectNavigator.getItems()) {
                    item.setExpanded(false);
                }
            }
        });

        this.tbConfig = new ToolBar(this.cpNaviMenus, SWT.FLAT | SWT.RIGHT);

        new ToolItem(this.tbConfig, SWT.SEPARATOR);

        ToolItem tltmNewDvm = new ToolItem(this.tbConfig, SWT.NONE);
        tltmNewDvm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StringInputDlg dlg = new StringInputDlg(getShell(), "");
                dlg.setDlgTitle("프로젝트 이름");
                dlg.setDlgMessage("프로젝트 이름을 입력하세요.");

                if (dlg.open() != Dialog.OK) {
                    return;
                }

                String newPrjName = dlg.getString();
                if (newPrjName.isEmpty()) {
                    MessageDialog.openWarning(getShell(), "데이터 누락", "프로젝트 이름이 누락 되었습니다.");
                    return;
                }

                clearProjectConfig();

                ProjectConfig newProject = new ProjectConfig();
                newProject.setProject(newPrjName);

                addProjectConfig(newProject);

                createDetailedLogo();

                treeViewer.refresh();

            }
        });
        tltmNewDvm.setToolTipText("Create a new DVM");
        tltmNewDvm.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/ic_action_copy.png"));

        new ToolItem(this.tbConfig, SWT.SEPARATOR);

        this.tltmNewProject = new ToolItem(this.tbConfig, SWT.NONE);
        this.tltmNewProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StringInputDlg dlg = new StringInputDlg(getShell(), "");
                dlg.setDlgTitle("프로젝트 이름");
                dlg.setDlgMessage("프로젝트 이름을 입력하세요.");

                if (dlg.open() != Dialog.OK) {
                    return;
                }

                String newPrjName = dlg.getString();
                if (newPrjName.isEmpty() || !dvmConfig.isNewProject(newPrjName)) {
                    MessageDialog.openWarning(getShell(), "데이터 중복", "동일한 이름의 프로젝트가 존재합니다.");
                    return;
                }

                ProjectConfig newProject = new ProjectConfig();
                newProject.setProject(newPrjName);

                addProjectConfig(newProject);

                treeViewer.refresh();
            }
        });
        this.tltmNewProject.setToolTipText("Add a new Project Config.");
        this.tltmNewProject.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/add_project.png"));

        this.tltmNewDoc = new ToolItem(this.tbConfig, SWT.NONE);
        this.tltmNewDoc.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createNewDocConfig(null);
            }
        });
        this.tltmNewDoc.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/osa_server_file_24x24.png"));
        this.tltmNewDoc.setToolTipText("Add a new Doc Config.");

        this.tltmSeparator = new ToolItem(this.tbConfig, SWT.SEPARATOR);
        this.tltmSeparator.setText("New Item");

        this.tltmSaveFile = new ToolItem(this.tbConfig, SWT.NONE);
        this.tltmSaveFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!dvmConfig.hasProject()) {

                    MessageDialog.openInformation(mainShell, "No Projects", "저장할 프로젝트 정보가 없습니다.");

                    return;
                }

                try {
                    // Map<String, ProjectConfig> configs = (Map<String, ProjectConfig>) treeViewer.getInput();
                    // Result<File> result = saveProject(JSONArrayFactory.toJSONArray(configs.values(),
                    // ProjectConfig.class).toString(2), resourceFile);

                    Result<File> result = saveProject(dvmConfig.toJSONString(2), resourceFile);

                    if (result.getResult()) {
                        resourceFile = result.getData().getAbsolutePath();
                        cbxConfigFile.setText(resourceFile);
                        addConfigFileList(resourceFile);
                        MessageDialog.openInformation(mainShell, "작업 완료", "성공적으로 파일을 저장하였습니다.");
                    } else {
                        if (result.getMessage() != null) {
                            MessageDialog.openInformation(mainShell, "파일 저장 실패", result.getMessage());
                        }
                    }
                } catch (JSONException e1) {
                    MessageDialog.openInformation(mainShell, "작업 완료", "성공적으로 파일을 저장하였습니다.");
                }
            }
        });
        this.tltmSaveFile.setImage(SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/ic_action_save.png"));
        this.tltmSaveFile.setToolTipText("Save Configurations.");

        treeViewer = new TreeViewer(this.cpProjectNavigation, SWT.BORDER);
        this.treeViewer.setComparator(new Sorter());
        this.treeProjectNavigator = treeViewer.getTree();
        this.treeProjectNavigator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        this.treeProjectNavigator.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                TreeItem item = treeProjectNavigator.getItem(new Point(e.x, e.y));
                if (item == null) {
                    return;
                }

                Object data = item.getData();

                if (data == null) {
                    return;
                }

                Class<?> dataClass = data.getClass();
                if (ProjectConfig.class.isAssignableFrom(dataClass)) {
                    Event event = new Event();
                    event.item = item;
                    event.display = getShell().getDisplay();
                    event.doit = true;
                    event.widget = treeProjectNavigator;

                    treeProjectNavigator.notifyListeners(SWT.Expand, event);
                    item.setExpanded(!item.getExpanded());
                } else if (DocConfig.class.isAssignableFrom(dataClass)) {
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
                Object obj = e.getSource();
                if (obj == null || !Tree.class.isAssignableFrom(obj.getClass())) {
                    return;
                }

                Tree tree = (Tree) obj;
                final TreeItem item = tree.getItem(new Point(e.x, e.y));
                if (item == null) {
                    return;
                }

                final Object data = item.getData();
                if (data == null) {
                    return;
                }

                switch (e.button) {
                    case 1: // Left Button
                        if (ProjectConfig.class.isAssignableFrom(data.getClass())) {
                            clearDetailedView();

                            setDetailedView((ProjectConfig) data);

                        } else if (DocConfig.class.isAssignableFrom(data.getClass())) {
                            clearDetailedView();

                            setDetailedView(((ProjectConfig) item.getParentItem().getData()).getProject(), (DocConfig) data);

                        }

                        scDetailedView.setMinSize(400, 300);
                        break;
                    case 3: // Right button

                        final Menu popmenu = new Menu((Control) e.getSource());

                        MenuItem deleteItem = new MenuItem(popmenu, SWT.NONE);
                        deleteItem.setText("&Delete");
                        deleteItem.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                if (MessageDialog.openConfirm(mainShell, "설정 삭제", "선택하신 설정을 삭제하시겠습니까?")) {

                                    if (ProjectConfig.class.isAssignableFrom(data.getClass())) {
                                        deleteConfig(((ProjectConfig) data).getProject(), null);
                                    } else if (DocConfig.class.isAssignableFrom(data.getClass())) {
                                        deleteConfig(((ProjectConfig) item.getParentItem().getData()).getProject(), ((DocConfig) data).getDocKind());
                                    }

                                    if (itsMe(data)) {
                                        clearDetailedView();
                                        createDetailedLogo();
                                    }
                                }
                                popmenu.dispose();
                            }
                        });

                        MenuItem newDocConfigItem = new MenuItem(popmenu, SWT.NONE);
                        newDocConfigItem.setText("&New Doc Config");
                        newDocConfigItem.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {

                                // (start) [BUG-FIX]: 신규 문서 추가시 현재 프로젝트 이름 설정 / Park_Jun_Hong_(parkjunhong77@gmail.com):
                                // 2019. 12. 15. 오후 6:43:18
                                String project = null;
                                if (ProjectConfig.class.isAssignableFrom(data.getClass())) {
                                    project = ((ProjectConfig) data).getProject();
                                } else if (DocConfig.class.isAssignableFrom(data.getClass())) {
                                    project = ((ProjectConfig) item.getParentItem().getData()).getProject();
                                }
                                // (end): 2019. 12. 15. 오후 6:43:18

                                createNewDocConfig(project);
                                popmenu.dispose();
                            }
                        });

                        if (ProjectConfig.class.isAssignableFrom(data.getClass())) {
                            MenuItem saveProjectStandalone = new MenuItem(popmenu, SWT.NONE);
                            saveProjectStandalone.setText("&Save Project");
                            saveProjectStandalone.addSelectionListener(new SelectionAdapter() {
                                @Override
                                public void widgetSelected(SelectionEvent e) {

                                    List<ProjectConfig> config = new ArrayList<>();
                                    config.add((ProjectConfig) data);

                                    try {
                                        // Result<File> result = saveProject(JSONArrayFactory.toJSONArray(config,
                                        // ProjectConfig.class).toString(2), null);
                                        Result<File> result = saveProject(dvmConfig.toJSONString(2), null);

                                        if (result.getResult()) {
                                            // (start) [BUG-FIX]: 파일 저장 취소에 따른 메시지 추가 /
                                            // Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 12. 15. 오후 6:47:01
                                            if (result.getData() != null) {
                                                MessageDialog.openInformation(mainShell, "작업 완료", "성공적으로 파일을 저장하였습니다.");
                                            } else {
                                                MessageDialog.openInformation(mainShell, "작업 완료", "파일을 저장하지 않았습니다.");
                                            }
                                            // (end): 2019. 12. 15. 오후 6:47:01

                                        } else {
                                            MessageDialog.openInformation(mainShell, "파일 저장 실패", result.getMessage());
                                        }

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    popmenu.dispose();
                                }
                            });
                        }

                        popmenu.setVisible(true);

                        break;
                }
            }
        });

        this.treeViewer.setContentProvider(new TreeContentProvider());
        this.treeViewer.setLabelProvider(new ViewerLabelProvider());

        this.treeViewer.setInput(this.dvmConfig.getProjectConfigs());
    }

    /**
     * Create the status line manager.
     * 
     * @return the status line manager
     */
    @Override
    protected StatusLineManager createStatusLineManager() {
        StatusLineManager statusLineManager = new StatusLineManager();
        return statusLineManager;
    }

    /**
     * Create the toolbar manager.
     * 
     * @return the toolbar manager
     */
    @Override
    protected ToolBarManager createToolBarManager(int style) {
        ToolBarManager toolBarManager = new ToolBarManager(style);
        return toolBarManager;
    }

    private void deleteConfig(String project, String docKind) {
        if (project != null && docKind != null) {
            ProjectConfig projectConfig = this.dvmConfig.getProjectConfig(project);
            if (projectConfig == null) {
                return;
            }
            projectConfig.removeDocConfig(docKind);
        } else if (project != null) {
            this.dvmConfig.removeProjectConfig(project);
        }

        treeViewer.refresh();
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(1356, 918);
    }

    private void init() {
        // create file
    }

    private boolean itsMe(Object item) {
        if (currentDetailedItem != null) {
            return currentDetailedItem.equals(item);
        }

        return false;
    }

    /**
     * 
     * @param data
     *            저장할 데이터 문자열. JSON 포맷
     * @param latestFilename
     *            기본 파일명
     *
     * @since 2014. 12. 2.
     */
    private Result<File> saveProject(String data, String latestFilename) {
        Result<File> result = new Result<>();

        File file = null;
        FileDialog dlg = new FileDialog(mainShell, SWT.SAVE);
        dlg.setFilterExtensions(new String[] { "*.dvm" });
        if (latestFilename != null) {
            dlg.setFileName(FileUtils.getFileName(latestFilename));
        }

        String filepath = dlg.open();

        if (filepath == null) {
            // (start) [BUG-FIX]: 파일 저장 취소에 따른 메시지 추가 / Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 12. 15. 오후 6:47:13
            result.andTrue().setMessage("파일을 저장하지 않았습니다.");
            // (end): 2019. 12. 15. 오후 6:47:13

            return result;
        }

        file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                result.setMessage("파일 생성시 에러가 발생하였습니다");
                return result;
            }
        } else {
            try {
                IOUtils.transfer(new FileInputStream(file), new FileOutputStream(new File(file.getAbsolutePath() + ".bak")), "UTF-8");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new StringReader(data));
            String readline = null;

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            while ((readline = reader.readLine()) != null) {
                writer.write(readline);
                writer.newLine();
            }

            writer.flush();

            result.andTrue().setData(file);

        } catch (Exception e1) {
            result.setMessage(e1.getLocalizedMessage());
        } finally {
            IOUtils.close(reader, writer);
        }

        return result;

    }

    private void setDetailedView(ProjectConfig projectConfig) {

        ProjectConfigView projectConfigView = new ProjectConfigView(scDetailedView, SWT.NONE);
        projectConfigView.setProjectConfig(projectConfig);
        projectConfigView.addProjectConfigChangeListener(DocVersionManagerMainUI.this);
        scDetailedView.setContent(projectConfigView);

        updateCurrentDetailedItem(projectConfig);
    }

    private void setDetailedView(String project, DocConfig docConfig) {
        DocConfigView docConfigView = new DocConfigView(scDetailedView, SWT.NONE, this.dvmConfig.getApps());
        docConfigView.setConfig(project, docConfig);
        docConfigView.addDocConfigChangeListener(DocVersionManagerMainUI.this);
        scDetailedView.setContent(docConfigView);

        updateCurrentDetailedItem(docConfig);
    }

    private void setProjectConfig(TreeViewer viewer) {
        viewer.setInput(this.dvmConfig.getProjectConfigs());
    }

    private void updateCurrentDetailedItem(Object item) {
        this.currentDetailedItem = item;
    }

    private void updateCurrentDetailedView(String project, Object data) {
        if (data == null) {

        } else if (ProjectConfig.class.isAssignableFrom(data.getClass())) {
            clearDetailedView();

            ProjectConfigView projectConfigView = new ProjectConfigView(scDetailedView, SWT.NONE);
            projectConfigView.setProjectConfig((ProjectConfig) data);
            projectConfigView.addProjectConfigChangeListener(DocVersionManagerMainUI.this);
            scDetailedView.setContent(projectConfigView);

            updateCurrentDetailedItem(data);

        } else if (DocConfig.class.isAssignableFrom(data.getClass())) {
            clearDetailedView();

            DocConfigView docConfigView = new DocConfigView(scDetailedView, SWT.NONE, this.dvmConfig.getApps());
            docConfigView.setConfig(project, (DocConfig) data);
            docConfigView.addDocConfigChangeListener(DocVersionManagerMainUI.this);
            scDetailedView.setContent(docConfigView);

            updateCurrentDetailedItem(data);
        }

        treeViewer.refresh();
    }

    private void updateResourceFile(String filename) {
        this.resourceFile = filename;
    }

    public static UpdateLogDigalog getLogView() {
        return logDlg;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
        try {
            DocVersionManagerMainUI window = new DocVersionManagerMainUI();
            window.setBlockOnOpen(true);
            window.open();
            Display.getCurrent().dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private static class Sorter extends ViewerSorter {
    private static class Sorter extends ViewerComparator {
        public int compare(Viewer viewer, Object e1, Object e2) {

            if (ProjectConfig.class.isAssignableFrom(e1.getClass()) //
                    && ProjectConfig.class.isAssignableFrom(e2.getClass())) {
                String project1 = ((ProjectConfig) e1).getProject();
                String project2 = ((ProjectConfig) e2).getProject();

                int orderP1 = project1.charAt(0);
                int orderP2 = project2.charAt(0);

                int c = orderP1 - orderP2;

                if (c != 0) {
                    return c;
                }

                String yearP1 = null;
                String yearP2 = null;

                switch (orderP1) {
                    case '0':
                        return project1.compareTo(project2);
                    case '[':
                        project1 = project1.substring(9);
                        project2 = project2.substring(9);
                    default:
                        // 년도별로는 내림차순
                        yearP1 = project1.substring(0, 4);
                        yearP2 = project2.substring(0, 4);

                        c = yearP1.compareTo(yearP2);

                        if (c != 0) {
                            return project2.compareTo(project1);
                        } else {
                            return project1.compareTo(project2);
                        }
                }
            } else {
                return 0;
            }
        }

    }

    private static class TreeContentProvider implements ITreeContentProvider {
        public void dispose() {
        }

        @SuppressWarnings("unchecked")
        public Object[] getChildren(Object parentElement) {
            Class<?> parentClass = parentElement.getClass();
            if (Map.class.isAssignableFrom(parentClass)) {
                return ((Map<String, ProjectConfig>) parentElement).values().toArray();
            } else if (ProjectConfig.class.isAssignableFrom(parentClass)) {
                return ((ProjectConfig) parentElement).getDocConfigs().toArray();
            } else {
                return new Object[0];
            }
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private static class ViewerLabelProvider extends LabelProvider {
        public Image getImage(Object element) {
            Class<?> elemClass = element.getClass();

            if (ProjectConfig.class.isAssignableFrom(elemClass)) {
                return SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/project.png");
            } else if (DocConfig.class.isAssignableFrom(elemClass)) {
                return SWTResourceManager.getImage(DocVersionManagerMainUI.class, "/images/doc.png");
            } else {
                return null;
            }
        }

        public String getText(Object element) {
            Class<?> elemClass = element.getClass();

            if (ProjectConfig.class.isAssignableFrom(elemClass)) {
                return ((ProjectConfig) element).getProject();
            } else if (DocConfig.class.isAssignableFrom(elemClass)) {
                return ((DocConfig) element).getDocKind();
            } else {
                return null;
            }
        }
    }
}
