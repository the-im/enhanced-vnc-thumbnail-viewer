/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - Fixed bug value of month
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Recent settings
 */

import java.util.Calendar;

public class RecentSetting {
    
    private String type;
    private String title;
    private String date;
    private String time;

    public RecentSetting () {}
    
    public RecentSetting (String title, String type) {
        this.title = title;
        this.type = type;
        
        Calendar c = Calendar.getInstance();
        this.date = addZeroOnDateTime(c.get(Calendar.MONTH) + 1) + "/" + 
                addZeroOnDateTime(c.get(Calendar.DATE)) + "/" + 
                c.get(Calendar.YEAR);
        
        this.time = addZeroOnDateTime(c.get(Calendar.HOUR)) + ":" + 
                addZeroOnDateTime(c.get(Calendar.MINUTE)) + ":" + 
                addZeroOnDateTime(c.get(Calendar.SECOND)) + " " + 
                (c.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
    }
    
    public RecentSetting (String title, String type, String date, String time) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.time = time;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    private String addZeroOnDateTime(int dt) {
        return (dt < 10 ? "0" + dt : dt) + "";
    }
}