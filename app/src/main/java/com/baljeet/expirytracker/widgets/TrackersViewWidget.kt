package com.baljeet.expirytracker.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.listAdapters.WidgetStackViewAdapter

/**
 * Implementation of App Widget functionality.
 */
const val WIDGET_VIEW_SYNC ="WIDGET_VIEW_SYNC"

class TrackersViewWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateTrackerViewsWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action == WIDGET_VIEW_SYNC){
            val appWidgetID = intent.getIntExtra("appWidgetId",0)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetID,R.id.trackers_stack_view)
            updateTrackerViewsWidget(context, AppWidgetManager.getInstance(context),appWidgetID)
        }
        super.onReceive(context, intent)
    }
}

internal fun updateTrackerViewsWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.trackers_view_widget)

    Toast.makeText(context,"updating ", Toast.LENGTH_SHORT).show()


    val trackers = ArrayList<TrackerAndProduct>()

    val trackerDao = AppDatabase.getDatabase(context.applicationContext).trackerDao()
    val repository = TrackerRepository(trackerDao)

    val serviceIntent = Intent(context, WidgetStackViewAdapter::class.java)
    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
    views.setRemoteAdapter(R.id.trackers_stack_view,serviceIntent)
    

    val clickIntent = Intent(context,TrackersViewWidget::class.java)
    clickIntent.action = WIDGET_VIEW_SYNC
    clickIntent.putExtra("appWidgetId", appWidgetId)
    val pendingRefreshIntent = PendingIntent.getBroadcast(context,0,clickIntent, PendingIntent.FLAG_IMMUTABLE)

    views.setOnClickPendingIntent(R.id.refresh_button,pendingRefreshIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}