/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 21. 오후 1:44:01
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm.core;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.commons.utils.FileUtils;

public class RevisionFile implements Comparable<RevisionFile> {

    private static final String FORMAT_REVISION = "(_([v|V]\\d+(\\.\\d+){0,2}))?-(\\d{8}(-|~)(\\d{4}|\\d{8}))";

    private final Pattern patternRevision = Pattern.compile(FORMAT_REVISION, Pattern.CASE_INSENSITIVE);

    private FileVersion3L version;

    private boolean isVersionManaged = false;

    private String minDate;

    private String maxDate;

    private char dateConcatenator;

    private File source;

    private String prefix;

    public RevisionFile(File file, String prefix) {
        init(FileUtils.getFileNameNoExtension(file), prefix);

        source = file;
        this.prefix = prefix;
    }

    /**
     * 
     * @param file
     *            fullpath file string
     * @param prefix
     */
    public RevisionFile(String file, String prefix) {
        init(FileUtils.getFileNameNoExtension(file), prefix);

        source = new File(file);
    }

    @Override
    public int compareTo(RevisionFile o) {
        // check "Management By Version"
        if (!(isVersionManaged ^ o.isVersionManaged)) {
            int c = version.compareTo(o.version);

            if (c != 0) {
                return c;
            }

            return extendMaxDateLength().compareTo(o.extendMaxDateLength());

        } else if (isVersionManaged) {
            return 1;
        } else {
            return -1;
        }
    }

    private String extendMaxDateLength() {
        String dateValue = null;

        if (maxDate.length() < 8) {
            String minYear = minDate.substring(0, 4);
            String minMonth = minDate.substring(4, 6);

            String maxYear = null;
            String maxMonth = maxDate.substring(0, 2);

            maxYear = maxMonth.compareTo(minMonth) < 0 ? String.valueOf(Integer.parseInt(minYear) + 1) : minYear;

            dateValue = new StringBuffer().append(maxYear).append(maxDate).toString();
        }

        return dateValue;
    }

    public char getDateConcatenator() {
        return dateConcatenator;
    }

    /**
     * @return the maxDate
     */
    public String getMaxDate() {
        return maxDate;
    }

    /**
     * @return the minDate
     */
    public String getMinDate() {
        return minDate;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    public File getSource() {
        return source;
    }

    /**
     * @return the versions
     */
    public FileVersion3L getVersion() {
        return version;
    }

    private void init(String name, String prefix) {
        Matcher m = patternRevision.matcher(name.replace(prefix, ""));

        if (m.matches()) {
            String versionStr = m.group(2);
            String date = m.group(4);

            if (versionStr != null) {
                version = new FileVersion3L(versionStr.substring(1));
                isVersionManaged = true;
            }

            minDate = date.substring(0, 8);
            maxDate = date.substring(9);

            dateConcatenator = m.group(5).charAt(0);

        } else {
            throw new IllegalArgumentException("Does not matched to the FORMAT_REVISION(" + FORMAT_REVISION + ")");
        }
    }

    /**
     * @return the isVersionManaged
     */
    public boolean isVersionManaged() {
        return isVersionManaged;
    }
    
    public void setVersion(FileVersion3L version) {
        this.version = version;
    }

    /**
     * 
     * @param versions
     * @return
     */
    public FileVersion3L setVersion(int... versions) {
        if (version == null) {
            version = new FileVersion3L(versions);
        } else {
            version.update(versions);
        }

        isVersionManaged = true;

        return version;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        RevisionFile f1 = new RevisionFile("TEST_SNMSP2_WBS_2단계개발_V2.1-20130305~0306.xlsx", "TEST_SNMSP2_WBS_2단계개발");
        RevisionFile f2 = new RevisionFile("TEST_SNMSP2_WBS_2단계개발_v2.1.2-20130305~0306.xlsx", "TEST_SNMSP2_WBS_2단계개발");

        System.out.println(f2.compareTo(f1));

    }

}
