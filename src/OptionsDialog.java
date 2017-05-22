/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Dialog for settings
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
    
    public OptionsDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        
        evnctv = tnviewer;
        layout = new SpringLayout();
        
        // Tab pane section
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Slideshow", getSlideshowTab());
        tabPane.addTab("Proxy", getProxyTab());
        tabPane.addTab("Login", getLoginTab());
        
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
        setSize(400,300);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            if (LoginSetting.getIsRemember() != loginRememberCheckbox.isSelected()) {
                LoginSetting.setIsRemember(loginRememberCheckbox.isSelected());
                
                String msg = loginRememberCheckbox.isSelected() ? "Changed to remember username" : "Changed to not remember username";
                RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
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
        }
    }
}