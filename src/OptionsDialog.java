/* *
 * Enhanced VNC Thumbnail Viewer 1.4.0
 *      - Added theme tab
 *
 * Enhanced VNC Thumbnail Viewer 1.003
 *      - Added screen capture tab
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *      - Dialog for settings
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OptionsDialog extends JDialog implements ActionListener {
    
    private static final int PADDING = 15;
    private static final int SPACING = 9;
    
    private EnhancedVncThumbnailViewer evnctv;
    private JButton slideShowSettingButton;
    private JButton proxySettingButton;
    private JButton loginSettingButton;
    private SpringLayout layout;
    private JCheckBox loginRememberCheckbox;
    private JButton okButton, cancelButton;
    
    /* Added on evnctv 1.003 */
    private JButton scSettingButton;
    private JCheckBox scStartCaptureCheckbox;
    
    /* Added on evnctv 1.4.0 */
    private JButton themeSettingButton;
    
    public OptionsDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        
        evnctv = tnviewer;
        layout = new SpringLayout();
        
        // Tab pane section
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Slideshow", getSlideshowTab());
        tabPane.addTab("Proxy", getProxyTab());
        tabPane.addTab("Login", getLoginTab());
        tabPane.addTab("Screen Capture", getScreenCaptureTab());
        tabPane.addTab("Theme", getThemeTab());
        
        // Button section
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // Add components
        add(tabPane);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("Options");
        setSize(500,300);
        validate();
        setResizable(false);
        setVisible(true);
    }
    
    private JPanel getSlideshowTab() {
        // Initial components
        JLabel delaySettingLabel = new JLabel("Delay for slideshow viewers");
        slideShowSettingButton = new JButton("Setting...");
        slideShowSettingButton.addActionListener(this);
        
        // Panel
        JPanel panel = new JPanel();
        panel.add(delaySettingLabel);
        panel.add(slideShowSettingButton);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, delaySettingLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, delaySettingLabel, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.EAST, slideShowSettingButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, slideShowSettingButton, PADDING, SpringLayout.NORTH, panel);
        
        return panel;
    }
    
    private JPanel getProxyTab() {
        // Initial components
        JLabel proxySettingLabel = new JLabel("How to connects to each machine");
        proxySettingButton = new JButton("Setting...");
        proxySettingButton.addActionListener(this);
        
        // Panel
        JPanel panel = new JPanel();
        panel.add(proxySettingLabel);
        panel.add(proxySettingButton);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, proxySettingLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, proxySettingLabel, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.EAST, proxySettingButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, proxySettingButton, PADDING, SpringLayout.NORTH, panel);
        
        return panel;
    }
    
    private JPanel getLoginTab() {
        // Initial components
        JLabel loginSettingLabel = new JLabel("Authentication when the file was loaded");
        loginSettingButton = new JButton("Setting...");
        loginRememberCheckbox = new JCheckBox("Remember username");
        loginRememberCheckbox.setSelected(LoginSetting.getIsRemember());
                
        loginSettingButton.addActionListener(this);

        // Panel
        JPanel panel = new JPanel();
        panel.add(loginSettingLabel);
        panel.add(loginSettingButton);
        panel.add(loginRememberCheckbox);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, loginSettingLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, loginSettingLabel, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.EAST, loginSettingButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, loginSettingButton, PADDING, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.WEST, loginRememberCheckbox, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, loginRememberCheckbox, SPACING, SpringLayout.SOUTH, loginSettingLabel);
        
        return panel;
    }
    
    /* *
     * Added on evnctv 1.003
     */
    private JPanel getScreenCaptureTab() {
        // Initial components
        JLabel scSettingLabel = new JLabel("Automatic capture screen each viewer");
        scSettingButton = new JButton("Setting...");
        scStartCaptureCheckbox = new JCheckBox("Enable capture");
        scStartCaptureCheckbox.setSelected(ScreenCaptureSetting.getIsEnable());
                
        scSettingButton.addActionListener(this);

        // Panel
        JPanel panel = new JPanel();
        panel.add(scSettingLabel);
        panel.add(scSettingButton);
        panel.add(scStartCaptureCheckbox);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, scSettingLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scSettingLabel, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.EAST, scSettingButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, scSettingButton, PADDING, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.WEST, scStartCaptureCheckbox, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scStartCaptureCheckbox, SPACING, SpringLayout.SOUTH, scSettingLabel);
        
        return panel;
    }
    
    /* *
     * Added on evnctv 1.4.0
     */
    private JPanel getThemeTab() {
        // Initial components
        JLabel themeSettingLabel = new JLabel("Your preferred theme for display");
        themeSettingButton = new JButton("Setting...");
                
        themeSettingButton.addActionListener(this);

        // Panel
        JPanel panel = new JPanel();
        panel.add(themeSettingLabel);
        panel.add(themeSettingButton);
        
        // Layout
        panel.setLayout(layout);
        
        layout.putConstraint(SpringLayout.WEST, themeSettingLabel, PADDING, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, themeSettingLabel, PADDING + 5, SpringLayout.NORTH, panel);
        
        layout.putConstraint(SpringLayout.EAST, themeSettingButton, -PADDING, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, themeSettingButton, PADDING, SpringLayout.NORTH, panel);
        
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            // Login
            if (LoginSetting.getIsRemember() != loginRememberCheckbox.isSelected()) {
                LoginSetting.setIsRemember(loginRememberCheckbox.isSelected());
                
                String msg = loginRememberCheckbox.isSelected() ? "Changed to remember username" : "Changed to not remember username";
                RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                System.out.println(msg);
            }
            
            // Screen capture
            else if (ScreenCaptureSetting.getIsEnable() != scStartCaptureCheckbox.isSelected()) {
                ScreenCaptureSetting.setIsEnable(scStartCaptureCheckbox.isSelected());
                
                String msg = scStartCaptureCheckbox.isSelected() ? "Changed to enable capture" : "Changed to disable capture";
                RecentSettingsList.addRecent(new RecentSetting(msg, "Screen Capture"));
                System.out.println(msg);
            }
            
            this.dispose();
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        } else if (e.getSource() == slideShowSettingButton) {
            new SlideshowSettingDialog(evnctv);
        } else if (e.getSource() == proxySettingButton) {
            new ProxySettingDialog(evnctv);
        } else if (e.getSource() == loginSettingButton) {
            new LoginSettingDialog(evnctv);
        } else if (e.getSource() == scSettingButton) {
            new ScreenCaptureSettingDialog(evnctv);
        }  else if (e.getSource() == themeSettingButton) {
            new ThemeSettingDialog(evnctv);
        }
    }
}