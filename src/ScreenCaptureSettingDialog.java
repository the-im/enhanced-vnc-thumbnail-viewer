/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  This dialog uses for capture screen settings
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ScreenCaptureSettingDialog extends JDialog implements ActionListener, KeyListener {

    private static final int PADDING = 15;
    private static final int SPACING = 9;
    
    private JTextField pathField, delayField;
    private JButton selectPathButton, okButton, cancelButton;
    private JFileChooser chooser;
    
    public ScreenCaptureSettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        
        // Initial components
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        
        chooser = new JFileChooser();
        chooser.setDialogTitle("Browse...");
        //chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
    
        pathField = new JTextField(ScreenCaptureSetting.getPath(), 20);
        delayField = new JTextField(ScreenCaptureSetting.getDelay() + "", 5);
        selectPathButton = new JButton("Browse...");
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        
        JLabel delayLabel = new JLabel("Delay:");
        JLabel delayLabel2 = new JLabel("second(s) between each viewer");
        JLabel pathLabel = new JLabel("Save to:");
        
        pathField.setEnabled(false);

        selectPathButton.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        delayField.addKeyListener(this);
        
        // Panel
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel();
        panel.add(delayLabel);
        panel.add(delayField);
        panel.add(delayLabel2);
        panel.add(pathLabel);
        panel.add(pathField);
        panel.add(selectPathButton);
        panel.add(okButton);
        panel.add(cancelButton);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, delayLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, delayLabel, PADDING + 5, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, delayField, PADDING + 50, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, delayField, PADDING + 3, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, delayLabel2, 3, SpringLayout.EAST, delayField);
        layout.putConstraint(SpringLayout.NORTH, delayLabel2, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.WEST, pathLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, pathLabel, SPACING, SpringLayout.SOUTH, delayLabel);
        layout.putConstraint(SpringLayout.WEST, pathField, PADDING + 50, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, pathField, SPACING - 2, SpringLayout.SOUTH, delayLabel);
        layout.putConstraint(SpringLayout.WEST, selectPathButton, 3, SpringLayout.EAST, pathField);
        layout.putConstraint(SpringLayout.NORTH, selectPathButton, SPACING - 3, SpringLayout.SOUTH, delayLabel);
        
        layout.putConstraint(SpringLayout.EAST, cancelButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, cancelButton, -PADDING - 5, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, okButton, -PADDING + 9, SpringLayout.WEST, cancelButton);
        layout.putConstraint(SpringLayout.SOUTH, okButton, -PADDING - 5, SpringLayout.SOUTH, panel);
        
        add(panel);

        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("Screen Capture Settings");
        setSize(430, 180);
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
                    int delay = Integer.parseInt(delayField.getText().trim());
                    
                    // If delay has been changed
                    if (ScreenCaptureSetting.getDelay() != delay) {
                        String msg = "Changed delay from " + ScreenCaptureSetting.getDelay() + " to " + delay + " second(s)";
                        RecentSettingsList.addRecent(new RecentSetting(msg, "Screen Capture"));
                        System.out.println(msg);
                    }
                    
                    // If path has been changed
                    if (!ScreenCaptureSetting.getPath().equals(pathField.getText().trim())) {
                        String msg = "Changed path to save screens";
                        RecentSettingsList.addRecent(new RecentSetting(msg, "Screen Capture"));
                        System.out.println(msg);
                    }
                    
                    ScreenCaptureSetting.setDelay(delay);
                    ScreenCaptureSetting.setPath(pathField.getText().trim());
                    
                    this.dispose();
                }
            } catch (NumberFormatException en) {
                JOptionPane.showMessageDialog(this, "Delay must be integer number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == selectPathButton) {
            if (chooser.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile() + "");
            }
        } else if (e.getSource() == okButton) {
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