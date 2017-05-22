/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Renamed from ProxyData class
 *  - Removed Serializable interface
 *  - Added static to attributes & methods
 *  - Added static final attributes
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 *  -Keep proxy data: host, port, on/off proxy
 * 
 */

public class ProxySetting {
    
    public static final String INIT_SERVER = "";
    public static final int INIT_PORT = 0;
    public static final boolean INIT_IS_ENABLE = false;
    
    private static String server = INIT_SERVER;
    private static int port = INIT_PORT;
    private static boolean isEnable = INIT_IS_ENABLE;

    public static String getServer() {
        return server;
    }

    public static void setServer(String aServer) {
        server = aServer;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int aPort) {
        port = aPort;
    }

    public static boolean getIsEnable() {
        return isEnable;
    }

    public static void setIsEnable(boolean aIsEnable) {
        isEnable = aIsEnable;
    }
}