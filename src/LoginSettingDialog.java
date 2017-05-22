/*
 * Enhanced VNC Thumbnail Viewer 1.001
 *      - Moved readFile(), writeFile() method to LoginIO class
 *      - Added KeyListener
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 * To access this dialog just going to Setting menu -> Login
 * This dialog uses for setting about login such as enable/disable login on start up
 * You can set account for login on this
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class LoginSettingDialog extends JDialog implements ActionListener, KeyListener {

    EnhancedVncThumbnailViewer tnviewer;
    JTextField usernameField;
    JPasswordField newPasswordField, confirmNewPasswordField, presentPasswordField;
    JButton okButton, cancelButton, deleteButton;
    JRadioButton noLoginRadio, loginRadio;
    JLabel errorLabel;

    public LoginSettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        setFont(new Font("Helvetica", Font.PLAIN, 14));

        noLoginRadio = new JRadioButton("No login on start up");
        loginRadio = new JRadioButton("Use login on start up");
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

        init();

        c.gridwidth = GridBagConstraints.REMAINDER;

        // If have no account
        if (Setting.getLoginData().getUsername() == null || Setting.getLoginData().getUsername().equals("")) {
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

        setTitle("Login setting");

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

    private void init() {
        if (Setting.getLoginData().getIsAuth()) {
            loginRadio.setSelected(true);
        } else {
            noLoginRadio.setSelected(true);
        }

        enableLogin();
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
            Setting.getLoginData().setIsAuth(false);

            // No account
            if (Setting.getLoginData().getUsername() == null || Setting.getLoginData().getUsername().equals("")) {
                LoginIO.writeFile(Setting.getLoginData());
                this.dispose();
            } else {
                // Check present password
                if (!presentPasswordField.getText().equals(Setting.getLoginData().getPassword())) {
                    JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                } else {
                    LoginIO.writeFile(Setting.getLoginData());
                    this.dispose();
                }
            }

        }

        // When use login on start up is selected
        if (loginRadio.isSelected()) {

            // No account
            if (Setting.getLoginData().getUsername() == null || Setting.getLoginData().getUsername().equals("")) {
                if (usernameField.getText().trim().equals("") || newPasswordField.getText().equals("") || confirmNewPasswordField.getText().equals("")) {
                    JOptionPane.showConfirmDialog(this, "Please enter username/password", "Error", JOptionPane.DEFAULT_OPTION);
                } else if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    JOptionPane.showConfirmDialog(this, "Password and confirm password do not match", "Error", JOptionPane.DEFAULT_OPTION);
                } else {
                    Setting.getLoginData().setUsername(usernameField.getText().trim());
                    Setting.getLoginData().setPassword(newPasswordField.getText());
                    Setting.getLoginData().setIsAuth(true);

                    LoginIO.writeFile(Setting.getLoginData());

                    this.dispose();
                    JOptionPane.showConfirmDialog(this, "The account has been created already", "Notification", JOptionPane.DEFAULT_OPTION);
                    new LoginDialog(tnviewer);
                }
            } else {
                // Change password
                if (!newPasswordField.getText().isEmpty() || !confirmNewPasswordField.getText().isEmpty()) {
                    if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                        JOptionPane.showConfirmDialog(this, "New password and confirm new password do not match", "Error", JOptionPane.DEFAULT_OPTION);
                    } else if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                        JOptionPane.showConfirmDialog(this, "New password ", "Error", JOptionPane.DEFAULT_OPTION);
                    } else {
                        // Check present password
                        if (!presentPasswordField.getText().equals(Setting.getLoginData().getPassword())) {
                            JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                        } else {
                            Setting.getLoginData().setPassword(newPasswordField.getText());
                            Setting.getLoginData().setIsAuth(true);

                            LoginIO.writeFile(Setting.getLoginData());

                            this.dispose();
                            JOptionPane.showConfirmDialog(this, "Password has been changed already", "Notification", JOptionPane.DEFAULT_OPTION);
                            new LoginDialog(tnviewer);
                        }
                    }
                } else {
                    // Check present password
                    if (!presentPasswordField.getText().equals(Setting.getLoginData().getPassword())) {
                        JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                    } else {
                        Setting.getLoginData().setIsAuth(true);

                        LoginIO.writeFile(Setting.getLoginData());

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
        }
        if (e.getSource() == deleteButton) {
            if (JOptionPane.showConfirmDialog(this, "Do you want to delete this account?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == 0) {
                // Check present password
                if (!presentPasswordField.getText().equals(Setting.getLoginData().getPassword())) {
                    JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                } else {
                    Setting.getLoginData().setUsername("");
                    Setting.getLoginData().setPassword("");
                    Setting.getLoginData().setIsAuth(false);

                    LoginIO.writeFile(Setting.getLoginData());

                    this.dispose();
                    JOptionPane.showConfirmDialog(this, "This account has been deleted already", "Notification", JOptionPane.DEFAULT_OPTION);
                }
            }
        }
        if (e.getSource() == cancelButton) {
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
