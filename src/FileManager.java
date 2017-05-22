/* *
 * Enhanced VNC Thumbnail Viewer 1.4.0
 *  - Added load & save file of theme feature

 * Enhanced VNC Thumbnail Viewer 1.003
 *  - Added load & save file of screen capture feature
 * 
 * Enhanced VNC Thumbnail Viewer 1.002
 *  - To manage file such as open, save file
 */

import java.io.*;
import java.net.*;
import java.util.*;
import net.n3.nanoxml.*;

public class FileManager {

    public static boolean isHostsFileEncrypted(String filename) {
        boolean encrypted = false;

        try {
            File file = new File(filename);
            URL url = file.toURL();
            filename = url.getPath();

            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = StdXMLReader.fileReader(filename);
            parser.setReader(reader);
            IXMLElement root = (IXMLElement) parser.parse();

            if (root.getFullName().equalsIgnoreCase("Manifest")) {
                String e = root.getAttribute("Encrypted", "0");
                if (Integer.parseInt(e) == 1) {
                    encrypted = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error testing file for encryption.");
            System.out.println(e.getMessage());
        }

        return encrypted;
        // this returns false even if there is a problem reading the file
    }

    public static void loadFile(String filename, String encPassword, EnhancedVncThumbnailViewer evnctv) {
        try {
            File file = new File(filename);
            URL url = file.toURL();
            filename = url.getPath();

            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = StdXMLReader.fileReader(filename);
            parser.setReader(reader);
            IXMLElement root = (IXMLElement) parser.parse();

            if (root.getFullName().equalsIgnoreCase("Manifest")) {
                boolean encrypted = (1 == Integer.parseInt(root.getAttribute("Encrypted", "0")));
                String version = root.getAttribute("Version", "0.9");
                System.out.println("Loading file...  file format version " + version + " encrypted(" + encrypted + ")");

                if (encrypted && encPassword == null) {
                    // FIX-ME: do something
                    System.out.println("ERROR: Password needed to properly read file.");
                }

                Enumeration enm = root.enumerateChildren();

                // Read file version 1.4, 1.000 or 1.001 only
                if (version.equals("1.4") || version.equals("1.000") || version.equals("1.001")) {
                    while (enm.hasMoreElements()) {
                        IXMLElement e = (IXMLElement) enm.nextElement();

                        if (e.getFullName().equalsIgnoreCase("Connection")) {
                            parseConnection(e, encrypted, encPassword, evnctv.getViewerList());
                        } else {
                            System.out.println("Load: Ignoring " + e.getFullName());
                        }
                    }
                } else {
                    while (enm.hasMoreElements()) {
                        IXMLElement e = (IXMLElement) enm.nextElement();
                        Enumeration enm2 = e.enumerateChildren();

                        if (e.getFullName().equalsIgnoreCase("Connections")) {
                            while (enm2.hasMoreElements()) {
                                IXMLElement e2 = (IXMLElement) enm2.nextElement();

                                if (e2.getFullName().equalsIgnoreCase("Connection")) {
                                    parseConnection(e2, encrypted, encPassword, evnctv.getViewerList());
                                } else {
                                    System.out.println("Load: Ignoring " + e2.getFullName());
                                }
                            }
                        } else if (e.getFullName().equalsIgnoreCase("Settings")) {
                            while (enm2.hasMoreElements()) {
                                IXMLElement e2 = (IXMLElement) enm2.nextElement();

                                if (e2.getFullName().equalsIgnoreCase("Proxy")) {
                                    initSettings(e2, encrypted, encPassword, "Proxy");
                                } else if (e2.getFullName().equalsIgnoreCase("Login")) {
                                    initSettings(e2, encrypted, encPassword, "Login");
                                } else if (e2.getFullName().equalsIgnoreCase("Slideshow")) {
                                    initSettings(e2, encrypted, encPassword, "Slideshow");
                                } else if (e2.getFullName().equalsIgnoreCase("ScreenCapture")) {
                                    // Added on evnctv 1.003
                                    initSettings(e2, encrypted, encPassword, "ScreenCapture");
                                } else if (e2.getFullName().equalsIgnoreCase("Theme")) {
                                    // Added on evnctv 1.4.0
                                    initSettings(e2, encrypted, encPassword, "Theme");
                                    evnctv.setGuiTheme();
                                } else {
                                    System.out.println("Load: Ignoring " + e2.getFullName());
                                }
                            }
                        } else if (e.getFullName().equalsIgnoreCase("RecentSettings")) {
                            while (enm2.hasMoreElements()) {
                                IXMLElement e2 = (IXMLElement) enm2.nextElement();

                                if (e2.getFullName().equalsIgnoreCase("Recent")) {
                                    initSettings(e2, encrypted, encPassword, "Recent");
                                } else {
                                    System.out.println("Load: Ignoring " + e2.getFullName());
                                }
                            }
                        } else {
                            System.out.println("Load: Ignoring " + e.getFullName());
                        }

                    }
                }

            } else {
                System.out.println("Malformed file, missing manifest tag.");
                System.out.println("Found " + root.getFullName());
            }

        } catch (Exception e) {
            System.out.println("Error loading file.\n" + e.getMessage());
        }
    }

    private static boolean initSettings(IXMLElement e, boolean isEncrypted, String encPass, String child) {
        try {
            if (child.equals("Proxy")) {
                ProxySetting.setServer(e.getAttribute("Server", ProxySetting.INIT_SERVER));
                ProxySetting.setPort(e.getAttribute("Port", ProxySetting.INIT_PORT));
                ProxySetting.setIsEnable(
                        e.getAttribute("Enable",
                        ProxySetting.INIT_IS_ENABLE ? 1 : 0) == 1 ? true : false);

                System.out.println("Load proxy settings...");
            } else if (child.equals("Login")) {
                String password = e.getAttribute("Password", LoginSetting.INIT_PASSWORD);

                LoginSetting.setUsername(e.getAttribute("Username", LoginSetting.INIT_USERNAME));
                LoginSetting.setPassword(isEncrypted ? DesCipher.decryptData(password, encPass) : password);
                LoginSetting.setIsEnable(e.getAttribute("Enable", LoginSetting.INIT_IS_ENABLE ? 1 : 0) == 1 ? true : false);
                LoginSetting.setIsRemember(e.getAttribute("Remember", LoginSetting.INIT_IS_ENABLE ? 1 : 0) == 1 ? true : false);
                
                System.out.println("Load login settings...");
            } else if (child.equals("Slideshow")) {
                SlideshowSetting.setDelay(e.getAttribute("Delay", SlideshowSetting.INIT_DELAY));
                System.out.println("Load slideshow settings...");
            } else if (child.equals("ScreenCapture")) {
                // Added on evnctv 1.003
                ScreenCaptureSetting.setDelay(e.getAttribute("Delay", ScreenCaptureSetting.INIT_DELAY));
                ScreenCaptureSetting.setPath(e.getAttribute("Path", ScreenCaptureSetting.INIT_PATH));
                ScreenCaptureSetting.setIsEnable(e.getAttribute("Enable", ScreenCaptureSetting.INIT_IS_ENABLE ? 1 : 0) == 1 ? true : false);
                
                System.out.println("Load screen capture settings...");
            } else if (child.equals("Recent")) {
                RecentSettingsList.addRecent(new RecentSetting(
                        e.getAttribute("Title", ""),
                        e.getAttribute("Type", ""),
                        e.getAttribute("Date", ""),
                        e.getAttribute("Time", "")));

                System.out.println("Load recent settings list...");
            } else if (child.equals("Theme")) {
                ThemeSetting.use(e.getAttribute("Name", "Default"));
                System.out.println("Load theme settings...");
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean parseConnection(IXMLElement e, boolean isEncrypted, String encPass, VncViewersList viewerLists) {
        String host = e.getAttribute("Host", null);
        String prt = e.getAttribute("Port", null);

        boolean success = true;

        if (prt == null || host == null) {
            System.out.println("Missing Host or Port attribute");
            success = false;
        } else {
            int port = Integer.parseInt(prt);

            int secType = Integer.parseInt(e.getAttribute("SecType", "1"));
            String password = e.getAttribute("Password", null);
            String username = e.getAttribute("Username", null);
            String userdomain = e.getAttribute("UserDomain", null);
            String compname = e.getAttribute("CompName", null);
            String comment = e.getAttribute("Comment", null);

            if (isEncrypted) {
                if (password != null) {
                    password = DesCipher.decryptData(password, encPass);
                }
                if (username != null) {
                    username = DesCipher.decryptData(username, encPass);
                }
                if (userdomain != null) {
                    userdomain = DesCipher.decryptData(userdomain, encPass);
                }
                if (compname != null) {
                    compname = DesCipher.decryptData(compname, encPass);
                }
                if (comment != null) {
                    comment = DesCipher.decryptData(comment, encPass);
                }
            }

            // Error Checking:
            switch (secType) {
                case 1: // none
                    if (password != null || username != null) {
                        System.out.println("WARNING: Password or Username specified for NoAuth");
                    }
                case 2: // vnc auth
                    if (password == null) {
                        System.out.println("ERROR: Password missing for VncAuth");
                        success = false;
                    }
                    if (username != null) {
                        System.out.println("WARNING: Username specified for VncAuth");
                    }
                    break;
                case -6: // ms-logon
                    if (password == null || username != null) {
                        System.out.println("ERROR: Password or Username missing for MsAuth");
                        success = false;
                    }
                    break;
                case 5: // ra2
                case 6: // ra2ne
                case 16: // tight
                case 17: // ultra
                case 18: // tls
                case 19: // vencrypt
                    System.out.println("ERROR: Incomplete security type (" + secType + ") for Host: " + host + " Port: " + port);
                case 0: // invalid
                default:
                    // Error
                    success = false;
                    break;
            }

            // Launch the Viewer:
            System.out.println("LOAD Host: " + host + " Port: " + port + " SecType: " + secType);
            if (success) {
                if (viewerLists.getViewer(host, port) == null) {
                    VncViewer v = viewerLists.launchViewer(host, port, password, username, userdomain, compname); // Modified on Enhanced VNC Thumbnail Viewer 1.0 ***
                    //VncViewer v = launchViewer(host, port, password, username, userdomain);
                    //v.setCompName(compname);
                    //v.setComment(comment);
                }
                // else - the host is already open
            }
        }

        return success;
    }

    public static void saveEncryptedFile(String filename, String encPassword, VncViewersList viewersList) {
        if (encPassword == null || encPassword.length() == 0) {
            System.out.println("WARNING: Saving to encrypted file with empty passkey");
        }
        writeFile(true, filename, encPassword, viewersList);
    }

    public static void saveFile(String filename, VncViewersList viewersList) {
        writeFile(false, filename, null, viewersList);
    }

    private static void writeFile(boolean isEncrypted, String filename, String encPassword, VncViewersList viewersList) {

        IXMLElement manifest = new XMLElement("Manifest");
        manifest.setAttribute("Encrypted", (isEncrypted ? "1" : "0"));
        manifest.setAttribute("Version", EnhancedVncThumbnailViewer.VERSION);

        // Settings element
        IXMLElement settings = new XMLElement("Settings");
        manifest.addChild(settings);

        // Proxy child
        IXMLElement proxy = settings.createElement("Proxy");
        proxy.setAttribute("Server", ProxySetting.getServer());
        proxy.setAttribute("Port", ProxySetting.getPort() + "");
        proxy.setAttribute("Enable", ProxySetting.getIsEnable() ? "1" : "0");
        settings.addChild(proxy);

        // Login child
        IXMLElement login = settings.createElement("Login");
        login.setAttribute("Username", LoginSetting.getUsername());
        login.setAttribute("Password", isEncrypted
                ? DesCipher.encryptData(LoginSetting.getPassword(), encPassword) : LoginSetting.getPassword());
        login.setAttribute("Enable", LoginSetting.getIsEnable() ? "1" : "0");
        login.setAttribute("Remember", LoginSetting.getIsRemember() ? "1" : "0");
        settings.addChild(login);

        // Slideshow child
        IXMLElement slideShow = settings.createElement("Slideshow");
        slideShow.setAttribute("Delay", SlideshowSetting.getDelay() + "");
        settings.addChild(slideShow);
        
        // Added on evnctv 1.003 - Screen capture child
        IXMLElement screenCapture = settings.createElement("ScreenCapture");
        screenCapture.setAttribute("Delay", ScreenCaptureSetting.getDelay() + "");
        screenCapture.setAttribute("Path", ScreenCaptureSetting.getPath());
        screenCapture.setAttribute("Enable", ScreenCaptureSetting.getIsEnable() ? "1" : "0");
        settings.addChild(screenCapture);
        
        // Added on evnctv 1.4.0 - Theme
        IXMLElement theme = settings.createElement("Theme");
        theme.setAttribute("Name", ThemeSetting.getName());
        settings.addChild(theme);

        // Recent settings element
        if (RecentSettingsList.getTotalRecents().size() > 0) {
            IXMLElement recentSettings = new XMLElement("RecentSettings");
            manifest.addChild(recentSettings);

            // Recent child
            Enumeration enm = RecentSettingsList.getTotalRecents().elements();
            while (enm.hasMoreElements()) {
                RecentSetting rs = (RecentSetting) enm.nextElement();

                IXMLElement recent = settings.createElement("Recent");
                recent.setAttribute("Title", rs.getTitle());
                recent.setAttribute("Type", rs.getType());
                recent.setAttribute("Date", rs.getDate());
                recent.setAttribute("Time", rs.getTime());
                recentSettings.addChild(recent);
            }
        }
        
        // Connections element
        if (!viewersList.isEmpty()) {
            IXMLElement connections = new XMLElement("Connections");
            manifest.addChild(connections);

            // Connection child
            ListIterator l = viewersList.listIterator();
            while (l.hasNext()) {
                VncViewer v = (VncViewer) l.next();
                String host = v.host;
                String port = Integer.toString(v.port);
                String password = v.passwordParam;
                String username = v.usernameParam;
                String compname = v.compname;
                String sectype = "1";

                if (password != null && password.length() != 0) {
                    sectype = "2";
                    if (username != null && username.length() != 0) {
                        sectype = "-6";
                    }
                }

                if (isEncrypted) {
                    if (sectype != "1") {
                        password = DesCipher.encryptData(password, encPassword);
                    }
                    if (sectype == "-6") {
                        username = DesCipher.encryptData(username, encPassword);
                    }
                    //compname = encryptData(compname,encPassword);
                    //comment = encryptData(comment,encPassword);
                }

                IXMLElement c = connections.createElement("Connection");
                connections.addChild(c);

                c.setAttribute("Host", host);
                c.setAttribute("Port", port);
                c.setAttribute("SecType", sectype);
                if (sectype == "2" || sectype == "-6") {
                    c.setAttribute("Password", password);
                    if (sectype == "-6") {
                        c.setAttribute("Username", username);
                        //c.setAttribute("UserDomain", userdomain);
                    }
                }
                c.setAttribute("CompName", compname);
            }
        }

        try {
            PrintWriter o = new PrintWriter(new FileOutputStream(filename));
            XMLWriter writer = new XMLWriter(o);
            o.println("<?xml version=\"1.0\" standalone=\"yes\"?>");
            writer.write(manifest, true);
            System.out.println("Saved file...");

        } catch (IOException e) {
            System.out.print("Error saving file.\n" + e.getMessage());
        }

    }
}