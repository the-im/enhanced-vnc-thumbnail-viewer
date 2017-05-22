/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Optimized code & re-comments
 * 
 * Enhanced VNC Thumbnail Viewer 1.000
 *  To access this dialog just going to Help -> About
 *  This dialog will display version of this program 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutDialog extends JDialog implements ActionListener{
    
    private JButton okButton;
    private JLabel programNameLabel, versionLabel;
    
    public AboutDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);

        // Initial components
        programNameLabel = new JLabel(EnhancedVncThumbnailViewer.PROGRAM_NAME);
        versionLabel = new JLabel("Version "+ EnhancedVncThumbnailViewer.VERSION);
        
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        
        programNameLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
        versionLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        
        // Layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        gridbag.setConstraints(programNameLabel, c);
        c.ipady = 20;
        gridbag.setConstraints(versionLabel, c);
        c.ipady = 0;
        gridbag.setConstraints(okButton, c);

        // Add to dialog
        add(programNameLabel);
        add(versionLabel);
        add(okButton);

        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);

        setTitle("About");
        setSize(250,120);
        validate();
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            this.dispose();
        }
    }
    
}
