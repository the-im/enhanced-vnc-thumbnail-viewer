/*
 * Enhanced VNC Thumbnail Viewer 1.001
 * To access this dialog just going to Settings menu -> Slide Show
 * You can setting about slide show on this
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SlideShowSettingDialog extends JDialog implements ActionListener, KeyListener {

    EnhancedVncThumbnailViewer tnviewer;
    SlideShowData slideShowData;
    JTextField delayField;
    JButton okButton, cancelButton;

    public SlideShowSettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        setLayout(new GridBagLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        delayField = new JTextField(Setting.getSlideShowData().getDelay() + "", 5);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        delayField.addKeyListener(this); // Added on evnc 1.001

        add(new JLabel("Delay:", JLabel.RIGHT));
        add(delayField);
        add(new JLabel(" second(s)", JLabel.RIGHT));
        add(okButton);
        add(cancelButton);

        setTitle("Slide show settings");

        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        pack();
        validate();
        setResizable(false);
        setVisible(false);
    }

    private void saveSetting() {
        if (delayField.getText().trim().equals("")) {
            JOptionPane.showConfirmDialog(this, "Please enter delay", "Error", JOptionPane.DEFAULT_OPTION);
        } else {
            try {
                if (Integer.parseInt(delayField.getText().trim()) <= 0) {
                    JOptionPane.showConfirmDialog(this, "Delay must more than 0 second", "Error", JOptionPane.DEFAULT_OPTION);
                } else {
                    Setting.getSlideShowData().setDelay(Integer.parseInt(delayField.getText().trim()));
                    SlideShowIO.writeFile(Setting.getSlideShowData());

                    this.dispose();
                }
            } catch (NumberFormatException en) {
                JOptionPane.showConfirmDialog(this, "Delay must be integer number", "Error", JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == okButton) {
            saveSetting();
        }
        if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }

    /*
     * Added on evnctv 1.001
     */
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