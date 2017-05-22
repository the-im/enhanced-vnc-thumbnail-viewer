/*
 * Enhanced VNC Thumbnail Viewer 1.001
 */

import java.io.*;

public class SlideShowIO {
    public static void writeFile(SlideShowData data) {
        try {
            FileOutputStream f = new FileOutputStream(Setting.SLIDE_SHOW_FILE_NAME);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(data);
            o.close();
            f.close();

            System.out.println("Save slide show setting.. Delay:" + data.getDelay() + " second(s)");
        } catch (IOException ex) {
        }
    }

    public static SlideShowData readFile() {
        try {
            FileInputStream f = new FileInputStream(Setting.SLIDE_SHOW_FILE_NAME);
            ObjectInputStream o = new ObjectInputStream(f);
            SlideShowData data = (SlideShowData) o.readObject();
            o.close();
            f.close();

            System.out.println("Load slide show setting.. Delay:" + data.getDelay() + " second(s)");

            return data;
        } catch (ClassNotFoundException ex) {
            return new SlideShowData();
        } catch (IOException ex) {
            return new SlideShowData();
        }
    }
}
