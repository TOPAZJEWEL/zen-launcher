package fr.neamar.kiss.pojo;

import android.util.Log;

import fr.neamar.kiss.BadgeHandler;
import fr.neamar.kiss.BuildConfig;
import fr.neamar.kiss.normalizer.StringNormalizer;

public abstract class Pojo {
    public static final String DEFAULT_ID = "(none)";
    private static final String TAG = Pojo.class.getSimpleName();
    // Globally unique ID.
    // Usually starts with provider scheme, e.g. "app://" or "contact://" to
    // ensure unique constraint
    public String id;
    // normalized name, for faster search
    public StringNormalizer.Result normalizedName = null;
    // Lower-cased name, for faster search
    //public String nameNormalized = "";
    // How relevant is this record ? The higher, the most probable it will be
    // displayed
    public int relevance = 0;
    private int badgeCount = 0;
    private String displayBadge;
    // Name for this pojo, e.g. app name
    String name = "";
    private boolean hasNotification;
    private String notificationPackage;
    private int notificationCount = 0;

    public Pojo(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getBadgeCount(){
        if (id.contains("app://")){
            if (BuildConfig.DEBUG) Log.i(TAG,"id:"+id.substring(6,id.indexOf("/",6)));
            badgeCount = BadgeHandler.getBadgeCount(id.substring(6,id.indexOf("/", 6)));
        }
        return badgeCount;
    }
    /**
     * Set the user-displayable name of this container
     * <p/>
     * When this method a searchable version of the name will be generated for the name and stored
     * as `nameNormalized`. Additionally a mapping from the positions in the searchable name
     * to the positions in the displayable name will be stored (as `namePositionMap`).
     *
     * @param name User-friendly name of this container
     */
    public void setName(String name) {
        if (name != null) {
            // Set the actual user-friendly name
            this.name = name;
            this.normalizedName = StringNormalizer.normalizeWithResult(this.name, false);
        } else {
            this.name = null;
            this.normalizedName = null;
        }
    }

    public void setName(String name, boolean generateNormalization) {
        if (generateNormalization) {
            setName(name);
        } else {
            this.name = name;
            this.normalizedName = null;
        }
    }

    public String getBadgeText(){
        return String.valueOf(badgeCount);
    }

    public void setHasNotification(boolean hasNotification){
        this.hasNotification = hasNotification;
        if (!hasNotification){
            notificationCount = 0;
        }
    }
    public void incrementNotifications(){
        this.notificationCount++;
    }
    public int getNotificationCount(){
        return this.notificationCount;
    }
    public String getNotificationCountText(){
        return String.valueOf(notificationCount);
    }

    public void setNotificationPackage(String notificationPackage){
        this.notificationPackage = notificationPackage;
    }
    public String getNotificationPackage(){
        return notificationPackage;
    }

    /**
     * ID to use in the history
     * (may be different from the one used in the adapter for display)
     */
    public String getHistoryId() {
        return this.id;
    }

    /**
     * ID to use for favorites
     * (may be different from the one used in the adapter for display, or for history)
     */
    public String getFavoriteId() {
        return getHistoryId();
    }
}
