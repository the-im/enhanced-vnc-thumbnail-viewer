/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - To keep each recent settings
 */

import java.util.Vector;

public class RecentSettingsList {
    
    public static final int MAX_RECENT_SETTINGS = 10;
    private static Vector rsList = new Vector();
    
    public static void addRecent(RecentSetting rs) {
        if (rsList.size() >= MAX_RECENT_SETTINGS) {
            rsList.removeElementAt(MAX_RECENT_SETTINGS - 1);
        }
        rsList.add(0, rs);
    }

    public static RecentSetting getRecent(int i) {
        return (RecentSetting) rsList.get(i);
    }
    
    public static Vector getTotalRecents() {
        return rsList;
    }

}
