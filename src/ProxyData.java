/*
 *  Enhanced VNC Thumbnail Viewer 1.0
 *  Keep proxy data: host, port, on/off proxy
 * 
 */

import java.io.Serializable;

public class ProxyData implements Serializable{
    private String host;
    private int port;
    private boolean isProxy;
    private String username;
    private String password;
    private boolean isAuth;

    public String getHost() {
        return host;
    }

    public void setHost(String aHost) {
        host = aHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int aPort) {
        port = aPort;
    }

    public boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(boolean aIsProxy) {
        isProxy = aIsProxy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String aUsername) {
        username = aUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String aPassword) {
        password = aPassword;
    }

    public boolean getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(boolean aIsAuth) {
        isAuth = aIsAuth;
    }
}
