/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * To access this dialog just going to Setting menu -> Login
 * This dialog uses for setting about login such as enable/disable login on start up
 * You can set account for login on this
 * 
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class LoginSettingDialog extends JDialog implements ActionListener {

    VncThumbnailViewer tnviewer;
    LoginData loginData;
    JTextField usernameField;
    JPasswordField newPasswordField, confirmNewPasswordField, presentPasswordField;
    JButton okButton, cancelButton, deleteButton;
    JRadioButton noLoginRadio, loginRadio;
    JLabel errorLabel;

    public LoginSettingDialog(VncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        PlatformUI.getLookAndFeel();
        
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

        init();
        
        c.gridwidth = GridBagConstraints.REMAINDER;

        // No account
        if(loginData.getUsername() == null || loginData.getUsername().equals("")){
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
        }
        else{
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
    
    private void init(){
        loginData = readFile();
        
        if(loginData.getIsAuth())
            loginRadio.setSelected(true);
        else
            noLoginRadio.setSelected(true);
        
        enableLogin();
    }
    
    private void enableLogin(){
        if(noLoginRadio.isSelected()){
            usernameField.setEnabled(false);
            newPasswordField.setEnabled(false);
            confirmNewPasswordField.setEnabled(false);
        }
        else if(loginRadio.isSelected()){
            usernameField.setEnabled(true);
            newPasswordField.setEnabled(true);
            confirmNewPasswordField.setEnabled(true);
        }
    }
    
    private void writeFile(){
        try {
            FileOutputStream f = new FileOutputStream("login.b");
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(loginData);
            o.close();
            f.close();

            System.out.println("Save login data..");
        } catch (IOException ex) {
        }
    }
    
    public LoginData readFile(){
        try {
            FileInputStream f = new FileInputStream("login.b");
            ObjectInputStream o = new ObjectInputStream(f);
            loginData = (LoginData) o.readObject();
            o.close();
            f.close();
            
            System.out.println("Load login data..");
            
            return loginData;
        } catch (ClassNotFoundException ex) {
            return new LoginData();
        } catch (IOException ex) {
            return new LoginData();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        enableLogin();
        
        if (e.getSource() == okButton) {

            // When no login on start up is selected
            if(noLoginRadio.isSelected()){
                loginData.setIsAuth(false);
                
                // No account
                if(loginData.getUsername() == null || loginData.getUsername().equals("")){
                    writeFile();
                    this.dispose();
                }
                else{
                    // Check present password
                    if(!presentPasswordField.getText().equals(loginData.getPassword())){
                        JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                    }
                    else{
                        writeFile();
                        this.dispose();
                    }
                }
                
            }
            
            // When use login on start up is selected
            if(loginRadio.isSelected()){
                
                // No account
                if(loginData.getUsername() == null || loginData.getUsername().equals("")){
                    if(usernameField.getText().trim().equals("") || newPasswordField.getText().equals("") || confirmNewPasswordField.getText().equals("")){
                        JOptionPane.showConfirmDialog(this, "Please enter username/password", "Error", JOptionPane.DEFAULT_OPTION);
                    }
                    else if(!newPasswordField.getText().equals(confirmNewPasswordField.getText())){
                        JOptionPane.showConfirmDialog(this, "Password and confirm password do not match", "Error", JOptionPane.DEFAULT_OPTION);
                    }
                    else{
                        loginData.setUsername(usernameField.getText().trim());
                        loginData.setPassword(newPasswordField.getText());
                        loginData.setIsAuth(true);
                        writeFile();
                        this.dispose();
                        JOptionPane.showConfirmDialog(this, "The account has been created already", "Notification", JOptionPane.DEFAULT_OPTION);
                        new LoginDialog(tnviewer);
                    }
                }
                else{
                    // Change password
                    if(!newPasswordField.getText().isEmpty() || !confirmNewPasswordField.getText().isEmpty()){
                        if(!newPasswordField.getText().equals(confirmNewPasswordField.getText())){
                            JOptionPane.showConfirmDialog(this, "New password and confirm new password do not match", "Error", JOptionPane.DEFAULT_OPTION);
                        }
                        else if(!newPasswordField.getText().equals(confirmNewPasswordField.getText())){
                            JOptionPane.showConfirmDialog(this, "New password ", "Error", JOptionPane.DEFAULT_OPTION);
                        }
                        else{
                            // Check present password
                            if(!presentPasswordField.getText().equals(loginData.getPassword())){
                                JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                            }
                            else{
                                loginData.setPassword(newPasswordField.getText());
                                loginData.setIsAuth(true);
                                writeFile();
                                this.dispose();
                                JOptionPane.showConfirmDialog(this, "Password has been changed already", "Notification", JOptionPane.DEFAULT_OPTION);
                                new LoginDialog(tnviewer);
                            }
                        }
                    }
                    else{
                        // Check present password
                        if(!presentPasswordField.getText().equals(loginData.getPassword())){
                            JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                        }
                        else{
                            loginData.setIsAuth(true);
                            writeFile();
                            this.dispose();
                            new LoginDialog(tnviewer);
                        }
                    }
                }
            }
        }
        if (e.getSource() == deleteButton) {
            if(JOptionPane.showConfirmDialog(this, "Do you want to delete this account?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == 0){
                // Check present password
                if(!presentPasswordField.getText().equals(loginData.getPassword())){
                    JOptionPane.showConfirmDialog(this, "Invalid present password", "Error", JOptionPane.DEFAULT_OPTION);
                } else {
                    loginData.setUsername("");
                    loginData.setPassword("");
                    loginData.setIsAuth(false);
                    writeFile();
                    this.dispose();
                    JOptionPane.showConfirmDialog(this, "This account has been deleted already", "Notification", JOptionPane.DEFAULT_OPTION);
                }
            }
        }
        if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }
}
