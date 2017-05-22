/*
 * Enhanced VNC Thumbnail Viewer 1.001
 * Keep slide show data: on/off slide show, delay
 */

import java.io.Serializable;

public class SlideShowData implements Serializable {
    private int delay = 4;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
    
}
