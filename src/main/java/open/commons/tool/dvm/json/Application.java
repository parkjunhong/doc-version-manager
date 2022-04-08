package open.commons.tool.dvm.json;

import java.util.ArrayList;
import java.util.List;

import open.commons.core.annotation.ComparableValue;
import open.commons.json.annotation.JSONField;
import open.commons.json.model.DefaultJSONModel;

/**
 * <a href="http://tools.ietf.org/html/rfc7159">JSON</a> source: <br>
 * 
 * <pre>
 * 
 * [CASE - 0]
 * 
 * {
 *   "fileExts": [
 *     "doc",
 *     "docx"
 *   ],
 *   "appPath": "C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.EXE"
 * }
 * </pre>
 */
public class Application extends DefaultJSONModel {

    private static final long serialVersionUID = 1L;

    @ComparableValue(order=0)
    @JSONField(name = "appPath")
    private String appPath;

    @ComparableValue(order=1)
    @JSONField(name = "fileExts")
    private List<String> fileExts = new ArrayList<>();

    public Application() {
    }

    public void addFileExtensions(String... fileExts) {
        for (String fileExt : fileExts) {
            if (fileExt != null && !this.fileExts.contains(fileExt)) {
                this.fileExts.add(fileExt);
            }
        }
    }

    /**
     *
     * @return appPath
     *
     * @since 2014. 12. 09.
     */
    public String getAppPath() {
        return this.appPath;
    }

    /**
     *
     * @return fileExts
     *
     * @since 2014. 12. 09.
     */
    public List<String> getFileExts() {
        return this.fileExts;
    }

    /**
     *
     * @param appPath
     *            appPath to set.
     *
     * @since 2014. 12. 09.
     */
    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    /**
     *
     * @param fileExts
     *            fileExts to set.
     *
     * @since 2014. 12. 09.
     */
    public void setFileExts(List<String> fileExts) {
        this.fileExts = fileExts != null ? fileExts : new ArrayList<String>();
    }

}