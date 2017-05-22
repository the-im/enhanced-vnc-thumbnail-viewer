/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Added recent settings logs
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - Moved readFile(), writeFile() method to ProxyIO class
 *  - Added KeyListener
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 *  - To access this dialog just going to Settings menu -> Proxy
 *  - You can choose how to connect to vnc server via SOCKS5 or not
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProxySettingDialog extends JDialog implements ActionListener, KeyListener {

    private JTextField serverField, portField;
    private JButton okButton, cancelButton;
    private JRadioButton noProxyRadio, socks5Radio;

    public ProxySettingDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);

        // Initial components
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        noProxyRadio = new JRadioButton("No proxy");
        socks5Radio = new JRadioButton("Use SOCKS5");
        serverField = new JTextField("", 10);
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
        serverField.addKeyListener(this);
        portField.addKeyListener(this);

        // Layout
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        
        /*GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        gridbag.setConstraints(noProxyRadio, c);
        gridbag.setConstraints(socks5Radio, c);
        gridbag.setConstraints(hostField, c);
        gridbag.setConstraints(portField, c);
        gridbag.setConstraints(okButton, c);*/

        // Add to dialog
        add(noProxyRadio);
        add(socks5Radio);
        add(new JLabel("Server:", JLabel.RIGHT));
        add(serverField);
        add(new JLabel("Port:", JLabel.RIGHT));
        add(portField);
        add(okButton);
        add(cancelButton);

        init();

        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("Proxy Settings");
        pack();
        validate();
        setResizable(false);
        setVisible(true);
    }

    private void init() {
        if (ProxySetting.getIsEnable()) {
            socks5Radio.setSelected(true);
        } else {
            noProxyRadio.setSelected(true);
        }

        enableTypeProxy();

        serverField.setText(ProxySetting.getServer());
        portField.setText(ProxySetting.getPort() + "");
    }

    private void enableTypeProxy() {
        if (noProxyRadio.isSelected()) {
            serverField.setEnabled(false);
            portField.setEnabled(false);
        } else if (socks5Radio.isSelected()) {
            serverField.setEnabled(true);
            portField.setEnabled(true);
        }
    }

    private void saveSetting() {
        if (noProxyRadio.isSelected()) {
            /* *
             * Added on evnctv 1.002
             * To save recent settings
             */
            // Change to no proxy
            if (ProxySetting.getIsEnable()) {
                String msg = "Changed to use no proxy";
                RecentSettingsList.addRecent(new RecentSetting(msg, "Proxy"));
                System.out.println(msg);
            }
            
            
            ProxySetting.setIsEnable(false);
            this.dispose();
        } else if (socks5Radio.isSelected()) {
            if (serverField.getText().trim().equals("") || portField.getText().trim().equals("")) {
                JOptionPane.showConfirmDialog(this, "Please enter server and/or port number", "Error", JOptionPane.DEFAULT_OPTION);
            } else {
                try {
                    /* *
                     * Added on evnctv 1.002
                     * To save recent settings
                     */
                    String server = serverField.getText().trim();
                    int port = Integer.parseInt(portField.getText().trim());
                    
                    // Change to proxy
                    if (!ProxySetting.getIsEnable()) {
                        String msg = "";
                        
                        // Change server & port
                        if (!ProxySetting.getServer().equalsIgnoreCase(server) || ProxySetting.getPort() != port) {
                            msg = "Changed to use SOCKS5 & changed from " + ProxySetting.getServer() + ":" + ProxySetting.getPort() + " to " + server + ":" + port;
                        } else {
                            msg = "Changed to use SOCKS5";
                        }
                        
                        RecentSettingsList.addRecent(new RecentSetting(msg, "Proxy"));
                        System.out.println(msg);
                    } else {
                        if (!ProxySetting.getServer().equalsIgnoreCase(server) || ProxySetting.getPort() != port) {
                            String msg = "Changed SOCKS5 from " + ProxySetting.getServer() + ":" + ProxySetting.getPort() + " to " + server + ":" + port;
                            RecentSettingsList.addRecent(new RecentSetting(msg, "Proxy"));
                            System.out.println(msg);
                        }
                    }

                    
                    ProxySetting.setServer(server);
                    ProxySetting.setPort(port);
                    ProxySetting.setIsEnable(true);

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