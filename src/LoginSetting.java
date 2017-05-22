/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Renamed from LoginData class
 *  - Removed Serializable interface
 *  - Added static to attributes & methods
 *  - Added static final attributes
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 *  - Keep login data: username, password, on/off login on start up
 */

public class LoginSetting {
    
    public static final String INIT_USERNAME = "";
    public static final String INIT_PASSWORD = "";
    public static final boolean INIT_IS_ENABLE = false;
    public static final boolean INIT_IS_REMEMBER = false;
    
    private static String username = INIT_USERNAME;
    private static String password = INIT_PASSWORD;
    private static boolean isEnable = INIT_IS_ENABLE;
    private static boolean isRemember = INIT_IS_REMEMBER;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String aUsername) {
        username = aUsername;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String aPassword) {
        password = aPassword;
    }

    public static boolean getIsEnable() {
        return isEnable;
    }

    public static void setIsEnable(boolean aIsEnable) {
        isEnable = aIsEnable;
    }
    
    public static boolean getIsRemember() {
        return isRemember;
    }

    public static void setIsRemember(boolean aIsRemember) {
        isRemember = aIsRemember;
    }
}