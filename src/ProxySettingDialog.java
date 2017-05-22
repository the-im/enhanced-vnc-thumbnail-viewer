/*
 * Enhanced VNC Thumbnail Viewer 1.001
 *      - Moved readFile(), writeFile() method to ProxyIO class
 *      - Added KeyListener
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 * To access this dialog just going to Settings menu -> Proxy
 * You can choose how to connect to vnc server via SOCKS5 or not
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProxySettingDialog extends JDialog implements ActionListener, KeyListener {

    EnhancedVncThumbnailViewer tnviewer;
    JTextField hostField, portField;
    JButton okButton, cancelButton;
    JRadioButton noProxyRadio, socks5Radio;
    JLabel errorLabel;

    public ProxySettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        // GUI Stuff:
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        setFont(new Font("Helvetica", Font.PLAIN, 14));

        noProxyRadio = new JRadioButton("No proxy");
        socks5Radio = new JRadioButton("Use SOCKS5");
        hostField = new JTextField("", 10);
        portField = new JTextField("", 5);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        ButtonGroup proxyGroup = new ButtonGroup();
        proxyGroup.add(noProxyRadio);
        proxyGroup.add(socks5Radio);

        noProxyRadio.addActionListener(this);
        socks5Radio.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // Added on evnctv 1.001
        noProxyRadio.addKeyListener(this);
        socks5Radio.addKeyListener(this);
        hostField.addKeyListener(this);
        portField.addKeyListener(this);

        // End of Row Components:
        /*c.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        gridbag.setConstraints(noProxyRadio, c);
        gridbag.setConstraints(socks5Radio, c);
        gridbag.setConstraints(hostField, c);
        gridbag.setConstraints(portField, c);
        gridbag.setConstraints(okButton, c);*/

        add(noProxyRadio);
        add(socks5Radio);
        add(new JLabel("Host:", JLabel.RIGHT));
        add(hostField);
        add(new JLabel("Port:", JLabel.RIGHT));
        add(portField);
        add(okButton);
        add(cancelButton);

        init();

        setTitle("Proxy settings");

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
        if (Setting.getProxyData().getIsProxy()) {
            socks5Radio.setSelected(true);
        } else {
            noProxyRadio.setSelected(true);
        }

        enableTypeProxy();

        hostField.setText(Setting.getProxyData().getHost());
        portField.setText(Setting.getProxyData().getPort() + "");
    }

    private void enableTypeProxy() {
        if (noProxyRadio.isSelected()) {
            hostField.setEnabled(false);
            portField.setEnabled(false);
        } else if (socks5Radio.isSelected()) {
            hostField.setEnabled(true);
            portField.setEnabled(true);
        }
    }

    private void saveSetting() {
        if (noProxyRadio.isSelected()) {
            Setting.getProxyData().setIsProxy(false);
            ProxyIO.writeFile(Setting.getProxyData());
            this.dispose();
        }
        if (socks5Radio.isSelected()) {
            if (hostField.getText().trim().equals("") || portField.getText().trim().equals("")) {
                JOptionPane.showConfirmDialog(this, "Please enter host/port number", "Error", JOptionPane.DEFAULT_OPTION);
            } else {
                try {
                    Setting.getProxyData().setHost(hostField.getText().trim());
                    Setting.getProxyData().setPort(Integer.parseInt(portField.getText().trim()));
                    Setting.getProxyData().setIsProxy(true);

                    ProxyIO.writeFile(Setting.getProxyData());

                    this.dispose();
                } catch (NumberFormatException en) {
                    JOptionPane.showConfirmDialog(this, "Invalid port number", "Error", JOptionPane.DEFAULT_OPTION);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        enableTypeProxy();

        if (e.getSource() == okButton) {
            saveSetting();
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
