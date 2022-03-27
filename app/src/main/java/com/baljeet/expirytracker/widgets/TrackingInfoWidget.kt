package com.baljeet.expirytracker.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
            Toast.makeText(context,"on update called", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        Toast.makeText(context!!,"on changed - $appWidgetId", Toast.LENGTH_SHORT).show()
        updateAppWidget(context, appWidgetManager!!, appWidgetId)
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if(intent.action.equals(WIDGET_SYNC)){
            val appWidgetID = intent.getIntExtra("appWidgetId",0)
            updateAppWidget(context!!, AppWidgetManager.getInstance(context),appWidgetID)
        }
        super.onReceive(context, intent)
    }

    companion object{
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val trackers = ArrayList<TrackerAndProduct>()


            val trackerDao = AppDatabase.getDatabase(context.applicationContext).trackerDao()
            val repository = TrackerRepository(trackerDao)

            // Construct the RemoteViews object
            trackers.addAll(repository.getActiveTrackers())


            val clickIntent = Intent(context,TrackingInfoWidget::class.java)
            clickIntent.action = WIDGET_SYNC
            clickIntent.putExtra("appWidgetId", appWidgetId)
            val pendingRefreshIntent = PendingIntent.getBroadcast(context,appWidgetId,clickIntent,PendingIntent.FLAG_IMMUTABLE)
            val views = RemoteViews(context.packageName, R.layout.tracking_info_widget).also {
                it.setTextViewText(
                    R.id.fresh_num,
                    trackers.filter { t -> t.tracker.getTrackingStatus() == ActiveTrackingStatus.FRESH }.size.toString()
                )
                it.setTextViewText(
                    R.id.ok_products_num,
                    trackers.filter { t -> t.tracker.getTrackingStatus() == ActiveTrackingStatus.NEAR_EXPIRY }.size.toString()
                )
                it.setTextViewText(
                    R.id.near_expiry_num,
                    trackers.filter { t -> t.tracker.getTrackingStatus() == ActiveTrackingStatus.STILL_OK }.size.toString()
                )
                it.setTextViewText(
                    R.id.expired_num,
                    trackers.filter { t -> t.tracker.getTrackingStatus() == ActiveTrackingStatus.EXPIRED }.size.toString()
                )
                it.setTextViewText(
                    R.id.heading,
                    context.getString(R.string.total_expiry_tracking_var, trackers.size)
                )
                it.setOnClickPendingIntent(R.id.refresh_button,pendingRefreshIntent)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}



