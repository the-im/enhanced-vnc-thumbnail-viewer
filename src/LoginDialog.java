/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Optimized code
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - Added KeyListener
 * 
 * Enhanced VNC Thumbnail Viewer 1.000
 *  - Login dialog will be shown on start up
 *  - Also it can be set disable by going to Settings menu -> Login
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginDialog extends JDialog implements ActionListener, KeyListener {

    private EnhancedVncThumbnailViewer tnviewer;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, cancelButton;

    public LoginDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        // Initial components
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        usernameField = new JTextField(LoginSetting.getIsRemember() ? LoginSetting.getUsername() : "", 15);
        passwordField = new JPasswordField("", 15);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
        usernameField.addKeyListener(this); // Added on evnctv 1.001
        passwordField.addKeyListener(this); // Added on evnctv 1.001
        
        // Layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        gridbag.setConstraints(usernameField, c);
        gridbag.setConstraints(passwordField, c);
        gridbag.setConstraints(loginButton, c);

        // Add to dialog
        add(new JLabel("Username:", JLabel.RIGHT));
        add(usernameField);
        add(new JLabel("Password:", JLabel.RIGHT));
        add(passwordField);
        add(cancelButton);
        add(loginButton);
        
        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("Login");
        pack();
        validate();
        setResizable(false);
        setVisible(true);
    }

    private boolean checkPassword(String user, String pass) {
        if (LoginSetting.getUsername().equals(user) && LoginSetting.getPassword().equals(pass)) {
            return true;
        } else {
            return false;
        }
    }
    // Added on evnctv 1.001
    private void checkLogin() {
        if (checkPassword(usernameField.getText(), passwordField.getText())) {
            this.dispose();
        } else {
            JOptionPane.showConfirmDialog(this, "Invalid Username/Password", "error", JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            checkLogin();
        } else if (e.getSource() == cancelButton) {
            tnviewer.quit();
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
            checkLogin();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
