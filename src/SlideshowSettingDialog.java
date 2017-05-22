/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - Changed message dialogs
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Added recent settings logs
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - To access this dialog just going to Settings menu -> Slide Show
 *  - You can setting about slide show on this
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SlideshowSettingDialog extends JDialog implements ActionListener, KeyListener {

    private JTextField delayField;
    private JButton okButton, cancelButton;

    public SlideshowSettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);

        setLayout(new GridBagLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        delayField = new JTextField(SlideshowSetting.getDelay() + "", 5);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        delayField.addKeyListener(this);

        add(new JLabel("Delay:", JLabel.RIGHT));
        add(delayField);
        add(new JLabel(" second(s)", JLabel.RIGHT));
        add(okButton);
        add(cancelButton);

        setTitle("Slideshow Settings");

        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        pack();
        validate();
        setResizable(false);
        setVisible(true);
    }

    private void saveSetting() {
        if (delayField.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter delay", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                if (Integer.parseInt(delayField.getText().trim()) <= 0) {
                    JOptionPane.showMessageDialog(this, "Delay must more than 0 second", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    /* *
                     * Added on evnctv 1.002
                     * To save recent settings
                     */
                    int delay = Integer.parseInt(delayField.getText().trim());
                    
                    // If delay has been changed
                    if (SlideshowSetting.getDelay() != delay) {
                        String msg = "Changed delay from " + SlideshowSetting.getDelay() + " to " + delay + " second(s)";
                        RecentSettingsList.addRecent(new RecentSetting(msg, "Slideshow"));
                        System.out.println(msg);
                    }
                    
                    
                    SlideshowSetting.setDelay(delay);
                    this.dispose();
                }
            } catch (NumberFormatException en) {
                JOptionPane.showMessageDialog(this, "Delay must be integer number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == okButton) {
            saveSetting();
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEvent.VK_ENTER == e.getKeyCode()) {
            saveSetting();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}