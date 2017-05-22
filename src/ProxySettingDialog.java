/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * To access this dialog just going to Settings menu -> Proxy
 * You can choose how to connect to vnc server via SOCKS5 or not
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class ProxySettingDialog extends JDialog implements ActionListener{
    
    VncThumbnailViewer tnviewer;
    ProxyData proxyData;
    JTextField hostField, portField;
    JButton okButton, cancelButton;
    JRadioButton noProxyRadio, socks5Radio;
    JLabel errorLabel;
    
    public ProxySettingDialog(VncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        // GUI Stuff:
        setResizable(false);
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
        setVisible(false);
    }
    
    private void init(){
        proxyData = readFile();
        
        if(proxyData.getIsProxy())
            socks5Radio.setSelected(true);
        else
            noProxyRadio.setSelected(true);
        
        enableTypeProxy();
        
        hostField.setText(proxyData.getHost());
        portField.setText(proxyData.getPort() +"");
    }
    
    private void enableTypeProxy(){
        if(noProxyRadio.isSelected()){
            hostField.setEnabled(false);
            portField.setEnabled(false);
        }
        else if(socks5Radio.isSelected()){
            hostField.setEnabled(true);
            portField.setEnabled(true);
        }
    }
    
    private void writeFile(){
        try {
            FileOutputStream f = new FileOutputStream("proxy.b");
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(proxyData);
            o.close();
            f.close();

            System.out.println("Save proxy.. Host:"+ proxyData.getHost() +" Port:"+ proxyData.getPort() +" Enable:"+ proxyData.getIsProxy());
        } catch (IOException ex) {
        }
    }
    
    public ProxyData readFile(){
        try {
            FileInputStream f = new FileInputStream("proxy.b");
            ObjectInputStream o = new ObjectInputStream(f);
            proxyData = (ProxyData) o.readObject();
            o.close();
            f.close();
            
            System.out.println("Load proxy.. Host:"+ proxyData.getHost() +" Port:"+ proxyData.getPort() +" Enable:"+ proxyData.getIsProxy());
            
            return proxyData;
        } catch (ClassNotFoundException ex) {
            return new ProxyData();
        } catch (IOException ex) {
            return new ProxyData();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        enableTypeProxy();
        
        if (e.getSource() == okButton) {
            if(noProxyRadio.isSelected()){
                proxyData.setIsProxy(false);
                writeFile();
                this.dispose();
            }
            if(socks5Radio.isSelected()){
                if(hostField.getText().trim().equals("") || portField.getText().trim().equals(""))
                    JOptionPane.showConfirmDialog(this, "Please enter host/port number", "Error", JOptionPane.DEFAULT_OPTION);
                else{ 
                    try{
                        proxyData.setHost(hostField.getText().trim());
                        proxyData.setPort(Integer.parseInt(portField.getText().trim()));
                        proxyData.setIsProxy(true);
                        writeFile();
                        this.dispose();
                    }catch(NumberFormatException en){
                        JOptionPane.showConfirmDialog(this, "Invalid port number", "Error", JOptionPane.DEFAULT_OPTION);
                    }  
                }
            }
        }
        if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }
    
}
