//
//  Copyright (C) 2011-2012 Intelligent Millionaire Co.,Ltd.  All Rights Reserved.
//  Copyright (C) 2007 David Czechowski.  All Rights Reserved.
//  Copyright (C) 2001-2004 HorizonLive.com, Inc.  All Rights Reserved.
//  Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
//  Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// EnhancedVncThumbnailViewer.java - a unique VNC viewer.  This class creates an empty frame
// into which multiple vncviewers can be added.
//

/* *
 * Original source code from VNC Thumbnail Viewer version 1.4 
 * on http://code.google.com/p/vncthumbnailviewer/source/checkout
 * --------------------------------------------------------------------
 * Abbreviation of Enhanced VNC Thumbnail Viewer is evnctv ***
 *
 * Enhanced VNC Thumbnail Viewer 1.4.0
 *  - Added theme feature
 *
 * Enhanced VNC Thumbnail Viewer 1.003
 *  - Added screen capture feature
 *  - No case-sensitive for searching
 *  - Changed message dialog
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - Added remember username option
 *  - Added 10 recent configurations list feature
 *  - Moved all settings into host list file
 *  - Fixed re-order when solo host is closed
 * 
 * Enhanced VNC Thumbnail Viewer 1.001
 *  - Added slide show feature by default delay is 4 seconds
 *  - Added loop pagination feature
 *  - Added evnctv's icon on title bar of window and application
 *  - Fixed disconnect button, make it's available since start connect 
 *  - Added Settings class for call all setting in project
 *  - Added IO class of each setting for read & write file
 * 
 * Enhanced VNC Thumbnail Viewer 1.000
 *  - Change UI from awt to swing
 *  - New classes -> LoginDialog, LoginSettingDialog, LoginData, ProxySettingDialog, AboutDialog, ProxyData, SearchList
 *  - SOCKS5 is available
 *  - Display most 4 viewers per page
 *  - Search for viewer that you want
 *  - Login on start up program
 *  - Reconnect is available
 *  - Display computer name
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class EnhancedVncThumbnailViewer extends Frame
        implements WindowListener, ComponentListener, ContainerListener, MouseListener, ActionListener, KeyListener {

    public static void main(String argv[]) {
        EnhancedVncThumbnailViewer t = new EnhancedVncThumbnailViewer();

        String h = new String("");
        String pw = new String("");
        String us = new String("");
        String compname = new String("");
        int p = 0;

        for (int i = 0; i < argv.length; i += 2) {
            if (argv.length < (i + 2)) {
                System.out.println("ERROR: No value found for parameter " + argv[i]);
                break;
            }
            String param = argv[i];
            String value = argv[i + 1];
            if (param.equalsIgnoreCase("host")) {
                h = value;
            }
            if (param.equalsIgnoreCase("port")) {
                p = Integer.parseInt(value);
            }
            if (param.equalsIgnoreCase("password")) {
                pw = value;
            }
            if (param.equalsIgnoreCase("username")) {
                us = value;
            }
            if (param.equalsIgnoreCase("encpassword")) {
                pw = AddHostDialog.readEncPassword(value);
            }
            if (param.equalsIgnoreCase("compname")) {
                compname = value;
            }

            if (i + 2 >= argv.length || argv[i + 2].equalsIgnoreCase("host")) {
                //if this is the last parameter, or if the next parameter is a next host...
                if (h != "" && p != 0) {
                    System.out.println("Command-line: host " + h + " port " + p);
                    t.launchViewer(h, p, pw, us, compname);
                    h = "";
                    p = 0;
                    pw = "";
                    us = "";
                    compname = "";
                } else {
                    System.out.println("ERROR: No port specified for last host (" + h + ")");
                }
            }
        }

    }

    public final static String VERSION = "1.4.0";
    public final static String PROGRAM_NAME = "Enhanced VNC Thumbnail Viewer";
    
    private VncViewersList viewersList, viewersSearchList;
    private MenuItem newhostMenuItem, loadhostsMenuItem, savehostsMenuItem, exitMenuItem;
    private Frame soloViewer;
    private int widthPerThumbnail, heightPerThumbnail;
    private int thumbnailRowCount;
    
    // Added on evnctv 1.000
    private LoginDialog loginDialog;
    private SearchList searchList;
    private Pagination pagination;
    private JButton nextButton, previousButton, searchButton, cancelSearchButton;
    private JPanel naviPanel, viewerPanel, naviCenterPanel, naviRightPanel;
    private MenuItem aboutMenuItem;
    private JTextField searchField;
    private boolean isSearch = false;
    
    // Added on evnctv 1.001
    private boolean isNext = true;
    private boolean isPrevious = false;
    private boolean isSlideShow = false;
    private JButton slideShowButton;
    private JPanel naviLeftPanel;
    
    // Added on evnctv 1.002
    private MenuItem recentSettingsShowMenuItem;
    private MenuItem optionsMenuItem;
    private int soloOrder;
    
    /* Added on evnctv 1.003 */
    private JPanel captureScreenPanel;
    
    public EnhancedVncThumbnailViewer() {
        PlatformUI.getLookAndFeel();
        
        thumbnailRowCount = 0;
        widthPerThumbnail = 0;
        heightPerThumbnail = 0;

        setTitle(PROGRAM_NAME + " " + VERSION);  // Modified on evnctv 1.001
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png")); // Added on evnctv 1.001
        addWindowListener(this);
        addComponentListener(this);
        addMouseListener(this);

        setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
        setMenuBar(new MenuBar());
        getMenuBar().add(createFileMenu());

        /* Added on evnctv 1.000 */
        getMenuBar().add(createToolsMenu()); // Modified on evnctv 1.002
        getMenuBar().add(createAboutMenu());

        /* Added on evnctv 1.003 */
        captureScreenPanel = new JPanel(new GridLayout());
        captureScreenPanel.setVisible(false);
        add(captureScreenPanel);
        
        /* Added on evnctv 1.000 */
        // Initial panels
        naviPanel = new JPanel(new GridLayout(1, 2));
        viewerPanel = new JPanel(new GridLayout(2, 2));
        naviPanel.setBackground(Color.gray);
        viewerPanel.setBackground(Color.decode(ThemeSetting.get("main.viewer.background-color")));
        add(naviPanel, BorderLayout.NORTH);
        add(viewerPanel, BorderLayout.CENTER);

        // Initial buttons
        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");
        searchField = new JTextField("", 15);
        searchButton = new JButton("Search");
        cancelSearchButton = new JButton("Cancel");
        slideShowButton = new JButton("Play slide");

        nextButton.setBackground(Color.gray);
        previousButton.setBackground(Color.gray);
        searchButton.setBackground(Color.gray);
        cancelSearchButton.setBackground(Color.gray);
        slideShowButton.setBackground(Color.gray);

        nextButton.setPreferredSize(new Dimension(80, 30));
        previousButton.setPreferredSize(new Dimension(80, 30));
        slideShowButton.setPreferredSize(new Dimension(80, 30));

        nextButton.setEnabled(false);
        previousButton.setEnabled(false);
        searchField.setEnabled(false);
        searchButton.setEnabled(false);
        cancelSearchButton.setEnabled(false);
        slideShowButton.setEnabled(false);

        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        searchField.addKeyListener(this);
        searchButton.addActionListener(this);
        cancelSearchButton.addActionListener(this);
        slideShowButton.addActionListener(this);

        // Initial navigator panels
        naviLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        naviCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        naviRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        naviLeftPanel.setBackground(Color.gray);
        naviCenterPanel.setBackground(Color.gray);
        naviRightPanel.setBackground(Color.gray);

        // Add components to navigator panel
        naviLeftPanel.add(slideShowButton);

        naviCenterPanel.add(previousButton);
        naviCenterPanel.add(nextButton);

        naviRightPanel.add(searchField);
        naviRightPanel.add(searchButton);
        naviRightPanel.add(cancelSearchButton);

        naviPanel.add(naviLeftPanel);
        naviPanel.add(naviCenterPanel);
        naviPanel.add(naviRightPanel);
        
        
        viewersList = new VncViewersList(this);
        pagination = new Pagination(viewersList);
        searchList = new SearchList(this);

        setVisible(true);

        soloViewer = new Frame();
        soloViewer.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png")); // Added on evnctv 1.001
        soloViewer.setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
        soloViewer.setBackground(Color.decode(ThemeSetting.get("main.viewer.background-color")));
        soloViewer.addWindowListener(this);
        soloViewer.addComponentListener(this);
        soloViewer.validate();
        
        /* Added on evnctv 1.003 - Screen capture */
        ScreenCapture c = new ScreenCapture(this, viewersList);
        Thread captureScreen = new Thread(c);
        captureScreen.start();
        
        /* Added on evnctv 1.001 - To support slideshow */
        while (true) {
            if (pagination.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {}
            } else {
                try {
                    Thread.sleep(SlideshowSetting.getDelay() * 1000);
                } catch (InterruptedException ex) {}

                if (!pagination.isEmpty() && isSlideShow) {
                    if (isPrevious) {
                        showPreviousNextPage(pagination.previous());
                    } else {
                        showPreviousNextPage(pagination.next());
                    }
                }
            }
        }

    }

    public void launchViewer(String host, int port, String password, String user, String compname) {
        launchViewer(host, port, password, user, "", compname);
    }

    public void launchViewer(String host, int port, String password, String user, String userdomain, String compname) {
        VncViewer v = viewersList.launchViewer(host, port, password, user, userdomain, compname);
    }
    
    /* *
     * Modified on evnctv 1.000, 1.002
     */
    void addViewer(VncViewer v) {
        addViewerToPanel(v, -1);
    }

    void addViewer(VncViewer v, int order) {
        addViewerToPanel(v, order);
    }
    
    /* *
     * Added on evnc 1.002
     *  - Reduced duplicate codes in addViewer() method
     */
    private void addViewerToPanel(VncViewer v, int order) {
        // Initial var r to choose a size for viewer 
        int r;
        if (pagination.isLimited()) {
            r = (int) Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
        } else {
            if (isSearch) {
                r = (int) Math.sqrt(viewersSearchList.size() - 1) + 1;
            } else {
                r = (int) Math.sqrt(viewersList.size() - 1) + 1;
            }
        }

        if (r != thumbnailRowCount) {
            thumbnailRowCount = r;
            ((GridLayout) viewerPanel.getLayout()).setRows(thumbnailRowCount);
            resizeThumbnails();
        }

        if (viewerPanel.getComponentCount() < Pagination.thumbsnailPerPage) {
            // -1 for no order
            if (order == -1) {
                viewerPanel.add(v);
            } else {
                viewerPanel.add(v, order);
            }
            validate();
        } else {
            /* Modified on evnctv 1.003 */
            v.setVisible(false);
            //viewerPanel.add(v);
            v.disconnect();
        }

        enableNaviButton();
    }
    
    /* Added on evnctv 1.003 - To support capture screen feature */
    void addViewerScreenCapture(VncViewer v) {
        captureScreenPanel.removeAll();
        captureScreenPanel.add(v);
    }

    /* Modified on evnctv 1.000 */
    void removeViewer(VncViewer v) {
        viewersList.remove(v);
        viewerPanel.remove(v);
        v.disconnect();
        validate();

        int r;
        if (pagination.isLimited()) {
            r = (int) Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
        } else {
            if (isSearch) {
                r = (int) Math.sqrt(viewersSearchList.size() - 1) + 1;
            } else {
                r = (int) Math.sqrt(viewersList.size() - 1) + 1;
            }
        }

        if (r != thumbnailRowCount) {
            thumbnailRowCount = r;
            ((GridLayout) viewerPanel.getLayout()).setRows(thumbnailRowCount);
            resizeThumbnails();
        }
    }

    void soloHost(VncViewer v) {
        if (v.vc == null) {
            return;
        }

        if (soloViewer.getComponentCount() > 0) {
            soloHostClose();
        }

        soloViewer.setVisible(true);
        soloViewer.setTitle(v.compname + " (" + v.host + ":" + v.port + ")"); // Modified on evnctv 1.000
        
        viewerPanel.remove(v); // Modified on evnctv 1.000
        soloViewer.add(v);
        v.vc.removeMouseListener(this);
        this.validate();
        soloViewer.validate();

        if (!v.rfb.closed()) {
            v.vc.enableInput(true);
        }
        updateCanvasScaling(v, getWidthNoInsets(soloViewer), getHeightNoInsets(soloViewer));
    }

    void soloHostClose() {
        VncViewer v = (VncViewer) soloViewer.getComponent(0);
        v.enableInput(false);
        updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
        soloViewer.removeAll();
        addViewer(v, soloOrder);
        v.vc.addMouseListener(this);
        soloViewer.setVisible(false);
    }

    private void updateCanvasScaling(VncViewer v, int maxWidth, int maxHeight) {
        maxHeight -= v.buttonPanel.getHeight();
        int fbWidth = v.vc.rfb.framebufferWidth;
        int fbHeight = v.vc.rfb.framebufferHeight;
        int f1 = maxWidth * 100 / fbWidth;
        int f2 = maxHeight * 100 / fbHeight;
        int sf = Math.min(f1, f2);
        if (sf > 100) {
            sf = 100;
        }

        v.vc.maxWidth = maxWidth;
        v.vc.maxHeight = maxHeight;
        v.vc.scalingFactor = sf;
        //v.vc.scaledWidth = (fbWidth * sf + 50) / 100;
        //v.vc.scaledHeight = (fbHeight * sf + 50) / 100;

        // Modified on evnctv 1.000
        v.vc.scaledWidth = (fbWidth * sf + 50) / 100 - 20;
        v.vc.scaledHeight = (fbHeight * sf + 50) / 100 - 20;

        //Fix: invoke a re-paint of canvas?
        //Fix: invoke a re-size of canvas?
        //Fix: invoke a validate of viewer's gridbag?
    }

    /* Modified on evnctv 1.000 */
    void resizeThumbnails() {
        int newWidth = getWidthNoInsets(this) / thumbnailRowCount;
        int newHeight = getHeightNoInsets(this) / thumbnailRowCount;


        if (newWidth != widthPerThumbnail || newHeight != heightPerThumbnail) {
            widthPerThumbnail = newWidth;
            heightPerThumbnail = newHeight;

            ListIterator l;
            if (isSearch) {
                l = viewersSearchList.listIterator();
            } else {
                l = viewersList.listIterator();
            }

            while (l.hasNext()) {
                VncViewer v = (VncViewer) l.next();
                //v.
                if (!soloViewer.isAncestorOf(v)) {
                    if (v.vc != null) { // if the connection has been established
                        updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
                    }
                }
            }
        }

    }

    private void loadsaveHosts(int mode) {
        FileDialog fd = new FileDialog(this, "Load file...", mode);
        if (mode == FileDialog.SAVE) {
            fd.setTitle("Save file...");
        }
        fd.show();

        String file = fd.getFile();
        if (file != null) {
            String dir = fd.getDirectory();

            if (mode == FileDialog.SAVE) {
                //ask about encrypting
                HostsFilePasswordDialog pd = new HostsFilePasswordDialog(this, true);
                if (pd.getResult()) {
                    FileManager.saveEncryptedFile(dir + file, pd.getPassword(), viewersList);
                } else {
                    FileManager.saveFile(dir + file, viewersList);
                }
            } else {
                if (FileManager.isHostsFileEncrypted(dir + file)) {
                    HostsFilePasswordDialog pd = new HostsFilePasswordDialog(this, false);
                    FileManager.loadFile(dir + file, pd.getPassword(), this);
                } else {
                    FileManager.loadFile(dir + file, "", this);
                }
            }
        }
    }

    public void quit() {
        // Called by either File->Exit or Closing of the main window
        System.out.println("Closing window");
        ListIterator l = viewersList.listIterator();
        while (l.hasNext()) {
            ((VncViewer) l.next()).disconnect();
        }
        this.dispose();

        System.exit(0);
    }

    static private int getWidthNoInsets(Frame frame) {
        Insets insets = frame.getInsets();
        int width = frame.getWidth() - (insets.left + insets.right);
        return width;
    }

    static private int getHeightNoInsets(Frame frame) {
        Insets insets = frame.getInsets();
        int height = frame.getHeight() - (insets.top + insets.bottom);
        return height;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");
        newhostMenuItem = new MenuItem("Add New Host");
        loadhostsMenuItem = new MenuItem("Open..."); // Modified on evnctv 1.002
        savehostsMenuItem = new MenuItem("Save..."); // Modified on evnctv 1.002
        exitMenuItem = new MenuItem("Exit");

        newhostMenuItem.addActionListener(this);
        loadhostsMenuItem.addActionListener(this);
        savehostsMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        fileMenu.add(newhostMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(loadhostsMenuItem);
        fileMenu.add(savehostsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        loadhostsMenuItem.enable(true);
        savehostsMenuItem.enable(true);

        return fileMenu;
    }

    /* *
     * Modified on evnctv 1.002
     * Added on evnctv 1.000
     */
    private Menu createToolsMenu() {
        Menu toolsMenu = new Menu("Tools");

        recentSettingsShowMenuItem = new MenuItem("Recent Settings");
        optionsMenuItem = new MenuItem("Options");

        recentSettingsShowMenuItem.addActionListener(this);
        optionsMenuItem.addActionListener(this);

        toolsMenu.add(recentSettingsShowMenuItem);
        toolsMenu.addSeparator();
        toolsMenu.add(optionsMenuItem);

        return toolsMenu;
    }

    private Menu createAboutMenu() {
        Menu helpMenu = new Menu("Help");
        aboutMenuItem = new MenuItem("About");
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);
        return helpMenu;
    }

    // Window Listener Events:
    public void windowClosing(WindowEvent evt) {
        if (soloViewer.isShowing()) {
            soloHostClose();
        }

        if (evt.getComponent() == this) {
            quit();
        }

    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowDeactivated(WindowEvent evt) {
    }

    public void windowOpened(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    // Component Listener Events:
    public void componentResized(ComponentEvent evt) {
        if (evt.getComponent() == this) {
            if (thumbnailRowCount > 0) {
                resizeThumbnails();
            }
        } else { // resize soloViewer
            VncViewer v = (VncViewer) soloViewer.getComponent(0);
            updateCanvasScaling(v, getWidthNoInsets(soloViewer), getHeightNoInsets(soloViewer));
        }

    }

    public void componentHidden(ComponentEvent evt) {
    }

    public void componentMoved(ComponentEvent evt) {
    }

    public void componentShown(ComponentEvent evt) {
    }

    // Mouse Listener Events:
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            Component c = evt.getComponent();
            if (c instanceof VncCanvas) {
                /* *
                 * Added on evnctv1.002
                 *  - To get order of viewer
                 */
                VncViewer v = ((VncCanvas) c).viewer;
                soloOrder = viewerPanel.getComponentZOrder(v);
        
                soloHost(v);
            }
        }

    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
    }

    public void mouseReleased(MouseEvent evt) {
    }

    // Container Listener Events:
    public void componentAdded(ContainerEvent evt) {
        // This detects when a vncviewer adds a vnccanvas to it's container
        if (evt.getChild() instanceof VncCanvas) {
            VncViewer v = (VncViewer) evt.getContainer();
            v.vc.addMouseListener(this);
            v.buttonPanel.addContainerListener(this);
            //v.buttonPanel.disconnectButton.addActionListener(this); // Cancelled on evnctv 1.001
            updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
        } // Cancelled on evnctv 1.001
        // This detects when a vncviewer's Disconnect button had been pushed
        /*else if (evt.getChild() instanceof Button) {
        Button b = (Button) evt.getChild();
        if (b.getLabel() == "Reconnect" || b.getLabel().equals("Disconnect")) {
        b.addActionListener(this);
        }
        
        // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
        if (b.getLabel() == "Remove") {
        b.addActionListener(this);
        }
        }*/ // Added on evnctv 1.001
        else if (evt.getChild() instanceof Panel) {
            try {
                Panel p = (Panel) evt.getChild();
                Button b = (Button) p.getComponent(0);
                if (b.getLabel().equals("Disconnect")) {
                    b.addActionListener(this);
                }
            } catch (Exception e) {
            }
        }
    }

    public void componentRemoved(ContainerEvent evt) {
    }

    // Action Listener Event:
    public void actionPerformed(ActionEvent evt) {
        // Cancelled on evnctv 1.0 
        /*if( evt.getSource() instanceof Button && ((Button)evt.getSource()).getLabel() == "Hide desktop") {
        VncViewer v = (VncViewer)((Component)((Component)evt.getSource()).getParent()).getParent();
        this.remove(v);
        viewersList.remove(v);
        }*/

        // Modified on evnctv 1.000, 1.001
        if (evt.getSource() instanceof Button) {
            Button b = (Button) evt.getSource();
            VncViewer v = (VncViewer) ((Component) ((Component) evt.getSource()).getParent()).getParent();

            if (b.getLabel().equals("Reconnect")) {
                int order = viewerPanel.getComponentZOrder(v);
                viewerPanel.remove(v);
                viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname, order);
            } else if (b.getLabel().equals("Disconnect")) {
                v.disconnect();
            }
        }

        if (evt.getSource() == newhostMenuItem) {
            new AddHostDialog(this);
        }
        if (evt.getSource() == savehostsMenuItem) {
            loadsaveHosts(FileDialog.SAVE);
        }
        if (evt.getSource() == loadhostsMenuItem) {
            loadsaveHosts(FileDialog.LOAD);
            
            if (LoginSetting.getIsEnable()) {
                new LoginDialog(this);
            }
        }
        if (evt.getSource() == exitMenuItem) {
            quit();
        }


        // Added on evnctv 1.000
    /*if(evt.getSource() instanceof Button && ((Button)evt.getSource()).getLabel() == "Remove") {
        VncViewer v = (VncViewer)((Component)((Component)evt.getSource()).getParent()).getParent();
        removeViewer(v);
        }*/
        if (evt.getSource() == aboutMenuItem) {
            new AboutDialog(this);
        }
        if (evt.getSource() == cancelSearchButton) {
            isSearch = false;
            searchField.setText("");
            pagination = new Pagination(viewersList);

            clearViewersOnPage();

            VncViewer v;
            int size = viewersList.size();
            for (int i = 0; i < size; i++) {
                v = (VncViewer) viewersList.get(i);
                viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
            }

            enableNaviButton();
        }
        // Modified on evnctv 1.001
        if (evt.getSource() == searchButton) {
            searchViewer();
        }
        if (evt.getSource() == previousButton) {
            isPrevious = true;
            isNext = false;
            showPreviousNextPage(pagination.previous());
        }
        if (evt.getSource() == nextButton) {
            isPrevious = false;
            isNext = true;
            showPreviousNextPage(pagination.next());
        }
        
        // Added on evnctv 1.001
        if (evt.getSource() == slideShowButton) {
            if (isSlideShow) {
                isSlideShow = false;
                slideShowButton.setText("Play slide");
            } else {
                isSlideShow = true;
                slideShowButton.setText("Stop slide");
            }

            enableNaviButton();
        }
        
        // Added on evnctv 1.002
        if (evt.getSource() == recentSettingsShowMenuItem) {
            new RecentSettingsShowDialog(this);
        }
        if (evt.getSource() == optionsMenuItem) {
            new OptionsDialog(this);
        }
        
    }

    /* Added on evnctv 1.001 */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(searchField) && KeyEvent.VK_ENTER == e.getKeyCode()) {
            searchViewer();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    //
    // Added on evnctv 1.000
    //  - Enable/disable button on navigator panel
    //
    private void enableNaviButton() {
        /**if (pagination.hasNext()) {
        nextButton.setEnabled(true);
        } else {
        nextButton.setEnabled(false);
        }
        
        if (pagination.hasPrevious()) {
        previousButton.setEnabled(true);
        } else {
        previousButton.setEnabled(false);
        }*/
        if (pagination.isLimited()) {
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
            slideShowButton.setEnabled(true);
        }

        if (!pagination.isLimited()) {
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
            slideShowButton.setEnabled(false);
        }

        // Modified on evnctv 1.001
        if (isSearch) {
            if (isSlideShow) {
                searchField.setEnabled(false);
                searchButton.setEnabled(false);
                cancelSearchButton.setEnabled(false);
            } else {
                searchField.setEnabled(true);
                searchButton.setEnabled(true);
                cancelSearchButton.setEnabled(true);
            }
        } else {
            if (isSlideShow) {
                searchField.setEnabled(false);
                searchButton.setEnabled(false);
                cancelSearchButton.setEnabled(false);
            } else {
                searchField.setEnabled(true);
                searchButton.setEnabled(true);
                cancelSearchButton.setEnabled(false);
            }
        }

    }

    //
    // Added on evnctv 1.000
    //  - Disconnect and remove all viewers on each page
    //
    private void clearViewersOnPage() {
        VncViewer v;
        for (int i = 0; i < viewerPanel.getComponentCount(); i++) {
            v = (VncViewer) viewerPanel.getComponent(i);
            v.disconnect();
        }
        System.out.println("TOTAL: "+viewerPanel.getComponentCount());
        viewerPanel.removeAll();
    }

    /* *
     * Added on evnctv 1.001
     *  - To show viewers when next/previous page is clicked
     */
    private void showPreviousNextPage(Vector viewers) {
        clearViewersOnPage();

        VncViewer v;
        int size = viewers.size();

        for (int i = 0; i < size; i++) {
            v = (VncViewer) viewers.get(i);
            viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
        }

        enableNaviButton();
    }

    /* *
     * Added on evnctv 1.001
     */
    private void searchViewer() {
        isSearch = true;
        viewersSearchList = searchList.searchViewer(viewersList, searchField.getText().trim());
        pagination = new Pagination(viewersSearchList);

        clearViewersOnPage();

        VncViewer v;
        int size = viewersSearchList.size();
        for (int i = 0; i < size; i++) {
            v = (VncViewer) viewersSearchList.get(i);
            viewersSearchList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
        }

        enableNaviButton();
    }
    
    public void setGuiTheme() {
        viewerPanel.setBackground(Color.decode(ThemeSetting.get("main.viewer.background-color")));
        soloViewer.setBackground(Color.decode(ThemeSetting.get("main.viewer.background-color")));
    }
    
    public VncViewersList getViewerList() {
        return viewersList;
    }
    
}