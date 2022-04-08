/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2015. 4. 29. 오후 3:19:26
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

import open.commons.core.utils.IOUtils;

public class ConfigFileHistory {

    private static final String HISTORY_FILE = "./config/history.config";

    private static ConcurrentSkipListSet<String> configFileHistory = new ConcurrentSkipListSet<>();

    static {

        File historyFile = new File(HISTORY_FILE);

        if (historyFile.exists()) {
            BufferedReader reader = IOUtils.getReader(historyFile);
            String readline = null;

            try {
                while ((readline = reader.readLine()) != null) {
                    if ((readline = readline.trim()).isEmpty()) {
                        continue;
                    }

                    configFileHistory.add(readline);
                }
            } catch (Exception ignored) {
            } finally {
                IOUtils.close(reader);
            }
        }
    }

    public static void add(String filepath) {

        if (filepath == null || filepath.trim().isEmpty() || !new File(filepath).exists() || configFileHistory.contains(filepath)) {
            return;
        }

        configFileHistory.add(filepath);

        write();
    }

    public static String[] get() {
        return configFileHistory.toArray(new String[] {});
    }

    private static void write() {

        File file = new File(HISTORY_FILE);

        if (!file.exists()) {
            file.mkdirs();
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(HISTORY_FILE));

            for (String fp : configFileHistory) {
                writer.write(fp);
                writer.flush();
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }

    }
}
