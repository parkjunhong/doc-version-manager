/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 26. 오전 10:39:58
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm.util;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class SWTUtils {

    public static String selectDirectory(Shell shell, String filterpath) {
        DirectoryDialog dlg = new DirectoryDialog(shell);
        if (filterpath != null && !filterpath.isEmpty()) {
            dlg.setFilterPath(filterpath);
        }

        return dlg.open();
    }

    public static String selectFile(Shell shell, String filename) {
        FileDialog dlg = new FileDialog(shell);
        dlg.setFileName(filename != null && !filename.isEmpty() ? filename : "");
        return dlg.open();
    }

    public static void setEnabled(boolean enabled, Control... controls) {
        for (Control control : controls) {
            if (control != null && !control.isDisposed()) {
                control.setEnabled(enabled);
            }
        }
    }
}
