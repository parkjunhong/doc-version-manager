/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 24. 오후 5:24:02
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.tool.dvm.ui;

import open.commons.tool.dvm.json.DocConfig;

public interface IDocConfigChangeListener {

    public void change(String project, DocConfig newValue, DocConfig oldValue);
}
