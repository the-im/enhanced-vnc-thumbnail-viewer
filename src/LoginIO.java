/*
 * Enhanced VNC Thumbnail Viewer 1.001
 */

import java.io.*;

public class LoginIO {
    public static void writeFile(LoginData data){
        try {
            FileOutputStream f = new FileOutputStream(Setting.LOGIN_FILE_NAME);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(data);
            o.close();
            f.close();

            System.out.println("Save login data..");
        } catch (IOException ex) {
        }
    }
    
    public static LoginData readFile(){
        try {
            FileInputStream f = new FileInputStream(Setting.LOGIN_FILE_NAME);
            ObjectInputStream o = new ObjectInputStream(f);
            LoginData data = (LoginData) o.readObject();
            o.close();
            f.close();
            
            System.out.println("Load login data..");
            
            return data;
        } catch (ClassNotFoundException ex) {
            return new LoginData();
        } catch (IOException ex) {
            return new LoginData();
        }
    }
}
