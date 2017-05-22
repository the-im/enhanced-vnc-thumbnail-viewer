/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * Keep login data: username, password, on/off login on start up
 * 
 */

import java.io.Serializable;

public class LoginData implements Serializable{
    private String username;
    private String password;
    private boolean isAuth;

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
