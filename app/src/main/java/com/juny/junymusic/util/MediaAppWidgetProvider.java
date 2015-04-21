package com.juny.junymusic.util;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;

import com.juny.junymusic.service.MediaPlaybackService;

/**
 * Created by Administrator on 2015-04-12.
 */
public class MediaAppWidgetProvider extends AppWidgetProvider {

    static final String TAG = "MusicAppWidgetProvider";

    public static final String CMDAPPWIDGETUPDATE = "appwidgetupdate";
    private static MediaAppWidgetProvider sInstance;

    public static synchronized MediaAppWidgetProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MediaAppWidgetProvider();
        }
        return sInstance;
    }

    /**
     * Update all active widget instances by pushing changes
     */
    public void performUpdate(MediaPlaybackService service, int[] appWidgetIds) {

    }

    /**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }

    /**
     * Handle a change notification coming over from {@link MediaPlaybackService}
     */
    public void notifyChange(MediaPlaybackService service, String what) {
        if (hasInstances(service)) {
            if (MediaPlaybackService.META_CHANGED.equals(what) ||
                    MediaPlaybackService.PLAYSTATE_CHANGED.equals(what)) {
                performUpdate(service, null);
            }
        }
    }
}
