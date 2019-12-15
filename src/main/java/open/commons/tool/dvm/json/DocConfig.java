/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 21. 오후 1:44:01
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm.json;

import open.commons.annotation.ComparableValue;
import open.commons.json.annotation.JSONField;
import open.commons.json.model.DefaultJSONModel;
import open.commons.utils.ComparableUtils;

/**
 * <a href="http://tools.ietf.org/html/rfc7159">JSON</a> source: <br>
 * 
 * <pre>
 * 
 * [CASE - 0]
 * 
 * {
 *      "exeCmd": "C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.EXE",
 *      "batchFileEndcoding": "EUC-KR",
 *      "fileDir": "파일 경로",
 *      "fileExt": "doc|docx",
 *      "docKind": "문서 종류",
 *      "filename": "파일이름",
 *      "backupDir": "백업경로",
 *      "datePattern": "날짜패턴"
 *  }
 * </pre>
 */
public class DocConfig extends DefaultJSONModel implements Comparable<DocConfig> {

    private static final long serialVersionUID = 1L;

    /** 대상 */
    @ComparableValue(order = 0)
    @JSONField(name = "docKind")
    private String docKind;

    /** 문서가 위치하는 디렉토리 */
    @ComparableValue(order = 1)
    @JSONField(name = "fileDir")
    private String fileDir;

    /** 문서 이름 */
    @ComparableValue(order = 2)
    @JSONField(name = "filename")
    private String filename;

    /** 문서 날짜 패턴 */
    @JSONField(name = "datePattern")
    private String datePattern;

    /** 문서 확장자 */
    @ComparableValue(order = 3)
    @JSONField(name = "fileExt")
    private String fileExt;

    /** 문서 실행 시스템 명령어 */
    @JSONField(name = "exeCmd")
    private String exeCmd;

    /** 최근 파일 백업 디렉토리 */
    @JSONField(name = "backupDir")
    private String backupDir;

    /** 배치 실행 파일 인코딩 */
    @JSONField(name = "batchFileEndcoding")
    private String batchFileEndcoding;

    public DocConfig() {
    }

    public DocConfig(String docKind) {
        this.docKind = docKind;
    }

    @Override
    public int compareTo(DocConfig o) {
        return ComparableUtils.compareTo(this, o);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocConfig other = (DocConfig) obj;
        if (backupDir == null) {
            if (other.backupDir != null)
                return false;
        } else if (!backupDir.equals(other.backupDir))
            return false;
        if (batchFileEndcoding == null) {
            if (other.batchFileEndcoding != null)
                return false;
        } else if (!batchFileEndcoding.equals(other.batchFileEndcoding))
            return false;
        if (datePattern == null) {
            if (other.datePattern != null)
                return false;
        } else if (!datePattern.equals(other.datePattern))
            return false;
        if (docKind == null) {
            if (other.docKind != null)
                return false;
        } else if (!docKind.equals(other.docKind))
            return false;
        if (exeCmd == null) {
            if (other.exeCmd != null)
                return false;
        } else if (!exeCmd.equals(other.exeCmd))
            return false;
        if (fileDir == null) {
            if (other.fileDir != null)
                return false;
        } else if (!fileDir.equals(other.fileDir))
            return false;
        if (fileExt == null) {
            if (other.fileExt != null)
                return false;
        } else if (!fileExt.equals(other.fileExt))
            return false;
        if (filename == null) {
            if (other.filename != null)
                return false;
        } else if (!filename.equals(other.filename))
            return false;
        return true;
    }

    /**
     * CLI option: '-t'
     * 
     * @return backupDir
     *
     * @since 2014. 11. 21.
     */
    public String getBackupDir() {
        return this.backupDir;
    }

    /**
     * CLI option: '-b'
     * 
     * @return batchFileEndcoding
     *
     * @since 2014. 11. 21.
     */
    public String getBatchFileEndcoding() {
        return this.batchFileEndcoding;
    }

    /**
     * CLI option: '-r'
     * 
     * @return datePattern
     *
     * @since 2014. 11. 21.
     */
    public String getDatePattern() {
        return this.datePattern;
    }

    /**
     * 
     * @return docKind
     *
     * @since 2014. 11. 21.
     */
    public String getDocKind() {
        return this.docKind;
    }

    /**
     * CLI option: '-e'
     * 
     * @return exeCmd
     *
     * @since 2014. 11. 21.
     */
    public String getExeCmd() {
        return this.exeCmd;
    }

    /**
     * CLI option: '-d'
     * 
     * @return fileDir
     *
     * @since 2014. 11. 21.
     */
    public String getFileDir() {
        return this.fileDir;
    }

    /**
     * CLI option: '-x'
     * 
     * @return fileExt
     *
     * @since 2014. 11. 21.
     */
    public String getFileExt() {
        return this.fileExt;
    }

    /**
     * CLI option: '-p'
     * 
     * @return filename
     *
     * @since 2014. 11. 21.
     */
    public String getFilename() {
        return this.filename;
    }

    public boolean isValid() {
        return validStr(docKind, fileDir, filename, datePattern, fileExt, exeCmd, batchFileEndcoding);
    }

    /**
     * 
     * @param backupDir
     *            backupDir to set.
     *
     * @since 2014. 11. 21.
     */
    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    /**
     *
     * @param batchFileEndcoding
     *            batchFileEndcoding to set.
     *
     * @since 2014. 11. 21.
     */
    public void setBatchFileEndcoding(String batchFileEndcoding) {
        this.batchFileEndcoding = batchFileEndcoding;
    }

    /**
     *
     * @param datePattern
     *            datePattern to set.
     *
     * @since 2014. 11. 21.
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    /**
     *
     * @param docKind
     *            docKind to set.
     *
     * @since 2014. 11. 21.
     */
    public void setDocKind(String docKind) {
        this.docKind = docKind;
    }

    /**
     *
     * @param exeCmd
     *            exeCmd to set.
     *
     * @since 2014. 11. 21.
     */
    public void setExeCmd(String exeCmd) {
        this.exeCmd = exeCmd;
    }

    /**
     *
     * @param fileDir
     *            fileDir to set.
     *
     * @since 2014. 11. 21.
     */
    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    /**
     *
     * @param fileExt
     *            fileExt to set.
     *
     * @since 2014. 11. 21.
     */
    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    /**
     *
     * @param filename
     *            filename to set.
     *
     * @since 2014. 11. 21.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    private boolean validStr(String... strings) {
        for (String str : strings) {
            if (str == null || str.isEmpty()) {
                return false;
            }
        }

        return true;
    }

}