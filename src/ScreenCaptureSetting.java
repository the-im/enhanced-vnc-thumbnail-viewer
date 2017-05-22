/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - To keep screen capture data
 */

public class ScreenCaptureSetting {
    
    public static final String INIT_PATH = System.getProperty("user.home");
    public static final int INIT_DELAY = 600;
    public static final boolean INIT_IS_ENABLE = false;
    
    private static String path = INIT_PATH;
    private static int delay = INIT_DELAY;
    private static boolean isEnable = INIT_IS_ENABLE;

    public static String getPath() {
        return path;
    }

    public static void setPath(String aPath) {
        path = aPath;
    }

    public static int getDelay() {
        return delay;
    }

    public static void setDelay(int aDelay) {
        delay = aDelay;
    }

    public static boolean getIsEnable() {
        return isEnable;
    }

    public static void setIsEnable(boolean aIsEnable) {
        isEnable = aIsEnable;
    }
}