//
//  Copyright (C) 2011 Intelligent Millionaire Co.,Ltd.  All Rights Reserved.
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
// VncThumbnailViewer.java - a unique VNC viewer.  This class creates an empty frame
// into which multiple vncviewers can be added.
//

/*
 * Original source code: VNC Thumbnail Viewer version 1.4 from http://code.google.com/p/vncthumbnailviewer/source/checkout
 * ----------------------------------------------
 * Enhanced VNC Thumbnail Viewer 1.0
 *      - Change UI from awt to swing
 *      - New classes -> LoginDialog, LoginSettingDialog, LoginData, ProxySettingDialog, AboutDialog, ProxyData, SearchList
 *      - SOCKS5 is available
 *      - Display most 4 viewers per page
 *      - Search for viewer that you want
 *      - Login on start up program
 *      - Reconnect is available
 *      - Display computer name
 */


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.lang.Math.*;
import java.net.*;
import javax.swing.*;

public class VncThumbnailViewer extends Frame
    implements WindowListener, ComponentListener, ContainerListener, MouseListener, ActionListener  {

  public static void main(String argv[])
  {
    VncThumbnailViewer t = new VncThumbnailViewer();

    String h = new String("");
    String pw = new String("");
    String us = new String("");
    String compname = new String("");
    int p = 0;

    for(int i = 0; i < argv.length; i += 2) {
      if(argv.length < (i+2) ) {
        System.out.println("ERROR: No value found for parameter " + argv[i]);
        break;
      }
      String param = argv[i];
      String value = argv[i+1];
      if(param.equalsIgnoreCase("host")) {
        h = value;
      }
      if(param.equalsIgnoreCase("port")) {
        p = Integer.parseInt(value);
      }
      if(param.equalsIgnoreCase("password")) {
        pw = value;
      }
      if(param.equalsIgnoreCase("username")) {
        us = value;
      }
      if(param.equalsIgnoreCase("encpassword")) {
        pw = AddHostDialog.readEncPassword(value);
      }
      if(param.equalsIgnoreCase("compname")) {
        compname = value;
      }
      
      if(i+2 >= argv.length || argv[i+2].equalsIgnoreCase("host")) {
        //if this is the last parameter, or if the next parameter is a next host...
        if(h != "" && p != 0) {
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
  //
  // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
  // Change version display from Float to String because it's available to show sub-version such as 1.0.1
  //
  final static String VERSION = "1.0";
  final static String PROGRAM_NAME = "Enhanced VNC Thumbnail Viewer";
  
  VncViewersList viewersList, viewersSearchList;
  AddHostDialog hostDialog;
  MenuItem newhostMenuItem, loadhostsMenuItem, savehostsMenuItem, exitMenuItem;
  Frame soloViewer;
  int widthPerThumbnail, heightPerThumbnail;
  int thumbnailRowCount;
  
  // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
  LoginDialog loginDialog;
  ProxySettingDialog proxySettingDialog;
  ProxyData proxyData;
  SearchList searchList;
  Pagination pagination;
  JButton nextButton, previousButton, searchButton, cancelSearchButton;
  JPanel naviPanel, viewerPanel, naviLeftPanel, naviRightPanel;
  MenuItem proxyMenuItem, loginMenuItem, aboutMenuItem;
  JTextField searchField;
  boolean isSearch;
  

  VncThumbnailViewer() {
    PlatformUI.getLookAndFeel();
      
    //viewersList = new VncViewersList(this);
    thumbnailRowCount = 0;
    widthPerThumbnail = 0;
    heightPerThumbnail = 0;

    setTitle(PROGRAM_NAME);
    addWindowListener(this);
    addComponentListener(this);
    addMouseListener(this);

    //GridLayout grid = new GridLayout();
    //setLayout(grid);
    setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
    setMenuBar(new MenuBar());
    getMenuBar().add( createFileMenu() );
    
    // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
    getMenuBar().add(createSettingsMenu());
    getMenuBar().add(createAboutMenu());
    
    naviPanel = new JPanel(new GridLayout(1,2));
    viewerPanel = new JPanel(new GridLayout(2,2));
    naviPanel.setBackground(Color.gray);
    viewerPanel.setBackground(Color.white);
    add(naviPanel, BorderLayout.NORTH);
    add(viewerPanel, BorderLayout.CENTER);

    nextButton = new JButton("Next");
    previousButton = new JButton("Previous");
    searchField = new JTextField("", 15);
    searchButton = new JButton("Search");
    cancelSearchButton = new JButton("Cancel");
    
    nextButton.setBackground(Color.gray);
    previousButton.setBackground(Color.gray);
    searchButton.setBackground(Color.gray);
    cancelSearchButton.setBackground(Color.gray);
    
    nextButton.setPreferredSize(new Dimension(80,30));
    previousButton.setPreferredSize(new Dimension(80,30));
    
    nextButton.setEnabled(false);
    previousButton.setEnabled(false);
    searchField.setEnabled(false);
    searchButton.setEnabled(false);
    cancelSearchButton.setEnabled(false);

    nextButton.addActionListener(this);
    previousButton.addActionListener(this);
    searchButton.addActionListener(this);
    cancelSearchButton.addActionListener(this);
    
    naviLeftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    naviRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    naviLeftPanel.setBackground(Color.gray);
    naviRightPanel.setBackground(Color.gray);
    naviLeftPanel.add(previousButton);
    naviLeftPanel.add(nextButton);
    naviRightPanel.add(searchField);
    naviRightPanel.add(searchButton);
    naviRightPanel.add(cancelSearchButton);
 
    naviPanel.add(naviLeftPanel);
    naviPanel.add(naviRightPanel);
    
    proxySettingDialog = new ProxySettingDialog(this);
    proxyData = proxySettingDialog.readFile();
    viewersList = new VncViewersList(this, proxyData);
    searchList = new SearchList(this);
    pagination = new Pagination(viewersList);
    
    setVisible(true);
    
    // Must login on start up?
    LoginSettingDialog loginSettingDialog = new LoginSettingDialog(this);
    LoginData loginData = loginSettingDialog.readFile();
    if(loginData.getIsAuth()){
        new LoginDialog(this);
    }

    
    soloViewer = new Frame();
    soloViewer.setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
    soloViewer.addWindowListener(this);
    soloViewer.addComponentListener(this);
    soloViewer.validate();
  }


  public void launchViewer(String host, int port, String password, String user, String compname) {
    launchViewer(host, port, password, user, "", compname);
  }

  public void launchViewer(String host, int port, String password, String user, String userdomain, String compname) {
    VncViewer v = viewersList.launchViewer(host, port, password, user, userdomain, compname);
    //addViewer(v); called by viewersList.launchViewer
  }

  /*void addViewer(VncViewer v) {
    int r = (int)Math.sqrt(viewersList.size() - 1) + 1;//int r = (int)Math.sqrt(this.getComponentCount() - 1) + 1;
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)this.getLayout()).setRows(thumbnailRowCount);
//      ((GridLayout)this.getLayout()).setColumns(thumbnailRowCount);
      resizeThumbnails();
    }
    add(v);
    validate();
  }*/
  

  //
  // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
  //
  void addViewer(VncViewer v) {
    // Initial var r to choose a size for viewer 
    int r;
    if(pagination.isLimited()){
        r = (int)Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
    }else{
        if(isSearch){
            r = (int)Math.sqrt(viewersSearchList.size() - 1) + 1;
        }else{
            r = (int)Math.sqrt(viewersList.size() - 1) + 1;
        }
    }
    
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)viewerPanel.getLayout()).setRows(thumbnailRowCount);
      resizeThumbnails();
    }
    
    if(viewerPanel.getComponentCount() < Pagination.thumbsnailPerPage){
        viewerPanel.add(v);
        validate();
    }
    else{
        //v.disconnect();
    }
    
    
    enableNaviButton();
  }
  
  void addViewer(VncViewer v, int index) {
    // Initial var r to choose a size for viewer 
    int r;
    if(pagination.isLimited()){
        r = (int)Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
    }else{
        if(isSearch){
            r = (int)Math.sqrt(viewersSearchList.size() - 1) + 1;
        }else{
            r = (int)Math.sqrt(viewersList.size() - 1) + 1;
        }
    }
    
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)viewerPanel.getLayout()).setRows(thumbnailRowCount);
      resizeThumbnails();
    }

    if(viewerPanel.getComponentCount() < Pagination.thumbsnailPerPage){
        viewerPanel.add(v, index);
        validate();
    }
    else{
        //v.disconnect();
    }
    
    
    enableNaviButton();
  }
  
  //
  // New method ***
  // When reconnect or next/previous page, this method will be called
  //
  /*void addViewerReconnect(VncViewer v) {
    // Initial var r to choose a size for viewer 
    int r;
    if(pagination.isLimited()){
        r = (int)Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
    }else{
        if(isSearch){
            r = (int)Math.sqrt(viewersListTmp.size() - 1) + 1;
        }else{
            r = (int)Math.sqrt(viewersList.size() - 1) + 1;
        }
    }
    
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)viewerPanel.getLayout()).setRows(thumbnailRowCount);
      resizeThumbnails();
    }
    
    viewerPanel.add(v);
    validate();
    
    enableNPButton();
  }*/

  

  /*void removeViewer(VncViewer v) {
    viewersList.remove(v);
    viewerPanel.remove(v);
    validate();

    int r = (int)Math.sqrt(viewersList.size() - 1) + 1;//int r = (int)Math.sqrt(this.getComponentCount() - 1) + 1;
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)this.getLayout()).setRows(thumbnailRowCount);
//      ((GridLayout)this.getLayout()).setColumns(thumbnailRowCount);
      resizeThumbnails();
    }
  }*/
  
  
  //
  // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
  //
  void removeViewer(VncViewer v) {
    viewersList.remove(v);
    viewerPanel.remove(v);
    v.disconnect();
    validate();

    int r;
    if(pagination.isLimited()){
        r = (int)Math.sqrt(Pagination.thumbsnailPerPage - 1) + 1;
    }else{
        if(isSearch){
            r = (int)Math.sqrt(viewersSearchList.size() - 1) + 1;
        }else{
            r = (int)Math.sqrt(viewersList.size() - 1) + 1;
        }
    }
    
    if(r != thumbnailRowCount) {
      thumbnailRowCount = r;
      ((GridLayout)viewerPanel.getLayout()).setRows(thumbnailRowCount);
      resizeThumbnails();
    }
  }


  void soloHost(VncViewer v) {
    if(v.vc == null)
      return;

    if(soloViewer.getComponentCount() > 0)
      soloHostClose();

    soloViewer.setVisible(true);
    //soloViewer.setTitle(v.host);
    soloViewer.setTitle(v.compname +" ("+ v.host +":"+v.port+")"); // Modified on version 1.0 - Enhanced VNC Thumbnail Viewer ***
    //this.remove(v);
    viewerPanel.remove(v); // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
    soloViewer.add(v);
    v.vc.removeMouseListener(this);
    this.validate();
    soloViewer.validate();

    if(!v.rfb.closed()) {
      v.vc.enableInput(true);
    }
    updateCanvasScaling(v, getWidthNoInsets(soloViewer), getHeightNoInsets(soloViewer));
  }


  void soloHostClose() {
    VncViewer v = (VncViewer)soloViewer.getComponent(0);
    v.enableInput(false);
    updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
    soloViewer.removeAll();
    addViewer(v);
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
    
    // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
    v.vc.scaledWidth = (fbWidth * sf + 50) / 100 - 20;
    v.vc.scaledHeight = (fbHeight * sf + 50) / 100 - 20;

    //Fix: invoke a re-paint of canvas?
    //Fix: invoke a re-size of canvas?
    //Fix: invoke a validate of viewer's gridbag?
  }


  /*void resizeThumbnails() {
    int newWidth = getWidthNoInsets(this) / thumbnailRowCount;
    int newHeight = getHeightNoInsets(this) / thumbnailRowCount;
 

    if(newWidth != widthPerThumbnail || newHeight != heightPerThumbnail) {
      widthPerThumbnail = newWidth;
      heightPerThumbnail = newHeight;

      ListIterator l = viewersList.listIterator();
      while(l.hasNext()) {
        VncViewer v = (VncViewer)l.next();
        //v.
        if(!soloViewer.isAncestorOf(v)) {
          if(v.vc != null) { // if the connection has been established
            updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
          }
        }
      }
    }

  }*/
  
  //
  // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
  //
  void resizeThumbnails() {
    int newWidth = getWidthNoInsets(this) / thumbnailRowCount;
    int newHeight = getHeightNoInsets(this) / thumbnailRowCount;
 

    if(newWidth != widthPerThumbnail || newHeight != heightPerThumbnail) {
      widthPerThumbnail = newWidth;
      heightPerThumbnail = newHeight;

      ListIterator l;
      if(isSearch){
          l = viewersSearchList.listIterator();
      }else{
          l = viewersList.listIterator();
      }
      
      while(l.hasNext()) {
        VncViewer v = (VncViewer)l.next();
        //v.
        if(!soloViewer.isAncestorOf(v)) {
          if(v.vc != null) { // if the connection has been established
            updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
          }
        }
      }
    }

  }
  
  private void loadsaveHosts(int mode) {
    FileDialog fd = new FileDialog(this, "Load hosts file...", mode);
    if(mode == FileDialog.SAVE) {
      fd.setTitle("Save hosts file...");
    }
    fd.show();

    String file = fd.getFile();
    if(file != null) {
      String dir = fd.getDirectory();
      
      if(mode == FileDialog.SAVE) {
        //ask about encrypting
        HostsFilePasswordDialog pd = new HostsFilePasswordDialog(this, true);
        if(pd.getResult()) {
          viewersList.saveToEncryptedFile(dir+file, pd.getPassword());
        } else {
          viewersList.saveToFile(dir+file);
        }
      } else {
        if(VncViewersList.isHostsFileEncrypted(dir+file)) {
          HostsFilePasswordDialog pd = new HostsFilePasswordDialog(this, false);
          viewersList.loadHosts(dir+file, pd.getPassword());
        } else {
          viewersList.loadHosts(dir+file, "");
        }
      }
    }
  }
  
  //private void quit() {
  public void quit() {
    // Called by either File->Exit or Closing of the main window
    System.out.println("Closing window");
    ListIterator l = viewersList.listIterator();
    while(l.hasNext()) {
      ((VncViewer)l.next()).disconnect();
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



  private Menu createFileMenu()
  {
    Menu fileMenu = new Menu("File");
    newhostMenuItem = new MenuItem("Add New Host");
    loadhostsMenuItem = new MenuItem("Load List of Hosts");
    savehostsMenuItem = new MenuItem("Save List of Hosts");
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
  
  //
  // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
  //
  private Menu createSettingsMenu()
  {
    Menu settingsMenu = new Menu("Settings");
    proxyMenuItem = new MenuItem("Proxy");
    loginMenuItem = new MenuItem("Login");
    proxyMenuItem.addActionListener(this);
    loginMenuItem.addActionListener(this);
    settingsMenu.add(proxyMenuItem);
    settingsMenu.addSeparator();
    settingsMenu.add(loginMenuItem);
        
    return settingsMenu;
  }
  
  private Menu createAboutMenu()
  {
    Menu helpMenu = new Menu("Help");
    aboutMenuItem = new MenuItem("About");
    aboutMenuItem.addActionListener(this);
    helpMenu.add(aboutMenuItem);
    return helpMenu;
  }


  // Window Listener Events:
  public void windowClosing(WindowEvent evt) {
    if(soloViewer.isShowing()) {
      soloHostClose();
    }

    if(evt.getComponent() == this) {
      quit();
    }

  }

  public void windowActivated(WindowEvent evt) {}
  public void windowDeactivated (WindowEvent evt) {}
  public void windowOpened(WindowEvent evt) {}
  public void windowClosed(WindowEvent evt) {}
  public void windowIconified(WindowEvent evt) {}
  public void windowDeiconified(WindowEvent evt) {}


  // Component Listener Events:
  public void componentResized(ComponentEvent evt) {
    if(evt.getComponent() == this) {
      if(thumbnailRowCount > 0) {
        resizeThumbnails();
      }
    }
    else { // resize soloViewer
      VncViewer v = (VncViewer)soloViewer.getComponent(0);
      updateCanvasScaling(v, getWidthNoInsets(soloViewer), getHeightNoInsets(soloViewer));
    }

  }

  public void componentHidden(ComponentEvent  evt) {}
  public void componentMoved(ComponentEvent evt) {}
  public void componentShown(ComponentEvent evt) {}


  // Mouse Listener Events:
  public void mouseClicked(MouseEvent evt) {
    if(evt.getClickCount() == 2) {
      Component c = evt.getComponent();
      if(c instanceof VncCanvas) {
        soloHost( ((VncCanvas)c).viewer );
      }
    }
    
  }

  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {}
  public void mousePressed(MouseEvent evt) {}
  public void mouseReleased(MouseEvent evt) {}


  // Container Listener Events:
  public void componentAdded(ContainerEvent evt) {
    // This detects when a vncviewer adds a vnccanvas to it's container
    if(evt.getChild() instanceof VncCanvas) {
      VncViewer v = (VncViewer)evt.getContainer();
      v.vc.addMouseListener(this);
      v.buttonPanel.addContainerListener(this);
      v.buttonPanel.disconnectButton.addActionListener(this);
      v.buttonPanel.removeButton.addActionListener(this); // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
      updateCanvasScaling(v, widthPerThumbnail, heightPerThumbnail);
    }

    // This detects when a vncviewer's Disconnect button had been pushed
    else if(evt.getChild() instanceof Button) {
      Button b = (Button)evt.getChild();
      if(b.getLabel() == "Reconnect") {
        b.addActionListener(this);
      }
      
      // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
      if(b.getLabel() == "Remove") {
        b.addActionListener(this);
      }
    }

  }
  
  public void componentRemoved(ContainerEvent evt) {}
  
  
  // Action Listener Event:
  public void actionPerformed(ActionEvent evt) {
    /*if( evt.getSource() instanceof Button && ((Button)evt.getSource()).getLabel() == "Hide desktop") {
      VncViewer v = (VncViewer)((Component)((Component)evt.getSource()).getParent()).getParent();
      this.remove(v);
      viewersList.remove(v);
    }*/
      
    // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
    if(evt.getSource() instanceof Button && ((Button)evt.getSource()).getLabel() == "Reconnect") {
      VncViewer v = (VncViewer)((Component)((Component)evt.getSource()).getParent()).getParent();
      int index = viewerPanel.getComponentZOrder(v);
      viewerPanel.remove(v);
      v = viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname, index);
    }
    if(evt.getSource() == newhostMenuItem) {
      hostDialog = new AddHostDialog(this);
    }
    if(evt.getSource() == savehostsMenuItem) {
      loadsaveHosts(FileDialog.SAVE);
    }
    if(evt.getSource() == loadhostsMenuItem) {
      loadsaveHosts(FileDialog.LOAD);
    }
    if(evt.getSource() == exitMenuItem) {
      quit();
    }
    
    
    //
    // Added on Enhanced VNC Thumbnail Viewer 1.0 ***
    //
    /*if(evt.getSource() instanceof Button && ((Button)evt.getSource()).getLabel() == "Remove") {
      VncViewer v = (VncViewer)((Component)((Component)evt.getSource()).getParent()).getParent();
      removeViewer(v);
    }*/
    if(evt.getSource() == aboutMenuItem) {
      new AboutDialog(this);
    }
    if(evt.getSource() == proxyMenuItem) {
      proxySettingDialog.setVisible(true);
    }
    if(evt.getSource() == loginMenuItem) {
      new LoginSettingDialog(this).setVisible(true);
    }
    if(evt.getSource() == cancelSearchButton) {
      isSearch = false;
      searchField.setText("");
      pagination = new Pagination(viewersList);
      
      clearViewerOnPage();

      VncViewer v;
      for(int i=0; i<viewersList.size(); i++){
          v = (VncViewer) viewersList.get(i);
          viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
      }
            
      enableNaviButton();
    }
    if(evt.getSource() == searchButton) {
        isSearch = true;
        viewersSearchList = searchList.searchViewer(viewersList, searchField.getText().trim());
        pagination = new Pagination(viewersSearchList);
        
        clearViewerOnPage();

        VncViewer v;
        for(int i=0; i<viewersSearchList.size(); i++){
            v = (VncViewer) viewersSearchList.get(i);
            viewersSearchList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
        }
            
        enableNaviButton();
    }
    if(evt.getSource() == previousButton || evt.getSource() == nextButton) {
        int[] step;
        
        if(evt.getSource() == previousButton){
            step = pagination.previous();
        }else{
            step = pagination.next();
        }
        
        if(step != null){
            clearViewerOnPage();

            VncViewer v;
            for(int i=step[0]; i<=step[1]; i++){
                if(isSearch){
                    v = (VncViewer) viewersSearchList.get(i);
                    viewersSearchList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
                }else{
                    v = (VncViewer) viewersList.get(i);
                    viewersList.launchViewerReconnect(v.host, v.port, v.passwordParam, v.usernameParam, v.userdomain, v.compname);
                }
            }
            enableNaviButton();
        }
    }

  }
  
  // 
  // Enable/disable button on navigator panel
  //
  private void enableNaviButton(){
      if(pagination.hasNext()){
          nextButton.setEnabled(true);
      }else{
          nextButton.setEnabled(false);
      }
            
      if(pagination.hasPrevious()){
          previousButton.setEnabled(true);
      }else{
          previousButton.setEnabled(false);
      }
      
      if(!pagination.isLimited()){
          nextButton.setEnabled(false);
          previousButton.setEnabled(false);
      }
      
      if(isSearch){
          cancelSearchButton.setEnabled(true);
      }else{
          cancelSearchButton.setEnabled(false);
      }
      
      if(pagination.isEmpty() && !isSearch){
          searchField.setEnabled(false);
          searchButton.setEnabled(false);
      }else{
          searchField.setEnabled(true);
          searchButton.setEnabled(true);
      }
      
      
  }
  
  //
  // Disconnect and remove all viewers on each page
  //
  private void clearViewerOnPage(){
      VncViewer v;
      for(int i=0; i<viewerPanel.getComponentCount(); i++){
          v = (VncViewer) viewerPanel.getComponent(i);
          v.disconnect();
      }
      viewerPanel.removeAll();
  }
  
}