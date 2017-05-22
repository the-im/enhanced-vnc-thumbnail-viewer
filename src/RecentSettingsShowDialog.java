/* *
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Dialog to show recent settings
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.table.*;

public class RecentSettingsShowDialog extends JDialog implements ActionListener {

    private JButton closeButton;

    public RecentSettingsShowDialog(EnhancedVncThumbnailViewer tnviewer) {
        super(tnviewer, true);

        // Initial rows & columns
        String[] columns = {"Title", "Setting type", "Modified on"};
        Object[][] rows = new Object[RecentSettingsList.getTotalRecents().size()][columns.length];

        DefaultTableModel dtm = new DefaultTableModel(rows, columns) {
            boolean[] canEdit = {false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        
        // Table
        JTable table = new JTable(dtm);
        table.setPreferredSize(new Dimension(550, 160));
        table.setAutoCreateRowSorter(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175)));
        
        // Table header
        JTableHeader th = table.getTableHeader();
        th.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175)));
        
        // Table columns
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(340);
        tcm.getColumn(1).setPreferredWidth(80);
        tcm.getColumn(2).setPreferredWidth(130);
        
        // Assign value to row
        int count = 0;
        RecentSetting rs;
        TableModel tm = table.getModel();
        Enumeration enm = RecentSettingsList.getTotalRecents().elements();
        while (enm.hasMoreElements()) {
            rs = (RecentSetting) enm.nextElement();
            tm.setValueAt("  " + rs.getTitle(), count, 0);
            tm.setValueAt("  " + rs.getType(), count, 1);
            tm.setValueAt("  " + rs.getDate() + " " + rs.getTime(), count, 2);
            
            count++;
        }
        
        // Close button
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);

        // Layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        gridbag.setConstraints(table.getTableHeader(), c);
        gridbag.setConstraints(table, c);
        gridbag.setConstraints(closeButton, c);

        // Add to dialog
        add(th);
        add(table);
        add(closeButton);

        // Dialog
        Point loc = tnviewer.getLocation();
        Dimension dim = tnviewer.getSize();
        loc.x += (dim.width / 2) - 50;
        loc.y += (dim.height / 2) - 50;
        setLocation(loc);
        
        setTitle("Show Recent Settings");
        setSize(600, 280);
        validate();
        setResizable(false);
        setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            this.dispose();
        }
    }
}
