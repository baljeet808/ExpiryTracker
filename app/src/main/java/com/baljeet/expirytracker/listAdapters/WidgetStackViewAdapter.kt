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
import com.baljeet.expirytracker.util.ImageConvertor
import java.time.Duration
import java.time.LocalDateTime

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

            when(tracker.productAndCategoryAndImage.image.mimeType){
                "asset"->{
                    remoteView.setImageViewResource(R.id.product_image,context.resources.getIdentifier(
                        tracker.productAndCategoryAndImage.image.imageUrl,
                        "drawable",
                        context.packageName
                    ))
                }
                else->{
                    remoteView.setImageViewBitmap(R.id.product_image,ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap))
                }
            }

            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate
            val dateToday = LocalDateTime.now()

            remoteView.setTextViewText(R.id.product_name,tracker.productAndCategoryAndImage.product.name)
            expiryDate?.let {
                remoteView.setTextViewText(R.id.expiring_date, context.getString(R.string.date_string_with_month_name_2_lined,it.dayOfMonth,it.month.name.substring(0,3).uppercase(), it.year))
            }


            val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
            val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


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