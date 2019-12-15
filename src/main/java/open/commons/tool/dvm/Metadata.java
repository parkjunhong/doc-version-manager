/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 12. 4. 오후 3:16:44
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm;

import java.io.IOException;
import java.util.Properties;

import open.commons.config.ReferenceableProperties;

public class Metadata {

    private static Properties properties = new ReferenceableProperties();

    static {
        try {
            properties.load(Metadata.class.getResourceAsStream("/resource/metadata.properties"));
        } catch (IOException e) {
        } finally {
        }
    }
    
    public static String product(){
        return properties.getProperty("product");
    }

    public static String author() {
        return properties.getProperty("author");
    }

    public static String email() {
        return properties.getProperty("email");
    }

    public static String version() {
        return properties.getProperty("version");
    }

    public static String update() {
        return properties.getProperty("update");
    }

}
