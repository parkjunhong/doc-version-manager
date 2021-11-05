/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 24. 오전 11:10:01
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.widget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.wb.swt.TableViewerColumnSorter;

import open.commons.tool.dvm.core.FileReplacer;
import open.commons.tool.dvm.core.FileVersion3L;
import open.commons.tool.dvm.core.RevisionFile;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.tool.dvm.json.ProjectConfig;
import open.commons.tool.dvm.ui.DocVersionManagerMainUI;
import open.commons.tool.dvm.ui.IProjectConfigChangeListener;
import open.commons.utils.FileUtils;

public class ProjectConfigView extends Composite implements IUpdateLogListener {
    private static final String REGEX_VERSION = "\\d+(\\.\\d+){0,2}";

    static SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ToolBar tbTitle;

    private Composite cpMenu;

    private Set<IProjectConfigChangeListener> listeners = new HashSet<>();
    private ScrolledComposite scrolledComposite;

    private Table table;

    private TableViewer tableViewer;

    private ProjectConfig projectConfig;

    private UpdateLogDigalog logView;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ProjectConfigView(Composite parent, int style) {
        super(parent, style);

        logView = DocVersionManagerMainUI.getLogView();

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);

        this.cpMenu = new Composite(this, SWT.BORDER);
        this.cpMenu.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_cpMenu = new GridLayout(2, false);
        gl_cpMenu.verticalSpacing = 0;
        gl_cpMenu.marginWidth = 0;
        gl_cpMenu.horizontalSpacing = 0;
        gl_cpMenu.marginHeight = 0;
        this.cpMenu.setLayout(gl_cpMenu);

        this.tbTitle = new ToolBar(this.cpMenu, SWT.FLAT | SWT.RIGHT);

        ToolBar tbActions = new ToolBar(this.cpMenu, SWT.FLAT | SWT.RIGHT);
        tbActions.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        ToolItem tltmToggleLogDlg = new ToolItem(tbActions, SWT.NONE);
        tltmToggleLogDlg.setToolTipText("Vew Logs.");
        tltmToggleLogDlg.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (logView.getShell() == null) {
                    logView.open();
                } else {
                    Shell shell = logView.getShell();

                    shell.setVisible(!shell.getVisible());

                    if (shell.getVisible()) {
                        shell.setFocus();
                    }
                }
            }
        });
        tltmToggleLogDlg.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/ic_action_go_to_today_24x24.png"));

        ToolItem tltmUpdateAll = new ToolItem(tbActions, SWT.NONE);
        tltmUpdateAll.setText("Update All");
        tltmUpdateAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                final List<FileUpdateData> fuds = new ArrayList<>();

                for (TableItem item : table.getItems()) {
                    fuds.add((FileUpdateData) item.getData());
                }

                final ToolItem item = (ToolItem) e.getSource();
                item.setEnabled(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<String> logs = new ArrayList<>();

                        for (FileUpdateData fud : fuds) {
                            logs.addAll(updateFile0(fud));
                        }

                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                log(logs);
                                item.setEnabled(true);

                                fireProjectConfigChanged(projectConfig);

                            }
                        });

                    }
                }).start();

            }
        });
        tltmUpdateAll.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/osa_ics_drive.png"));

        this.scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        this.scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        this.scrolledComposite.setExpandHorizontal(true);
        this.scrolledComposite.setExpandVertical(true);

        tableViewer = new TableViewer(this.scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
        // this.tableViewer.setSorter(new Sorter());
        this.tableViewer.setComparator(new Sorter());
        this.table = tableViewer.getTable();
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                switch (e.button) {
                    case 1: // left button
                        if (mouseOnColumn(e, ColumnLabelType.UPDATE.getColumnIndex())) {
                            TableItem item = mouseOnItem(e);

                            Font font = item.getFont(ColumnLabelType.UPDATE.getColumnIndex());
                            item.setData(genItemKey(item, "font", ColumnLabelType.UPDATE.getColumnIndex()), font);
                            FontData fd = font.getFontData()[0];
                            item.setFont(ColumnLabelType.UPDATE.getColumnIndex(), new Font(font.getDevice(), fd.getName(), fd.getHeight(), fd.getStyle() | SWT.BOLD));
                        }
                        break;
                    case 2: // middle button
                        break;
                    case 3: // right button
                        break;
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {

                switch (e.button) {
                    case 1: // left button
                        // 문서 정보 갱신
                        if (mouseOnColumn(e, ColumnLabelType.UPDATE.getColumnIndex())) {
                            final TableItem item = mouseOnItem(e);
                            Font font = item.getFont(ColumnLabelType.UPDATE.getColumnIndex());
                            FontData fd = font.getFontData()[0];
                            item.setFont(ColumnLabelType.UPDATE.getColumnIndex(), new Font(font.getDevice(), fd.getName(), fd.getHeight(), fd.getStyle() | SWT.ITALIC));

                            table.redraw();

                            final FileUpdateData fud = (FileUpdateData) item.getData();

                            if (fud.document == null || fud.document.trim().isEmpty() //
                                    || fud.version == null || fud.version.trim().isEmpty()//
                            // begin - PATCH [2014. 12. 4.]: 배치파일을 통한 문서 접근을 제거함. |
                            // Park_Jun_Hong_(parkjunhong77@gmail.com)
                            // || fud.docConfig.getExeCmd() == null || fud.docConfig.getExeCmd().trim().isEmpty()
                            // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2014. 12. 4.
                            ) {
                                MessageDialog.openWarning(getShell(), "문서 갱신 실패", "실제 파일이 존재하지 않거나 버전 정보가 올바르지 않습니다.\n정보: " + fud.toString());

                                item.setFont(ColumnLabelType.UPDATE.getColumnIndex(), (Font) item.getData(genItemKey(item, "font", ColumnLabelType.UPDATE.getColumnIndex())));

                                return;
                            }

                            updateFile(fud, item);

                        } else
                        // 파일 열기
                        if (mouseOnColumn(e, ColumnLabelType.OPEN_FILE.getColumnIndex())) {
                            final TableItem item = mouseOnItem(e);
                            final FileUpdateData fud = (FileUpdateData) item.getData();
                            DocConfig docConfig = fud.docConfig;

                            if (fud.document == null || fud.document.trim().isEmpty() //
                                    || fud.version == null || fud.version.trim().isEmpty()//
                                    || fud.docConfig.getExeCmd() == null || fud.docConfig.getExeCmd().trim().isEmpty()) {
                                MessageDialog.openWarning(getShell(), "문서 열기 실패", "실제 파일이 존재하지 않거나 버전 정보가 올바르지 않습니다.\n정보: " + fud.toString());

                                return;
                            }

                            openFile(docConfig);

                        } else
                        // 파일이 있는 디렉토리 열기
                        if (mouseOnColumn(e, ColumnLabelType.OPEN_DIR.getColumnIndex())) {
                            final TableItem item = mouseOnItem(e);
                            FileUpdateData fud = (FileUpdateData) item.getData();
                            final DocConfig docConfig = fud.docConfig;

                            if (fud.document == null || fud.document.trim().isEmpty() //
                                    || fud.version == null || fud.version.trim().isEmpty()//
                                    || fud.docConfig.getFileDir() == null || fud.docConfig.getFileDir().trim().isEmpty()) {
                                MessageDialog.openWarning(getShell(), "폴더 열기 실패", "실제 파일이 존재하지 않거나 버전 정보가 올바르지 않습니다.\n정보: " + fud.toString());

                                return;
                            }

                            openDirectory(docConfig);
                        }
                        break;
                    case 2: // middle button
                        break;
                    case 3: // right button
                        Table table = ((Table) e.widget);
                        TableItem item = table.getItem(new Point(e.x, e.y));

                        if (item == null) {
                            return;
                        }

                        FileUpdateData fud = (FileUpdateData) item.getData();

                        final String backupDir = fud.docConfig.getBackupDir();
                        if (backupDir == null || !new File(backupDir).exists()) {
                            return;
                        }

                        final Menu popmenu = new Menu((Control) e.getSource());

                        MenuItem openBackupDirItem = new MenuItem(popmenu, SWT.NONE);
                        openBackupDirItem.setText("&Open Backup Directory");
                        openBackupDirItem.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                try {
                                    Runtime runtime = Runtime.getRuntime();
                                    Process proc = runtime.exec(new String[] { "explorer", backupDir });
                                    proc.getInputStream().close();
                                } catch (IOException ignored) {
                                }

                                popmenu.dispose();
                            }
                        });

                        popmenu.setVisible(true);

                        break;
                }

            }
        });
        this.table.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {

                if (mouseOnColumns(e, ColumnLabelType.UPDATE.getColumnIndex(), ColumnLabelType.OPEN_FILE.getColumnIndex(), ColumnLabelType.OPEN_DIR.getColumnIndex())) {
                    table.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
                } else {
                    table.setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
                }
            }
        });

        this.table.setFont(SWTResourceManager.getFont("새굴림", 9, SWT.BOLD));
        this.table.setHeaderVisible(true);
        this.table.setLinesVisible(true);

        TableViewerColumn tvcDocKind = new TableViewerColumn(this.tableViewer, SWT.LEFT);
        new TableViewerColumnSorter(tvcDocKind) {
            @Override
            protected Object getValue(Object o) {
                return ((FileUpdateData) o).docConfig.getDocKind();
            }
        };
        tvcDocKind.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.DOC_KIND));
        TableColumn tblclmnDocKind = tvcDocKind.getColumn();
        tblclmnDocKind.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/osa_server_file_24x24.png"));
        tblclmnDocKind.setWidth(213);
        tblclmnDocKind.setText("Doc Kind");

        TableViewerColumn tvcDocument = new TableViewerColumn(this.tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tvcDocument) {
            protected Object getValue(Object o) {
                return ((FileUpdateData) o).document;
            }
        };
        tvcDocument.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.DOCUMENT));
        TableColumn tblclmDocument = tvcDocument.getColumn();
        tblclmDocument.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/doc_24x24.png"));
        tblclmDocument.setWidth(369);
        tblclmDocument.setText("Document");

        TableViewerColumn tvcVersion = new TableViewerColumn(this.tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tvcVersion) {
            @Override
            protected Object getValue(Object o) {
                return new FileVersion3L(((FileUpdateData) o).version);
            }
        };
        tvcVersion.setEditingSupport(new EditingSupport(this.tableViewer) {
            protected boolean canEdit(Object element) {
                return true;
            }

            protected CellEditor getCellEditor(Object element) {
                TextCellEditor tcEditor = new TextCellEditor(table);
                tcEditor.setValidator(new ICellEditorValidator() {
                    @Override
                    public String isValid(Object value) {
                        if (value == null) {
                            return null;
                        }
                        return value.toString().matches(REGEX_VERSION) ? null : value.toString();
                    }
                });

                return tcEditor;
            }

            protected Object getValue(Object element) {
                return ((FileUpdateData) element).version;
            }

            protected void setValue(Object element, Object value) {
                ((FileUpdateData) element).version = value.toString();
                tableViewer.refresh();
            }
        });
        tvcVersion.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.VERSION));
        TableColumn tblclmnVersion = tvcVersion.getColumn();
        tblclmnVersion.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/ic_action_go_to_today_24x24.png"));
        tblclmnVersion.setWidth(119);
        tblclmnVersion.setText("Version");

        TableViewerColumn tvcUpdate = new TableViewerColumn(this.tableViewer, SWT.NONE);
        tvcUpdate.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.UPDATE));

        TableColumn tblclmnUpdate = tvcUpdate.getColumn();
        tblclmnUpdate.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/ic_action_play_24x24.png"));
        tblclmnUpdate.setWidth(116);
        tblclmnUpdate.setText("Update");

        TableViewerColumn tvcOpenFile = new TableViewerColumn(this.tableViewer, SWT.NONE);
        tvcOpenFile.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.OPEN_FILE));

        TableColumn tblclmnOpenFile = tvcOpenFile.getColumn();
        tblclmnOpenFile.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/ic_action_paste.png"));
        tblclmnOpenFile.setWidth(116);
        tblclmnOpenFile.setText("File");

        TableViewerColumn tvcOpenDir = new TableViewerColumn(this.tableViewer, SWT.NONE);
        tvcOpenDir.setLabelProvider(new FileManageLabelProvider(ColumnLabelType.OPEN_DIR));

        TableColumn tblclmnOpenDir = tvcOpenDir.getColumn();
        tblclmnOpenDir.setImage(SWTResourceManager.getImage(ProjectConfigView.class, "/images/directory_24x24.png"));
        tblclmnOpenDir.setWidth(100);
        tblclmnOpenDir.setText("Directory");

        this.tableViewer.setContentProvider(new ContentProvider());

        this.scrolledComposite.setContent(this.table);
        this.scrolledComposite.setMinSize(this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    public void addProjectConfigChangeListener(IProjectConfigChangeListener l) {
        if (l != null) {
            this.listeners.add(l);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private void fireProjectConfigChanged(ProjectConfig projectConfig) {
        for (IProjectConfigChangeListener l : listeners) {
            l.change(projectConfig, projectConfig);
        }
    }

    private RevisionFile loadRealFilepath(DocConfig docConfig) {
        return FileReplacer.getTargetFile(docConfig.getFileDir(), docConfig.getFilename(), docConfig.getFileExt(), docConfig.getDatePattern());
    }

    @Override
    public void log(List<String> logs) {

        if (logView.getShell() == null) {
            logView.open();
        } else {
            Shell shell = logView.getShell();

            shell.setVisible(true);
            shell.setFocus();
        }

        logView.addLog(logs);
    }

    private boolean mouseOnColumn(MouseEvent e, int column) {
        Table table = ((Table) e.widget);
        TableItem item = table.getItem(new Point(e.x, e.y));

        if (item == null) {
            return false;
        }

        if (column > table.getColumnCount() - 1) {
            return false;
        }

        return item.getBounds(column).contains(e.x, e.y);
    }

    private boolean mouseOnColumns(MouseEvent e, int... columns) {
        Table table = ((Table) e.widget);
        TableItem item = table.getItem(new Point(e.x, e.y));

        if (item == null) {
            return false;
        }

        for (int column : columns) {
            if (column > table.getColumnCount() - 1) {
                continue;
            }

            if (item.getBounds(column).contains(e.x, e.y)) {
                return true;
            }
        }

        return false;

    }

    private TableItem mouseOnItem(MouseEvent e) {
        Table table = ((Table) e.widget);
        TableItem item = table.getItem(new Point(e.x, e.y));

        return item;
    }

    private void openDirectory(final DocConfig docConfig) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    RevisionFile revFile = FileReplacer.getTargetFile(docConfig.getFileDir(), docConfig.getFilename(), docConfig.getFileExt(), docConfig.getDatePattern());
                    if (revFile == null) {
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                MessageDialog.openWarning(getShell(), "디렉토리 열기", "문서가 존재하지 않습니다.\n정보: " + docConfig.toString());
                            }
                        });
                        return;
                    }

                    Runtime runtime = Runtime.getRuntime();
                    Process proc = runtime.exec(new String[] { "explorer", docConfig.getFileDir() });
                    proc.getInputStream().close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openFile(final DocConfig docConfig) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    RevisionFile revFile = FileReplacer.getTargetFile(docConfig.getFileDir(), docConfig.getFilename(), docConfig.getFileExt(), docConfig.getDatePattern());
                    if (revFile == null) {
                        Display.getDefault().asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.openWarning(getShell(), "문서 열기 실패", "문서가 존재하지 않습니다.\n정보: " + docConfig.toString());
                            }
                        });

                        return;
                    }

                    // begin - PATCH [2014. 12. 4.]: 배치파일을 통한 문서 접근을 제거함. | Park_Jun_Hong_(parkjunhong77@gmail.com)
                    // final File batchFile = new File(docConfig.getFileDir(), docConfig.getFilename() +
                    // ".bat");
                    // if( !batchFile.exists()) {
                    //
                    // updateFile(fud);
                    //
                    // if( !new File(docConfig.getFileDir(), docConfig.getFilename() + ".bat").exists()) {
                    // Display.getDefault().asyncExec(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // MessageDialog.openWarning(getShell(), "문서 열기 실패",
                    // "문서을 열기 위한 batch 파일이 존재하지 않습니다.\n정보: " + batchFile.getAbsolutePath());
                    // }
                    // });
                    // return;
                    // }
                    // }
                    // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2014. 12. 4.

                    Runtime runtime = Runtime.getRuntime();
                    Process proc = runtime.exec(new String[] { docConfig.getExeCmd(), revFile.getSource().getAbsolutePath() });
                    proc.getInputStream().close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setProjectConfig(final ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;

        if (projectConfig != null) {
            // for Project
            ToolItem tltmTitle = new ToolItem(tbTitle, SWT.NONE);
            tltmTitle.setImage(SWTResourceManager.getImage(DocConfigView.class, "/images/project.png"));
            tltmTitle.setText(projectConfig.getProject());
            tltmTitle.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    StringInputDlg dlg = new StringInputDlg(getShell(), projectConfig.getProject());

                    if (dlg.open() != Dialog.OK) {
                        return;
                    }

                    String newPrjName = dlg.getString();
                    if (newPrjName.isEmpty() || projectConfig.getProject().equals(newPrjName)) {
                        return;
                    }

                    ProjectConfig oldValue = new ProjectConfig();
                    try {
                        oldValue.mature(projectConfig.toJSONString());
                    } catch (JSONException e1) {
                        MessageDialog.openWarning(getShell(), "어떡하냥 ㅠ.ㅠ.", "프로젝트 이름을 변경하는 도중 에러 발생.\n");
                    }

                    projectConfig.setProject(newPrjName);

                    for (IProjectConfigChangeListener l : listeners) {
                        l.change(projectConfig, oldValue);
                    }
                }
            });

            if (projectConfig.getDocConfigs().size() < 1) {
                return;
            }

            List<FileUpdateData> fuds = new ArrayList<>();
            FileUpdateData fud = null;
            for (DocConfig docConfig : projectConfig.getDocConfigs()) {
                RevisionFile file = loadRealFilepath(docConfig);

                fud = new FileUpdateData(docConfig.getDocKind()//
                        , file != null ? FileUtils.getFileName(file.getSource().getAbsolutePath()) : ""//
                        , file != null ? file.getVersion().toString() : "" //
                        , docConfig);

                fuds.add(fud);
            }

            // set data
            tableViewer.setInput(fuds);
        }
    }

    private void updateFile(final FileUpdateData fud, final TableItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> logs = updateFile0(fud);

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {

                        log(logs);

                        item.setFont(ColumnLabelType.UPDATE.getColumnIndex(), (Font) item.getData(genItemKey(item, "font", ColumnLabelType.UPDATE.getColumnIndex())));

                        fireProjectConfigChanged(projectConfig);
                    }
                });
            }
        }).start();
    }

    private List<String> updateFile0(FileUpdateData fud) {

        if (fud.version == null || fud.version.trim().isEmpty()) {
            List<String> logs = new ArrayList<>();
            logs.add(logDate.format(new Date(System.currentTimeMillis())) + " | 문서의 버전 정보가 잘못되었습니다. version: " + fud.version + ", object: " + fud + "\n");
            return logs;
        }

        return FileReplacer.execute(fud.docConfig, new FileVersion3L(fud.version));
    }

    private static String genItemKey(Object object, String category, Object additional) {
        return object.hashCode() + "#" + category + (additional != null ? "-" + additional.toString() : "");
    }

    enum ColumnLabelType {
        DOC_KIND(0), DOCUMENT(1), VERSION(2), UPDATE(3), OPEN_FILE(4), OPEN_DIR(5);

        private int index;

        private ColumnLabelType(int idx) {
            this.index = idx;
        }

        public int getColumnIndex() {
            return this.index;
        }
    }

    private static class ContentProvider implements IStructuredContentProvider {
        public void dispose() {
        }

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement == null //
                    || !Collection.class.isAssignableFrom(inputElement.getClass())) {
                return new Object[0];
            }

            return ((Collection<FileUpdateData>) inputElement).toArray();
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    class FileManageLabelProvider extends ColumnLabelProvider {
        private ColumnLabelType labelType;

        public FileManageLabelProvider(ColumnLabelType labelType) {
            this.labelType = labelType;
        }

        @Override
        public Font getFont(Object element) {
            return SWTResourceManager.getFont("새굴림", 9, SWT.NORMAL);
        }

        @Override
        public Image getImage(Object element) {

            if (element == null || !FileUpdateData.class.isAssignableFrom(element.getClass())) {
                return null;
            }

            Image image = null;
            switch (labelType) {
                case UPDATE:
                    image = SWTResourceManager.getImage(ProjectConfigView.class, "/images/osa_ics_drive.png");
                    break;
                case OPEN_FILE:
                    image = SWTResourceManager.getImage(ProjectConfigView.class, "/images/new_doc.png");
                    break;
                case OPEN_DIR:
                    image = SWTResourceManager.getImage(ProjectConfigView.class, "/images/directory_24x24.png");
                    break;
                default:
                    break;
            }

            return image;
        }

        @Override
        public String getText(Object element) {
            if (element == null || !FileUpdateData.class.isAssignableFrom(element.getClass())) {
                return "";
            }

            FileUpdateData fud = (FileUpdateData) element;
            String text = null;
            switch (labelType) {
                case DOC_KIND:
                    text = fud.docConfig.getDocKind();
                    break;
                case DOCUMENT:
                    text = fud.document;
                    break;
                case VERSION:
                    text = fud.version;
                    break;
                case UPDATE:
                    text = "Update";
                    break;
                case OPEN_FILE:
                case OPEN_DIR:
                    text = "Open";
                    break;
                default:
                    text = "";
                    break;
            }

            return text;
        }

        @Override
        public String getToolTipText(Object element) {
            if (element == null || !FileUpdateData.class.isAssignableFrom(element.getClass())) {
                return "";
            }

            FileUpdateData fud = (FileUpdateData) element;
            DocConfig docConfig = (DocConfig) element;
            RevisionFile file = loadRealFilepath(docConfig);
            String text = null;
            switch (labelType) {
                case DOC_KIND:
                    text = fud.docConfig.getDocKind();
                    break;
                case DOCUMENT:
                    text = file != null ? file.getSource().getAbsolutePath() : "";
                    break;
                case VERSION:
                    text = fud.version;
                    break;
                case UPDATE:
                    text = "Update";
                    break;
                case OPEN_FILE:
                    text = "Open File";
                    break;
                case OPEN_DIR:
                    text = "Open Directory";
                    break;
                default:
                    text = "";
                    break;
            }

            return text;
        }
    }

    class FileUpdateData {
        public String document;
        public String version;

        public DocConfig docConfig;

        FileUpdateData(String d, String f, String v, DocConfig docConfig) {
            this.document = f;
            this.version = v;
            this.docConfig = docConfig;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "FileUpdateData [document=" + document + ", version=" + version + ", docConfig=" + docConfig + "]";
        }
    }

    // private static class Sorter extends ViewerSorter {
    private static class Sorter extends ViewerComparator {
        public int compare(Viewer viewer, Object e1, Object e2) {
            return ((FileUpdateData) e1).docConfig.getDocKind().compareTo(((FileUpdateData) e2).docConfig.getDocKind());
        }
    }
}
