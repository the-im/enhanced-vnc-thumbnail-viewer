/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * To access this dialog just going to Help -> About
 * This dialog will display version of this program 
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class AboutDialog extends JDialog implements ActionListener{
    
    EnhancedVncThumbnailViewer tnviewer;
    JButton okButton;
    JLabel programNameLabel, versionLabel;
    
    public AboutDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);
        this.tnviewer = tnviewer;

        // GUI Stuff:
        setResizable(false);
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        programNameLabel = new JLabel(EnhancedVncThumbnailViewer.PROGRAM_NAME);
        versionLabel = new JLabel("Version "+ EnhancedVncThumbnailViewer.VERSION);
        okButton = new JButton("OK");
        
        programNameLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
        versionLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        
        ButtonGroup proxyGroup = new ButtonGroup();
        proxyGroup.add(okButton);

        okButton.addActionListener(this);

        c.gridwidth = GridBagConstraints.REMAINDER;

        gridbag.setConstraints(programNameLabel, c);
        c.ipady = 20;
        gridbag.setConstraints(versionLabel, c);
        c.ipady = 0;
        gridbag.setConstraints(okButton, c);

        add(programNameLabel);
        add(versionLabel);
        add(okButton);

        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("About");
        setSize(250,120);
        validate();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            this.dispose();
        }
    }
    
}
