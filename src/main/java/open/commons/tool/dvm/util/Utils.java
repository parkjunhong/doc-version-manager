/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 24. 오전 10:53:11
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.util;

import java.io.File;

import org.eclipse.swt.widgets.Text;

import open.commons.tool.dvm.core.RevisionFile;

public class Utils {

    public static void clearText(Text... texts) {
        for (Text text : texts) {
            if (text != null && !text.isDisposed()) {
                text.setText("");
            }
        }
    }

    public static RevisionFile getLatestFile(String prefix, File... files) {

        if (files == null || files.length < 1) {
            return null;
        }

        RevisionFile lf = new RevisionFile(files[0], prefix);
        RevisionFile tf = null;

        for (int i = 1; i < files.length; i++) {
            tf = new RevisionFile(files[i], prefix);

            if (tf.compareTo(lf) > 0) {
                lf = tf;
            }
        }

        return lf;
    }

    public static void setText(Text[] texts, String[] strings) {
        int tl = texts.length;
        int sl = strings.length;

        for (int i = 0; i < tl; i++) {
            texts[i].setText(tl <= sl ? emptyIfNull(strings[i]) : "");
        }
    }
    
    public static String emptyIfNull(String string) {
        return string == null ? "" : string;
    }

    /**
     * 주어진 문자열이 <code>null</code>이거나 빈 문자열인 경우 <code>null</code>을 반환한다.
     * 
     * @param string
     * @return <code>null</code> 또는 trim 처리된 문자열.
     *
     * @since 2014. 12. 4.
     */
    public static String nullIfEmpty(String string) {
        return string != null && !string.trim().isEmpty() ? string.trim() : null;
    }
}
