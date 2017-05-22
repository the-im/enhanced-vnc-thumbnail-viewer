/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * Login dialog will be shown on start up
 * Also it can be set disable by going to Setting menu -> Login
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class LoginDialog extends JDialog implements ActionListener {

    VncThumbnailViewer tnviewer;
    LoginSettingDialog loginSettingDialog;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton;
    JLabel errorLabel;

    public LoginDialog(VncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;
        loginSettingDialog = new LoginSettingDialog(tnviewer);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        setFont(new Font("Helvetica", Font.PLAIN, 14));

        usernameField = new JTextField("", 15);
        passwordField = new JPasswordField("", 15);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        c.gridwidth = GridBagConstraints.REMAINDER;

        gridbag.setConstraints(usernameField, c);
        gridbag.setConstraints(passwordField, c);
        gridbag.setConstraints(loginButton, c);

        add(new JLabel("Username:", JLabel.RIGHT));
        add(usernameField);
        add(new JLabel("Password:", JLabel.RIGHT));
        add(passwordField);
        add(cancelButton);
        add(loginButton);
        
        setTitle("Login");

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
    
    private boolean checkPassword(String user, String pass){
        LoginData loginData = loginSettingDialog.readFile();
        if(loginData.getUsername().equals(user) && loginData.getPassword().equals(pass)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            if(checkPassword(usernameField.getText(),passwordField.getText())){
                this.dispose();
            }
            else{
                JOptionPane.showConfirmDialog(this, "Invalid Username/Password", "error", JOptionPane.DEFAULT_OPTION);
            }
        }
        if (e.getSource() == cancelButton) {
            tnviewer.quit();
            this.dispose();
        }
    }
}
