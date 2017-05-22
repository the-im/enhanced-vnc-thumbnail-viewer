/*
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Renamed from SlideShowData class
 *  - Removed Serializable interface
 *  - Added static to attributes & methods
 *  - Added static final attributes
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - Keep slide show data: on/off slide show, delay
 */

public class SlideshowSetting {
    
    public static final int INIT_DELAY = 4;
    private static int delay = INIT_DELAY;

    public static int getDelay() {
        return delay;
    }

    public static void setDelay(int aDelay) {
        delay = aDelay;
    }
}