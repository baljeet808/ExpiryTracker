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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant

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
            trackers.addAll(repository.getActiveTrackers())
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
            remoteView.setTextViewText(R.id.expiring_date, context.getString(R.string.date_short_var, tracker.tracker.expiryDate!!.dayOfMonth,
                tracker.tracker.expiryDate!!.month.name.substring(0,3).uppercase()))
            remoteView.setTextViewText(R.id.expiring_date_year,tracker.tracker.expiryDate!!.year.toString())
            val dateToday = Clock.System.now()

            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate

            val mfgInstant = mfgDate!!.toInstant(TimeZone.UTC)
            val expiryInstant = expiryDate!!.toInstant(TimeZone.UTC)

            val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
            val periodSpent = mfgInstant.periodUntil(dateToday, TimeZone.UTC)

            val totalHours = totalPeriod.days * 24 + totalPeriod.hours
            val spentHours = periodSpent.days * 24 + periodSpent.hours


            val progressValue =
                (spentHours.toFloat() / totalHours.toFloat()) * 100
            when {
                progressValue >= 100 -> {
                    remoteView.setTextViewText(R.id.status, context.getText(R.string.expired))
                }
                progressValue >= 80 -> {
                    remoteView.setTextViewText(R.id.status, context.getText(R.string.still_ok))
                }
                progressValue < 80 && progressValue >= 50 -> {
                    remoteView.setTextViewText(R.id.status, context.getText(R.string.good))
                }
                progressValue < 50 -> {
                    remoteView.setTextViewText(R.id.status, context.getText(R.string.fresh))
                }
            }
            remoteView.setProgressBar(R.id.item_progressbar,100,progressValue.toInt(),false)
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