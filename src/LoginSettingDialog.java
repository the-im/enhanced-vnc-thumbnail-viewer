/* *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - Changed message dialogs
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Added recent settings logs
 *  - Optimized code & re-comments
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - Moved readFile(), writeFile() method to LoginIO class
 *  - Added KeyListener
 * 
 * Enhanced VNC Thumbnail Viewer 1.000
 *  To access this dialog just going to Settings menu -> Login
 *  This dialog uses for setting about login such as enable/disable login on start up
 *  You can set account for login on this
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginSettingDialog extends JDialog implements ActionListener, KeyListener {

    private EnhancedVncThumbnailViewer tnviewer;
    private JTextField usernameField;
    private JPasswordField newPasswordField, confirmNewPasswordField, presentPasswordField;
    private JButton okButton, cancelButton, deleteButton;
    private JRadioButton noLoginRadio, loginRadio;

    public LoginSettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;
        
        // Initial components
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        noLoginRadio = new JRadioButton("No authentication");
        loginRadio = new JRadioButton("Use authentication");
        usernameField = new JTextField("", 15);
        newPasswordField = new JPasswordField("", 15);
        confirmNewPasswordField = new JPasswordField("", 15);
        presentPasswordField = new JPasswordField("", 15);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        deleteButton = new JButton("Delete");

        ButtonGroup loginGroup = new ButtonGroup();
        loginGroup.add(noLoginRadio);
        loginGroup.add(loginRadio);

        noLoginRadio.addActionListener(this);
        loginRadio.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        // Added on evcntv 1.001
        noLoginRadio.addKeyListener(this);
        loginRadio.addKeyListener(this);
        usernameField.addKeyListener(this);
        newPasswordField.addKeyListener(this);
        confirmNewPasswordField.addKeyListener(this);
        presentPasswordField.addKeyListener(this);

        // Layout
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        // Check login has enabled?
        if (LoginSetting.getIsEnable()) {
            loginRadio.setSelected(true);
        } else {
            noLoginRadio.setSelected(true);
        }
        enableLogin();
        
        // If have no account
        if (LoginSetting.getUsername() == null || LoginSetting.getUsername().equals("")) {
            gridbag.setConstraints(noLoginRadio, c);
            gridbag.setConstraints(loginRadio, c);
            gridbag.setConstraints(usernameField, c);
            gridbag.setConstraints(newPasswordField, c);
            gridbag.setConstraints(confirmNewPasswordField, c);
            gridbag.setConstraints(okButton, c);

            add(noLoginRadio);
            add(loginRadio);
            add(new JLabel("*Username:", JLabel.LEFT));
            add(usernameField);
            add(new JLabel("*Password:", JLabel.RIGHT));
            add(newPasswordField);
            add(new JLabel("*Confirm password:", JLabel.RIGHT));
            add(confirmNewPasswordField);
            add(cancelButton);
            add(okButton);
        } else {
            gridbag.setConstraints(noLoginRadio, c);
            gridbag.setConstraints(loginRadio, c);
            gridbag.setConstraints(newPasswordField, c);
            gridbag.setConstraints(confirmNewPasswordField, c);
            gridbag.setConstraints(presentPasswordField, c);
            gridbag.setConstraints(okButton, c);

            add(noLoginRadio);
            add(loginRadio);
            add(new JLabel("New password:", JLabel.RIGHT));
            add(newPasswordField);
            add(new JLabel("Confirm new password:", JLabel.RIGHT));
            add(confirmNewPasswordField);
            add(new JLabel("*Present password:", JLabel.RIGHT));
            add(presentPasswordField);
            add(cancelButton);
            add(deleteButton);
            add(okButton);
        }

        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("Login Settings");
        pack();
        validate();
        setResizable(false);
        setVisible(true);
    }

    private void enableLogin() {
        if (noLoginRadio.isSelected()) {
            usernameField.setEnabled(false);
            newPasswordField.setEnabled(false);
            confirmNewPasswordField.setEnabled(false);
        } else if (loginRadio.isSelected()) {
            usernameField.setEnabled(true);
            newPasswordField.setEnabled(true);
            confirmNewPasswordField.setEnabled(true);
        }
    }

    // Added on evnctv 1.001
    private void saveSetting() {
        // When no login on start up is selected
        if (noLoginRadio.isSelected()) {
            
            // No account
            if (LoginSetting.getUsername() == null || LoginSetting.getUsername().equals("")) {
                LoginSetting.setIsEnable(false);
                this.dispose();
            } else {
                // Check present password
                if (!presentPasswordField.getText().equals(LoginSetting.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Invalid present password", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    /* *
                     * Added on evnctv 1.002
                     * To save recent settings
                     */
                    // Change to no login
                    if (LoginSetting.getIsEnable()) {
                        String msg = "Changed to no authentication when the file was loaded";
                        RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                        System.out.println(msg);
                    }
                    
                    
                    LoginSetting.setIsEnable(false);
                    this.dispose();
                }
            }
            
        }

        // When use login on start up is selected
        if (loginRadio.isSelected()) {
            
            // No account
            if (LoginSetting.getUsername() == null || LoginSetting.getUsername().equals("")) {
                if (usernameField.getText().trim().equals("") || newPasswordField.getText().equals("") || confirmNewPasswordField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Please enter username/password", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    JOptionPane.showMessageDialog(this, "Password and confirm password do not match", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    /* *
                     * Added on evnctv 1.002
                     * To save recent settings
                     */
                    // Change to use login
                    String msg = "Changed to use authentication when the file was loaded & created new account";
                    RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                    System.out.println(msg);
                    
                
                    LoginSetting.setUsername(usernameField.getText().trim());
                    LoginSetting.setPassword(newPasswordField.getText());
                    LoginSetting.setIsEnable(true);
                    
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "The account has been created already", "Notification", JOptionPane.INFORMATION_MESSAGE);
                    new LoginDialog(tnviewer);
                }
            } else {
                // Change password
                if (!newPasswordField.getText().isEmpty() || !confirmNewPasswordField.getText().isEmpty()) {
                    if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                        JOptionPane.showMessageDialog(this, "New password and confirm new password do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                        JOptionPane.showMessageDialog(this, "New password ", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Check present password
                        if (!presentPasswordField.getText().equals(LoginSetting.getPassword())) {
                            JOptionPane.showMessageDialog(this, "Invalid present password", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            /* *
                             * Added on evnctv 1.002
                             * To save recent settings
                             */
                            // Change to use login
                            String msg = LoginSetting.getIsEnable() ? "Changed password" : "Changed to use authentication when the file was loaded & changed password";
                            RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                            System.out.println(msg);
                                
                            
                            LoginSetting.setPassword(newPasswordField.getText());
                            LoginSetting.setIsEnable(true);
                    
                            this.dispose();
                            JOptionPane.showMessageDialog(this, "Password has been changed already", "Notification", JOptionPane.INFORMATION_MESSAGE);
                            new LoginDialog(tnviewer);
                        }
                    }
                } else {
                    // Check present password
                    if (!presentPasswordField.getText().equals(LoginSetting.getPassword())) {
                        JOptionPane.showMessageDialog(this, "Invalid present password", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        /* *
                         * Added on evnctv 1.002
                         * To save recent settings
                         */
                        // Change to use login
                        if (!LoginSetting.getIsEnable()) {
                            String msg = "Changed to use authentication when the file was loaded";
                            RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                            System.out.println(msg);
                        }
                            
                        
                        LoginSetting.setIsEnable(true);
                        this.dispose();
                        new LoginDialog(tnviewer);
                    }
                }
            }
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        enableLogin();

        if (e.getSource() == okButton) {
            saveSetting();
        } else if (e.getSource() == deleteButton) {
            if (JOptionPane.showConfirmDialog(this, "Do you want to delete this account?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == 0) {
                // Check present password
                if (!presentPasswordField.getText().equals(LoginSetting.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Invalid present password", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    LoginSetting.setUsername("");
                    LoginSetting.setPassword("");
                    LoginSetting.setIsEnable(false);

                    /* *
                     * Added on evnctv 1.002
                     * To save recent settings
                     */
                    String msg = "Deleted account";
                    RecentSettingsList.addRecent(new RecentSetting(msg, "Login"));
                    System.out.println(msg);
                    
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "This account has been deleted already", "Notification", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }

    // Added on evnctv 1.001
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