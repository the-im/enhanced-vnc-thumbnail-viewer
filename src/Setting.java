/*
 * Enhanced VNC Thumbnail Viewer 1.001
 * For call all settings
 */

public class Setting {

    public final static String LOGIN_FILE_NAME = "login.b";
    public final static String PROXY_FILE_NAME = "proxy.b";
    public final static String SLIDE_SHOW_FILE_NAME = "slide-show.b";
    
    private static LoginData loginData = LoginIO.readFile();
    private static ProxyData proxyData = ProxyIO.readFile();
    private static SlideShowData slideShowData = SlideShowIO.readFile();
    
    /*
     * Login setting
     */
    public static void setLoginData(LoginData data) {
        loginData = data;
    }
    public static LoginData getLoginData() {
        return loginData;
    }
    
    /*
     * Proxy setting
     */
    public static void setProxyData(ProxyData data) {
        proxyData = data;
    }
    public static ProxyData getProxyData() {
        return proxyData;
    }
    
    /*
     * Slide show setting
     */
    public static void setSlideShowData(SlideShowData data) {
        slideShowData = data;
    }
    public static SlideShowData getSlideShowData() {
        return slideShowData;
    }
}
