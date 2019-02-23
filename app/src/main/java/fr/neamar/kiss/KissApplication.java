package fr.neamar.kiss;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

public class KissApplication extends Application {
    /**
     * Number of ms to wait, after a click occurred, to record a launch
     * Setting this value to 0 removes all animations
     */
    public static final int TOUCH_DELAY = 0;
    private DataHandler dataHandler;
    private RootHandler rootHandler;
    private IconsHandler iconsPackHandler;
    private MainActivity mainActivity;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // create intent to update all instances of the widget
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, ZenWidget.class);

        // retrieve all appWidgetIds for the widget & put it into the Intent
        AppWidgetManager appWidgetMgr = AppWidgetManager.getInstance(this);
        ComponentName cm = new ComponentName(this, ZenWidget.class);
        int[] appWidgetIds = appWidgetMgr.getAppWidgetIds(cm);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        // update the widget
        sendBroadcast(intent);
    }

    public static KissApplication getApplication(Context context) {
        return (KissApplication) context.getApplicationContext();
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public DataHandler getDataHandler() {
        if (dataHandler == null) {
            dataHandler = new DataHandler(this);
        }
        return dataHandler;
    }

    public void setDataHandler(DataHandler newDataHandler) {
        dataHandler = newDataHandler;
    }
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public RootHandler getRootHandler() {
        if (rootHandler == null) {
            rootHandler = new RootHandler(this);
        }
        return rootHandler;
    }

    public void resetRootHandler(Context ctx) {
        rootHandler.resetRootHandler(ctx);
    }

    public IconsHandler getIconsHandler() {
        if (iconsPackHandler == null) {
            iconsPackHandler = new IconsHandler(this);
        }

        return iconsPackHandler;
    }

    public void resetIconsHandler() {
        iconsPackHandler = new IconsHandler(this);
    }

    public static void startLaucherService(Intent intent, Context context){
        try {
            context.startService(intent);
        }catch (IllegalStateException e){
            if(BuildConfig.DEBUG) Log.d("KissApplication", "app in background?");
        }
    }
}
