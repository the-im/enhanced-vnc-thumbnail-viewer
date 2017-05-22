//
//  Copyright (C) 2007-2008 David Czechowski  All Rights Reserved.
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

/*
 * Enhanced VNC Thumbnail Viewer 1.001
 *      - Called proxy setting from Setting class
 * 
 * Enhanced VNC Thumbnail Viewer 1.0
 *      - New methods -> launchViewer()
 */

import java.awt.*;
import java.io.*;
import net.n3.nanoxml.*; // Source available at http://nanoxml.cyberelf.be/
import java.util.*;
import java.net.*;//TEMP

//
// This Vector-based List is used to maintain of a list of VncViewers
//
// This also contains the ability to load/save it's list of VncViewers to/from
//    an external xml file.
//

class VncViewersList extends Vector {
    
  private EnhancedVncThumbnailViewer tnViewer;

  //
  // Constructor.
  //
  public VncViewersList(EnhancedVncThumbnailViewer v)
  {
    super();
    tnViewer = v;
  }

  //
  // If a host is loaded in first time, this method will be called
  //
  public VncViewer launchViewer(String host, int port, String password, String user, String userdomain, String compname) {
    VncViewer v = launchViewer(tnViewer, host, port, password, user, userdomain, compname);
    add(v);
    tnViewer.addViewer(v);

    return v;
  }
  
  //
  // Added on evnctv 1.000
  //    When want to reconnect, this will be called
  //
  public VncViewer launchViewerReconnect(String host, int port, String password, String user, String userdomain, String compname) {
    VncViewer v = launchViewer(tnViewer, host, port, password, user, userdomain, compname);
    tnViewer.addViewer(v);
    
    return v;
  }
  public VncViewer launchViewerReconnect(String host, int port, String password, String user, String userdomain, String compname, int order) {
    VncViewer v = launchViewer(tnViewer, host, port, password, user, userdomain, compname);
    tnViewer.addViewer(v, order);
    
    return v;
  }
  
  public static VncViewer launchViewer(EnhancedVncThumbnailViewer tnviewer, String host, int port, String password, String user, String userdomain, String compname) {
    String args[] = new String[4];
    args[0] = "host";
    args[1] = host;
    args[2] = "port";
    args[3] = Integer.toString(port);

    if(password != null && password.length() != 0) {
      int newlen = args.length + 2;
      String[] newargs = new String[newlen];
      System.arraycopy(args, 0, newargs, 0, newlen-2);
      newargs[newlen-2] = "password";
      newargs[newlen-1] = password;
      args = newargs;
    }

    if(user != null && user.length() != 0) {
      int newlen = args.length + 2;
      String[] newargs = new String[newlen];
      System.arraycopy(args, 0, newargs, 0, newlen-2);
      newargs[newlen-2] = "username";
      newargs[newlen-1] = user;
      args = newargs;
    }

    if(userdomain != null && userdomain.length() != 0) {
      int newlen = args.length + 2;
      String[] newargs = new String[newlen];
      System.arraycopy(args, 0, newargs, 0, newlen-2);
      newargs[newlen-2] = "userdomain";
      newargs[newlen-1] = userdomain;
      args = newargs;
    }
    
    if(compname != null && compname.length() != 0) {
      int newlen = args.length + 2;
      String[] newargs = new String[newlen];
      System.arraycopy(args, 0, newargs, 0, newlen-2);
      newargs[newlen-2] = "compname";
      newargs[newlen-1] = compname;
      args = newargs;
    }

    // launch a new viewer
    System.out.println("Launch Host: " + host + ":" + port);
    //VncViewer v = new VncViewer();
    VncViewer v = new VncViewer();
    v.mainArgs = args;
    v.inAnApplet = false;
    v.inSeparateFrame = false;
    v.showControls = true;
    v.showOfflineDesktop = true;
    v.vncFrame = tnviewer;
    v.init();
    v.options.viewOnly = true;
    v.options.autoScale = true; // false, because ThumbnailViewer maintains the scaling
    v.options.scalingFactor = 10;
    v.addContainerListener(tnviewer);
    v.start();
    
    return v;
  }


  public VncViewer getViewer(String hostname, int port) {
    VncViewer v = null;

    ListIterator l = listIterator();
    while(l.hasNext()) {
      v = (VncViewer)l.next();
      if(v.host == hostname && v.port == port) {
        return v;
      }
    }

    return null;
  }

  public VncViewer getViewer(Container c) {
    VncViewer v = null;

    ListIterator l = listIterator();
    while(l.hasNext()) {
      v = (VncViewer)l.next();
      if(c.isAncestorOf(v)) {
        return v;
      }
    }

    return null;
  }

  public VncViewer getViewer(Button b) {
    VncViewer v;

    ListIterator l = listIterator();
    while(l.hasNext()) {
      v = (VncViewer)l.next();
      if(v.getParent().isAncestorOf(b)) {
        return v;
      }
    }

    return null;
  }

}
