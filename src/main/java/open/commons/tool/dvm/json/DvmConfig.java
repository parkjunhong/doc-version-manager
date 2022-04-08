/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 12. 9. 오후 1:14:58
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import open.commons.json.annotation.JSONField;
import open.commons.json.model.DefaultJSONModel;
import open.commons.core.utils.ComparableUtils;

/**
 * <a href="http://tools.ietf.org/html/rfc7159">JSON</a> source: <br>
 * 
 * <pre>
 * 
 * [CASE - 0]
 * 
 * {
 *   "apps": [
 *     {
 *       "fileExts": [
 *         "doc",
 *         "docx"
 *       ],
 *       "appPaths": [
 *         "C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.EXE"
 *       ]
 *     }
 *   ],
 *   "projects": [
 *     {
 *       "project": "테스트 프로젝트",
 *       "docConfigs": [
 *         {
 *           "exeCmd": "C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.EXE",
 *           "batchFileEndcoding": "EUC-KR",
 *           "fileDir": "파일 경로",
 *           "fileExt": "doc|docx",
 *           "docKind": "문서 종류",
 *           "filename": "파일이름",
 *           "backupDir": "백업경로",
 *           "datePattern": "날짜패턴"
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 */
public class DvmConfig extends DefaultJSONModel {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "apps")
    private List<Application> apps = new ArrayList<>();

    @JSONField(name = "projects")
    private List<ProjectConfig> projects = new ArrayList<>();

    private ConcurrentSkipListMap<String, ProjectConfig> configs = new ConcurrentSkipListMap<>();

    private static final Comparator<Application> APP_COMPARATOR = new Comparator<Application>() {

        @Override
        public int compare(Application o1, Application o2) {
            return ComparableUtils.comparable(new String[] { o1.getAppPath() }, new String[] { o2.getAppPath() });
        }
    };

    private static final Comparator<ProjectConfig> PRJ_CONFIG_COMPARATOR = new Comparator<ProjectConfig>() {

        @Override
        public int compare(ProjectConfig o1, ProjectConfig o2) {
            return ComparableUtils.comparable(new String[] { o1.getProject() }, new String[] { o2.getProject() });
        }
    };

    public DvmConfig() {
    }
    
    public DvmConfig(JSONObject json) throws JSONException {
        mature(json);
    }

    public DvmConfig(String jsonStr) throws JSONException {
        mature(jsonStr);
    }
    
    @Override
    protected void mature0(JSONObject json) throws JSONException {
        super.mature0(json);
        
        
        for(ProjectConfig config : this.projects) {
            this.configs.put(config.getProject(), config);
        }
    }

    public void addApplication(String appPath, String... fileExts) {
        Application app = getApplicationByPath(appPath);

        if (app == null) {
            app = new Application();
            app.setAppPath(appPath);

            this.apps.add(app);
        }

        app.addFileExtensions(fileExts);

        sortApplications();
    }

    public void addProjectConfig(ProjectConfig config) {
        ProjectConfig any = this.configs.get(config.getProject());

        if (any == null) {
            this.projects.add(config);
            putConfig(config);
        } else {
            if (any != config) {
                any.setDocConfigs(config.getDocConfigs());
            }
        }
        
        for(DocConfig docConfig : config.getDocConfigs()){
            addApplication(docConfig.getExeCmd(), docConfig.getFileExt().split("[|]"));
        }

        sortProjectConfigs();
    }
    
    public void addDocConfig(String project, DocConfig docConfig) {
        ProjectConfig projectConfig = this.configs.get(project);
        if( projectConfig == null) {
            projectConfig = new ProjectConfig();
            projectConfig.setProject(project);
        }
        
        projectConfig.addDocConfig(docConfig);
        
        String exeCmd = docConfig.getExeCmd();
        if( exeCmd == null ){//||  !new File(exeCmd).exists()) {
            return ;
        }
        
        String fileExts = docConfig.getFileExt();
        if( fileExts == null || fileExts.isEmpty()) {
            return ;
        }
        
        Application app = getApplicationByPath(exeCmd);
        if( app == null) {
            app = new Application();
            app.setAppPath(exeCmd);
            
            this.apps.add(app);
        }
        
        app.addFileExtensions(fileExts.split("[|]"));
        
    }

    public void clearApplication() {
        this.apps.clear();
    }

    public void clearProjectConfigs() {
        this.projects.clear();
        this.configs.clear();
        this.apps.clear();
    }

    public Application getApplicationByFileExts(String... fileExts) {
        return null;
    }

    public Application getApplicationByPath(String appPath) {
        for (Application app : apps) {
            if (appPath.equalsIgnoreCase(app.getAppPath())) {
                return app;
            }
        }

        return null;
    }

    /**
     *
     * @return apps
     *
     * @since 2014. 12. 09.
     */
    public List<Application> getApps() {
        return this.apps;
    }

    public ProjectConfig getProjectConfig(String project) {
        return this.configs.get(project);
    }

    public Map<String, ProjectConfig> getProjectConfigs() {
        return this.configs;
    }

    public String[] getProjectNames() {
        return this.configs.keySet().toArray(new String[] {});
    }

    /**
     *
     * @return projects
     *
     * @since 2014. 12. 09.
     */
    public List<ProjectConfig> getProjects() {
        return this.projects;
    }

    public boolean hasApplication() {
        return this.apps.size() > 0;
    }

    public boolean hasProject() {
        return this.projects.size() > 0;
    }

    public boolean isNewProject(String project) {
        return !this.configs.containsKey(project);
    }

    private void putConfig(ProjectConfig config) {
        this.configs.put(config.getProject(), config);
    }

    public void removeProjectConfig(String project) {
        ProjectConfig config = this.configs.remove(project);

        if (config != null) {
            this.projects.remove(config);
        }
    }

    /**
     *
     * @param apps
     *            apps to set.
     *
     * @since 2014. 12. 09.
     */
    public void setApps(List<Application> apps) {
        this.apps = apps != null ? apps : new ArrayList<Application>();

        sortApplications();
    }

    /**
     *
     * @param projects
     *            projects to set.
     *
     * @since 2014. 12. 09.
     */
    public void setProjects(List<ProjectConfig> projects) {
        this.projects = projects != null ? projects : new ArrayList<ProjectConfig>();

        sortProjectConfigs();

        for (ProjectConfig project : this.projects) {
            this.configs.put(project.getProject(), project);
        }
    }

    private void sortApplications() {
        Collections.sort(this.apps, APP_COMPARATOR);
    }

    private void sortProjectConfigs() {
        Collections.sort(this.projects, PRJ_CONFIG_COMPARATOR);
    }
}