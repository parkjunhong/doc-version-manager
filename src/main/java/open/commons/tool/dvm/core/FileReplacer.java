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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import open.commons.log.LogStringFactory;
import open.commons.log.LogStringFactory.LogStringContainer;
import open.commons.tool.dvm.json.DocConfig;
import open.commons.tool.dvm.util.Utils;
import open.commons.utils.ArrayUtils;
import open.commons.utils.DateUtil;
import open.commons.utils.FileUtils;

public class FileReplacer {

    public static final String FORMAT_VERSION = "(v|V)\\d+(\\.\\d+){0,2}";
    public static final String FORMAT_DEFAULT_DATE = "\\d{8}-\\d{4}|\\d{8}-\\d{8}";

    private static final int MSG_SUCCESS = 0x00;
    private static final int MSG_FAIL_TO_BATCH = 0x01;
    private static final int MSG_OCCUR_EXCEPTION = 0x02;
    private static final int MSG_EXIST_FILE = 0x03;
    private static final int MSG_FILE_BACKUP_SUCCESS = 0x04;
    private static final int FILE_BACKUP_FAIL = 0x05;

    static LogStringContainer log = LogStringFactory.getContainer(10, 10, 0, "\n");
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FileReplacer(String[] args) {
        try {
            // parseArguments(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File backupFile(File srcFile, String backupDirStr) {
        String filename = srcFile.getName();

        File backupDir = new File(backupDirStr);

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        File backupFile = new File(backupDir, filename);

        return copyFile(srcFile, backupFile) ? backupFile : null;
    }

    /**
     * 
     * @param src
     *            복사할 파일
     * @param target
     *            복사된 파일
     */
    private static boolean copyFile(File src, File target) {
        InputStream input = null;
        OutputStream output = null;

        try {

            input = new FileInputStream(src);
            output = new FileOutputStream(target);

            byte[] buf = new byte[1024 * 4];
            int read = -1;

            while ((read = input.read(buf)) > 0) {
                output.write(buf, 0, read);
            }

            input.close();
            output.close();

        } catch (Exception e) {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
            if (output != null) {
                try {
                    output.close();

                    FileUtils.delete(target, true);
                } catch (IOException ignored) {
                }
            }

            return false;
        }
        return true;
    }

    /**
     * 
     * @param ldvHeader
     *            header of latest date value
     * @param ldvTail
     *            tail of latest date value
     * @param con
     *            TODO
     */
    private static String createNextDateValue(String ldvHeader, String ldvTail, char con) {

        boolean tail4 = false;

        Calendar calendar = Calendar.getInstance();
        String curDate = DateUtil.toString(calendar.getTime(), "yyyyMMdd");

        // increase a length of tail date value
        if (ldvTail.length() < 5) {
            String headerMD = ldvHeader.substring(4);

            StringBuffer sb = new StringBuffer();
            if (headerMD.compareTo(ldvTail) > 0) {
                sb.append(Integer.parseInt(ldvHeader.substring(0, 4)) + 1);
            } else {
                sb.append(ldvHeader.substring(0, 4));
            }

            sb.append(ldvTail);

            ldvTail = sb.toString();

            tail4 = true;
        }

        // 오늘 날짜가 관리 대상 기간인지 확인
        String nextLdvHeader = null;
        String nextLdvTail = null;

        if (DateUtil.compare(curDate, ldvHeader, ldvTail) > 0) {
            nextLdvHeader = nextWeekValue(ldvHeader, 7);
            nextLdvTail = nextWeekValue(ldvTail, 7);

            while (DateUtil.compare(curDate, nextLdvHeader, nextLdvTail) > 0) {
                nextLdvHeader = nextWeekValue(nextLdvHeader, 7);
                nextLdvTail = nextWeekValue(nextLdvTail, 7);
            }

            if (tail4) {
                nextLdvTail = nextLdvTail.substring(4, 8);

            }

            return nextLdvHeader + con + nextLdvTail;
        } else {
            return null;
        }
    }

    private static File createNextFile(String targetDir, RevisionFile revLatestFile, String filePrefix, FileVersion3L newFileVersion) {

        File latestFile = revLatestFile.getSource();
        String fileExt_ = FileUtils.getFileExtension(latestFile);

        // check 'new version'
        FileVersion3L latestVersion = revLatestFile.getVersion();

        boolean newVersioned = latestVersion.compareTo(newFileVersion) < 0;
        String versions = "_v" + (newVersioned ? newFileVersion.toString() : latestVersion.toString());

        // next date
        String ldvHeader = revLatestFile.getMinDate();// dateValue_.substring(0, 8);
        String ldvTail = revLatestFile.getMaxDate();// dateValue_.substring(9);

        String nextDateValue = createNextDateValue(ldvHeader, ldvTail, revLatestFile.getDateConcatenator());

        boolean copied = false;
        File targetFile = null;

        if (newVersioned || nextDateValue != null) {
            String dateValue = nextDateValue != null ? nextDateValue : revLatestFile.getMinDate() + revLatestFile.getDateConcatenator() + revLatestFile.getMaxDate();
            targetFile = new File(targetDir + File.separator + filePrefix + versions + "-" + dateValue + "." + fileExt_);
            copied = copyFile(latestFile, targetFile);

        }

        return copied ? targetFile : null;
    }

    /**
     * 시작 요일부터 종료 요일까지 배열.
     * 
     * @param sDow
     * @param eDow
     * @return
     */
    private static int[] dows(int sDow, int eDow) {
        int[] dows = new int[7];

        int selected = sDow;

        int i = 0;
        while (selected != eDow) {
            dows[i++] = selected;

            if (++selected != 7) {
                selected %= 7;
            }
        }

        dows[i] = eDow;

        return Arrays.copyOf(dows, i + 1);
    }

    /**
     * Day of Week String
     * 
     * @param dow
     * @return
     */
    public static String dowString(int dow) {
        switch (dow) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thr";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return null;
        }
    }

    /**
     * Day of Week Strings
     * 
     * @param sDow
     * @param eDow
     * @return
     */
    public static String[] dowString(int sDow, int eDow) {
        int[] dows = dows(sDow, eDow);

        String[] dowString = new String[dows.length];

        for (int i = 0; i < dows.length; i++) {
            dowString[i] = dowString(dows[i]);
        }

        return dowString;
    }

    public static List<String> execute(DocConfig docConfig, FileVersion3L newFileVersion) {
        List<String> logs = new ArrayList<>();
        logs.add(logDate.format(new Date(System.currentTimeMillis())) + " | " + "[START] >>> " + docConfig.getDocKind() + " | " + docConfig.getFilename());

        String targetDir = docConfig.getFileDir();
        final String filePrefix = docConfig.getFilename();
        final String fileExt = docConfig.getFileExt();

        // DoW: Day of Week
        String datePatterns = docConfig.getDatePattern();

        RevisionFile latestFile = getTargetFile(targetDir, filePrefix, fileExt, datePatterns);

        if (latestFile == null) {

            logs.add(logDate.format(new Date(System.currentTimeMillis())) + " | " + "[INFO] <파일 변경 실패> 관리 대상 파일이 존재하지 않습니다.\n\t대상디렉토리: " + targetDir + "\n\t파일 접두어: " + filePrefix
                    + "\n\t파일 확장자: " + fileExt + "\n\t날짜  패턴: " + datePatterns + "\n");

            return logs;

        } else {

            File nextFile = createNextFile(targetDir, latestFile, filePrefix, newFileVersion);

            if (nextFile != null) {
                // 관리 파일이 변경된 경우 batch 파일 내용도 변경한다.
//                int updateBatchFile = updateBatchFile(targetDir, nextFile, docConfig);
//                logs.add(logMessage(updateBatchFile, latestFile.getSource(), nextFile));

                logs.add(logMessage(MSG_SUCCESS, latestFile.getSource(), nextFile));

            } else {
//                int updateBatchFile = updateBatchFile(targetDir, latestFile.getSource(), docConfig);
//                logs.add(logMessage(updateBatchFile, latestFile.getSource(), latestFile.getSource()));
                
                logs.add(logMessage(MSG_EXIST_FILE, latestFile.getSource(), null));
            }

            // backup latest file
            String backupDirStr = docConfig.getBackupDir();
            if (backupDirStr != null) {
                File bf = backupFile(latestFile.getSource(), backupDirStr);
                logs.add(logMessage(bf != null ? MSG_FILE_BACKUP_SUCCESS : FILE_BACKUP_FAIL, latestFile.getSource(), bf != null ? bf : new File(backupDirStr, latestFile
                        .getSource().getName())));

                if (nextFile != null) {
                    bf = backupFile(nextFile, backupDirStr);
                    logs.add(logMessage(bf != null ? MSG_FILE_BACKUP_SUCCESS : FILE_BACKUP_FAIL, nextFile, bf != null ? bf : new File(backupDirStr, nextFile.getName())));
                }
            }
        }

        logs.add(logDate.format(new Date(System.currentTimeMillis())) + " | " + "[FINISH] <<< " + docConfig.getDocKind() + " | " + docConfig.getFilename() + "\n");
        return logs;

    }

    public static RevisionFile getTargetFile(String dir, final String filePrefix, final String fileExts, final String datePatterns) {
        File curdir = new File(dir);

        FilenameFilter ff = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {

                try {
                    // check filename
                    if (!name.startsWith(filePrefix)) {
                        return false;
                    }

                    String fileExt_ = FileUtils.getFileExtension(name);
                    String dateValue_ = null;

                    String omitPrefixString = FileUtils.getFileNameNoExtension(name).replace(filePrefix, "");

                    if (omitPrefixString.length() < 1) {
                        return false;
                    }

                    // check version info
                    char bc = omitPrefixString.charAt(0);
                    if (bc == '_') {
                        int startDateIndex = omitPrefixString.indexOf('-');

                        if (startDateIndex < 0) {
                            return false;
                        }

                        // check version format
                        if (!omitPrefixString.substring(1, startDateIndex).matches(FORMAT_VERSION)) {
                            return false;
                        }

                        dateValue_ = omitPrefixString.substring(startDateIndex + 1);

                    } else if (bc == '-') {
                        dateValue_ = omitPrefixString.substring(1);
                    } else {
                        return false;
                    }

                    // check file extensions
                    if (!ArrayUtils.contains(fileExts.split("[|]"), fileExt_)) {
                        return false;
                    }

                    // check date value
                    for (String dp : datePatterns.split("[|]")) {
                        if (dateValue_.matches(dp.trim())) {
                            return true;
                        }
                    }

                    return false;
                } catch (Exception e) {
                    throw new RuntimeException("dir: " + dir + ", name: " + name);
                }
            }
        };

        File[] files = curdir.listFiles(ff);

        return Utils.getLatestFile(filePrefix, files);
    }

    static final StringBuffer sb = new StringBuffer();

    private static String logMessage(int alertIndex, File latestFile, File nextFile) {
        sb.setLength(0);
        final String timestamp = logDate.format(new Date(System.currentTimeMillis())) + " | ";
        sb.append(timestamp);
        switch (alertIndex) {
            case MSG_SUCCESS:
                sb.append("[INFO] <프로젝트 관리 파일 변경 성공> - 관리 대상 파일을 변경하였습니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 전 파일: ");
                sb.append(latestFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 후 파일: ");
                sb.append(nextFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));
                break;
            case MSG_FAIL_TO_BATCH:
                sb.append("[INFO] <프로젝트 관리 파일 변경 실패> - 관리 대상 파일을 변경하지 못했습니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 전 파일: ");
                sb.append(latestFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 후 파일: ");
                sb.append(nextFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t(((변경된 관리 대상 파일을 롤백하였습니다.)))");
                break;
            case MSG_OCCUR_EXCEPTION:
                sb.append("[INFO] <프로젝트 관리 파일 변경 실패> - 처리 도중 예외상황이 발생하였습니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 전 파일: ");
                sb.append(latestFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t변경 후 파일: ");
                sb.append(nextFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));
                sb.append("\t(((변경된 관리 대상 파일을 롤백하였습니다.)))");

                break;
            case MSG_EXIST_FILE:
                sb.append("[INFO] <파일 변경 실패> - 관리 대상 파일이 존재합니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 파일: ");
                sb.append(latestFile.getName());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));
                break;
            case MSG_FILE_BACKUP_SUCCESS:
                sb.append("[INFO] <파일 백업 성공> - 관리 대상 파일을 백업하였습니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t원본 파일: ");
                sb.append(latestFile.getAbsolutePath());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t백업 파일: ");
                sb.append(nextFile.getAbsolutePath());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));

                break;
            case FILE_BACKUP_FAIL:
                sb.append("[INFO] <파일 백업 완료 실패> - 관리 대상 파일을 백업하는데 실패하였습니다.");
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t원본 파일: ");
                sb.append(latestFile.getAbsolutePath());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t백업 파일: ");
                sb.append(nextFile.getAbsolutePath());
                sb.append('\n');
                sb.append(timestamp);
                sb.append("\t현재 날짜: ");
                sb.append(DateUtil.toString("yyyy년 MM월 dd일"));
                break;
            default:
                break;
        }

        return sb.toString();
    }

    private static String nextWeekValue(String dateValue, int day) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(dateValue.subSequence(0, 4).toString()));
        cal.set(Calendar.MONTH, Integer.parseInt(dateValue.subSequence(4, 6).toString()) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateValue.subSequence(6, 8).toString()));

        cal.add(Calendar.DAY_OF_MONTH, day);

        return DateUtil.getDateString(cal.getTime());

    }

    private static String readFile(File file, String encoding) {

        BufferedReader reader = null;
        String readline = null;

        StringBuffer sb = new StringBuffer();
        int length = 0;

        try {

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

            while ((readline = reader.readLine()) != null) {
                sb.append(readline + "\n");

                length += (readline.length() + 1);
            }

            reader.close();

            readline = sb.substring(0, length - 1);

        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }

            readline = null;
        }

        return readline;
    }

    /**
     * 
     * @param targetDir
     * @param nextFile
     * @param docConfig
     *            TODO
     * @return <ul>
     *         <li>0: 성공
     *         <li>1: 실패
     *         <li>2: 오류발생
     *         </ul>
     */
    private static int updateBatchFile(String targetDir, File nextFile, DocConfig docConfig) {

        File newBatch = new File(targetDir + File.separator + docConfig.getFilename() + ".bat.tmp");
        File latestBatch = new File(targetDir + File.separator + docConfig.getFilename() + ".bat");

        String encoding = docConfig.getBatchFileEndcoding();

        String oldBatchStr = readFile(latestBatch, encoding);

        try {

            // write new BatchFile
            String content = "start \"" + docConfig.getExeCmd() + "\" \"" + docConfig.getFileDir() + File.separator + nextFile.getName() + "\"";

            writeFile(newBatch, content, encoding);

            boolean deleteLatestBatch = FileUtils.delete(latestBatch, true);

            int retryDeleteLatestBatch = 0;
            while (!deleteLatestBatch & retryDeleteLatestBatch < 5) {

                if (JOptionPane.showConfirmDialog(new JFrame(), "기존 batch 파일을 삭제하지 못하였습니다.", "[파일 삭제 실패]", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) {

                    deleteLatestBatch = FileUtils.delete(latestBatch, true);
                } else {
                    break;
                }
            }

            if (deleteLatestBatch) {

                boolean renBatch = newBatch.renameTo(latestBatch);

                if (renBatch) {
                    return MSG_SUCCESS;
                }
            }

            FileUtils.delete(nextFile, true);
            FileUtils.delete(newBatch, true);

            return MSG_FAIL_TO_BATCH;

        } catch (Exception e) {
            FileUtils.delete(nextFile, true);
            FileUtils.delete(newBatch, true);

            if (!latestBatch.exists()) {
                writeFile(latestBatch, oldBatchStr, encoding);
            }

            return MSG_OCCUR_EXCEPTION;
        }
    }

    private static boolean writeFile(File file, String context, String encoding) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
            writer.write(context);
            writer.close();

            return true;
        } catch (Exception e) {
            if (writer != null) {
                try {
                    writer.close();
                    FileUtils.delete(file, true);
                } catch (Exception ignored) {
                }
            }

            return false;
        }
    }
}
