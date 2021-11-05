/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 21. 오후 1:44:01
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.json;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import open.commons.json.annotation.JSONField;
import open.commons.json.model.DefaultJSONModel;

/**
 * <a href="http://tools.ietf.org/html/rfc7159">JSON</a> source: <br>
 * 
 * <pre>
 * 
 *  [CASE - 0]
 *  
 * {
 *      "project": "테스트 프로젝트",
 *      "docConfigs": [
 *          {
 *              "exeCmd": "C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.EXE",
 *              "batchFileEndcoding": "EUC-KR",
 *              "fileDir": "파일 경로",
 *              "fileExt": "doc|docx",
 *              "docKind": "문서 종류",
 *              "filename": "파일이름",
 *              "backupDir": "백업경로",
 *              "datePattern": "날짜패턴"
 *          }
 *      ]
 * }
 * </pre>
 */
public class ProjectConfig extends DefaultJSONModel implements Comparable<ProjectConfig> {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "project")
    private String project;

    @JSONField(name = "docConfigs")
    private NavigableSet<DocConfig> docConfigs = new ConcurrentSkipListSet<>();

    public ProjectConfig() {
    }

    public ProjectConfig(String project) {
        this.project = project;
    }

    void addDocConfig(DocConfig... docConfigs) {
        for (DocConfig docConfig : docConfigs) {
            this.docConfigs.add(docConfig);
        }
    }

    public void removeDocConfig(String docKind) {
        for (DocConfig config : docConfigs) {
            if (config.getDocKind().equals(docKind)) {
                docConfigs.remove(config);
                break;
            }
        }
    }

    @Override
    public int compareTo(ProjectConfig o) {
        return this.project.compareTo(o.project);
    }

    public boolean contains(String docKind) {
        for (DocConfig docConfig : docConfigs) {
            if (docConfig.getDocKind().equals(docKind)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @return docConfigs
     *
     * @since 2014. 11. 21.
     */
    public Collection<DocConfig> getDocConfigs() {
        return this.docConfigs;
    }

    /**
     *
     * @return project
     *
     * @since 2014. 11. 21.
     */
    public String getProject() {
        return this.project;
    }

    /**
     *
     * @param docConfigs
     *            docConfigs to set.
     *
     * @since 2014. 11. 21.
     */
    public void setDocConfigs(Collection<DocConfig> docConfigs) {
        this.docConfigs = new ConcurrentSkipListSet<>();
        this.docConfigs.addAll(docConfigs);
    }

    /**
     *
     * @param project
     *            project to set.
     *
     * @since 2014. 11. 21.
     */
    public void setProject(String project) {
        this.project = project;
    }

}