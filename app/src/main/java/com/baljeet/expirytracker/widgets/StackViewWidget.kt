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
import com.baljeet.expirytracker.listAdapters.WidgetStackViewAdapter


const val ACTION_NAME = "refresh_button"
const val WIDGET_ID = "widget_id"
class StackViewWidget : AppWidgetProvider() {

    


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.stack_view_widget)

            val serviceIntent = Intent(context,WidgetStackViewAdapter::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
            views.setRemoteAdapter(R.id.trackers_stack_view,serviceIntent)

            val clickIntent = Intent(context , StackViewWidget::class.java)
            clickIntent.action = ACTION_NAME
            val clickPendingIntent = PendingIntent.getBroadcast(context,0,clickIntent,PendingIntent.FLAG_IMMUTABLE)

            views.setPendingIntentTemplate(R.id.refresh_button,clickPendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == ACTION_NAME){
            val widgetId  = intent.getIntExtra(WIDGET_ID,0)
            Toast.makeText(context,"$widgetId", Toast.LENGTH_SHORT).show()
        }
        super.onReceive(context, intent)
    }
}