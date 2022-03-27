package com.baljeet.expirytracker.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.util.ActiveTrackingStatus
import com.baljeet.expirytracker.util.getTrackingStatus

/**
 * Implementation of App Widget functionality.
 */


const val WIDGET_SYNC ="WIDGET_SYNC"
class TrackingInfoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
        }
    }
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action == WIDGET_SYNC){
            val appWidgetID = intent.getIntExtra("appWidgetId",0)
            updateAppWidget(context, AppWidgetManager.getInstance(context),appWidgetID)
            Toast.makeText(context,"Tracking synced", Toast.LENGTH_SHORT).show()
        }
        super.onReceive(context, intent)
    }
}



internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val trackers = ArrayList<TrackerAndProduct>()

    Toast.makeText(context,"updating ", Toast.LENGTH_SHORT).show()

    val trackerDao = AppDatabase.getDatabase(context.applicationContext).trackerDao()
    val repository = TrackerRepository(trackerDao)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.tracking_info_widget)
    trackers.addAll(repository.getActiveTrackers())
    views.setTextViewText(R.id.fresh_num, trackers.filter { t-> t.tracker.getTrackingStatus() == ActiveTrackingStatus.FRESH }.size.toString())
    views.setTextViewText(R.id.ok_products_num, trackers.filter { t-> t.tracker.getTrackingStatus() == ActiveTrackingStatus.NEAR_EXPIRY }.size.toString())
    views.setTextViewText(R.id.near_expiry_num, trackers.filter { t-> t.tracker.getTrackingStatus() == ActiveTrackingStatus.STILL_OK }.size.toString())
    views.setTextViewText(R.id.expired_num, trackers.filter { t-> t.tracker.getTrackingStatus() == ActiveTrackingStatus.EXPIRED }.size.toString())
    views.setTextViewText(R.id.heading,context.getString(R.string.total_expiry_tracking_var, trackers.size))

    val clickIntent = Intent(context,TrackingInfoWidget::class.java)
    clickIntent.action = WIDGET_SYNC
    clickIntent.putExtra("appWidgetId", appWidgetId)
    val pendingRefreshIntent = PendingIntent.getBroadcast(context,0,clickIntent,PendingIntent.FLAG_IMMUTABLE)

    views.setOnClickPendingIntent(R.id.refresh_button,pendingRefreshIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}