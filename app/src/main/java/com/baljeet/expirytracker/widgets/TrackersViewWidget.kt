package com.baljeet.expirytracker.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.navigation.NavDeepLinkBuilder
import com.baljeet.expirytracker.MainActivity
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.listAdapters.WidgetStackViewAdapter
import com.baljeet.expirytracker.util.SharedPref

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

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId,R.id.trackers_stack_view)
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == WIDGET_VIEW_SYNC){
            val appWidgetId = intent.getIntExtra("appWidgetId",0)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId,R.id.trackers_stack_view)
        }
        super.onReceive(context, intent)
    }

    companion object {
        internal fun updateTrackerViewsWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.trackers_view_widget)

            SharedPref.init(context)
            if(SharedPref.isUserAPro){
                views.setViewVisibility(R.id.pro_block_screen,View.GONE)
                val serviceIntent = Intent(context, WidgetStackViewAdapter::class.java)
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
                serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
                views.setRemoteAdapter(R.id.trackers_stack_view,serviceIntent)


                val clickIntent = Intent(context,TrackersViewWidget::class.java)
                clickIntent.action = WIDGET_VIEW_SYNC
                clickIntent.putExtra("appWidgetId", appWidgetId)
                val pendingRefreshIntent = PendingIntent.getBroadcast(context,appWidgetId,clickIntent, PendingIntent.FLAG_IMMUTABLE)

                views.setOnClickPendingIntent(R.id.refresh_button,pendingRefreshIntent)
            }else{
                views.setViewVisibility(R.id.pro_block_screen,View.VISIBLE)
                val deepLinkPendingIntent = NavDeepLinkBuilder(context)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.main_nav)
                    .setDestination(R.id.bePro)
                    .createPendingIntent()

                views.setOnClickPendingIntent(R.id.check_now_btn,deepLinkPendingIntent)
            }



            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
