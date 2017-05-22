/*
 * Enhanced VNC Thumbnail Viewer 1.001
 */

import java.io.*;

public class ProxyIO {
    public static void writeFile(ProxyData data){
        try {
            FileOutputStream f = new FileOutputStream(Setting.PROXY_FILE_NAME);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(data);
            o.close();
            f.close();

            System.out.println("Save proxy setting.. Host:"+ data.getHost() +" Port:"+ data.getPort() +" Enable:"+ data.getIsProxy());
        } catch (IOException ex) {
        }
    }
    
    public static ProxyData readFile(){
        try {
            FileInputStream f = new FileInputStream("proxy.b");
            ObjectInputStream o = new ObjectInputStream(f);
            ProxyData data = (ProxyData) o.readObject();
            o.close();
            f.close();
            
            System.out.println("Load proxy setting.. Host:"+ data.getHost() +" Port:"+ data.getPort() +" Enable:"+ data.getIsProxy());
            
            return data;
        } catch (ClassNotFoundException ex) {
            return new ProxyData();
        } catch (IOException ex) {
            return new ProxyData();
        }
    }
}
