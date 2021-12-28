package com.baljeet.expirytracker.listAdapters

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.util.SharedPref

class WidgetStackViewAdapter: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return WidgetStackViewFactory(applicationContext,intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)!!)
    }

    class  WidgetStackViewFactory(private val context : Context,
                                  private val appWidgetId : Int) : RemoteViewsFactory{
        private var repository: TrackerRepository
        private val trackers = ArrayList<TrackerAndProduct>()

        init {
            val trackerDao = AppDatabase.getDatabase(context.applicationContext).trackerDao()
            repository = TrackerRepository(trackerDao)

        }

        override fun onCreate() {
            trackers.clear()
            trackers.addAll(repository.getActiveTrackers().toCollection(ArrayList()))
        }

        override fun onDataSetChanged() {
            trackers.clear()
            trackers.addAll(repository.getActiveTrackers())
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return trackers.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val tracker = trackers[position]
            val remoteView = RemoteViews(context.packageName, R.layout.widget_stack_view_item)
            remoteView.setImageViewResource(R.id.product_image,context.resources.getIdentifier(
                tracker.productAndCategoryAndImage.image.imageUrl,
                "drawable",
                context.packageName
            ))
            remoteView.setTextViewText(R.id.product_name,tracker.productAndCategoryAndImage.product.name)
            remoteView.setTextViewText(R.id.expiry_date, tracker.tracker.expiryDate.date.toString())
            return remoteView
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(context.packageName, R.layout.widget_stack_view_item)
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }
}