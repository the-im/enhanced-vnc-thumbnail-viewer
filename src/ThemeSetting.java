import java.io.FileReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * Enhanced VNC Thumbnail Viewer 1.4
 * Setting theme
 * Default is default theme
 * 
 */
public class ThemeSetting {
    
    public final static String[] LIST = {"Default", "Black"};
    
    private static JSONObject settings;
    private static String name = "Default";

    public static String getName() {
        return name;
    }
    
    /**
     * Set value to use default theme
     */
    public static void useDefault() {
        name = "default";
        readFile();
    }
    
    /**
     * Use just one theme
     * 
     * @param str the theme name
     */
    public static void use(String str) {
        name = str;
        readFile();
    }
    
    /**
     * Get a setting in settings file 
     * 
     * @param setting the key name
     * @return the value
     */
    public static String get(String setting) {
        try {
            if (settings == null)
                readFile();
            
            JSONObject inSettings = settings;
            String[] subs = setting.split("\\.");
            for (int i = 0; i < subs.length; i++) {
                if (i == subs.length - 1)
                    return inSettings.getString(subs[i]);
                else
                    inSettings = inSettings.getJSONObject(subs[i]);
            }
        } catch (JSONException ex) {
            System.err.println("Cannot find value by this key.");
        }
        return null;
    }
    
    /**
     * Read settings file
     */
    protected static void readFile() {
        try {
            settings = new JSONObject(new JSONTokener(new FileReader("resources/themes/" + name.toLowerCase()  + ".json")));
        } catch (Exception ex) {
            String msg = "Cannot find theme settings file. Now use Default theme instead.";
            RecentSettingsList.addRecent(new RecentSetting(msg, "Theme"));
            System.err.println(msg);
            useDefault();
        }
    }
}
