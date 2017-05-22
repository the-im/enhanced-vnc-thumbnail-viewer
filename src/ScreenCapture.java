/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - To manage screen capture
 */

import javax.swing.*;

public class ScreenCapture implements Runnable {

    private EnhancedVncThumbnailViewer evnctv;
    private VncViewersList viewersList;
    private boolean isError = false;

    public ScreenCapture(EnhancedVncThumbnailViewer t, VncViewersList v) {
        evnctv = t;
        viewersList = v;
    }

    public void run() {
        int i = 0;
        
        while (true) {
            
            // If automatic capture is start
            if (ScreenCaptureSetting.getIsEnable()) {
                int size = viewersList.size();

                try {
                    // Delay each viewer
                    Thread.sleep(ScreenCaptureSetting.getDelay() * 1000);

                    // If has viewer(s)
                    if (size > 0) {
                        boolean isClosed = false;
                        VncViewer v = (VncViewer) viewersList.get(i);

                        // If viewer has been closed
                        if (v.rfb.closed()) {
                            boolean reconnecting = true;

                            // Viewer is reconnecting
                            while(reconnecting) {
                                v = viewersList.launchViewerScreenCapture(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);

                                // Delay waiting for reconnecting
                                Thread.sleep(3000);

                                reconnecting = (v.rfb == null) ? true : false;        
                                isClosed = true;
                            }
                        } else {
                        }

                        // To capture a screen
                        try {
                            v.vc.capture();

                            if (isClosed) {
                                v.disconnect();
                            }

                            isError = false;
                            i++;
                        } catch(Exception e){
                            if (!isError) {
                                JOptionPane.showConfirmDialog(evnctv, "Cannot save a screen. Please make sure your path is valid.", "Error", JOptionPane.DEFAULT_OPTION);
                                isError = true;
                            }
                        }

                        // Next viewer
                        i = (i == size) ? 0 : i;
                    }
                } catch (InterruptedException ex) {
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            
        }
    }
}