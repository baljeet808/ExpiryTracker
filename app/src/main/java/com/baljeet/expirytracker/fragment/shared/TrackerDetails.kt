package com.baljeet.expirytracker.fragment.shared

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentTrackerSummaryBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import com.dwellify.contractorportal.util.TimeConvertor
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


enum class PickingFor{
    EXPIRY, MFG, REMINDER
}

class TrackerDetails : Fragment() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var bind : FragmentTrackerSummaryBinding
    private val navArgs : TrackerDetailsArgs by navArgs()
    private val trackerViewModel : TrackerViewModel by viewModels()

    private var pickedFor : PickingFor = PickingFor.EXPIRY
    private var tempDateTime :LocalDateTime ? = null


    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager


    private lateinit var adLoader : AdLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentTrackerSummaryBinding.inflate(inflater, container, false)
        bind.apply {
            SharedPref.init(requireContext())
            backButton.setOnClickListener { activity?.onBackPressed() }
            val selectedTracker = trackerViewModel.getTrackerById(navArgs.selectedTrackerId)
            with(selectedTracker){
                productName.text = productAndCategoryAndImage.product.name
                categoryNameValue.text = productAndCategoryAndImage.categoryAndImage.category.categoryName
                expiryDateValue.text = tracker.expiryDate?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
                manufactureDateValue.text = tracker.mfgDate?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
                reminderDateValue.text = tracker.reminderDate?.let {
                    getString(
                        R.string.date_string_with_month_name_and_time_2_lined,
                        it.month.name.substring(0, 3).uppercase(),
                        it.dayOfMonth,
                        it.year,
                        TimeConvertor.getTime(it.hour,it.minute,true)
                    )
                } ?: kotlin.run {
                    getString(R.string.no_time)
                }

                when(productAndCategoryAndImage.image.mimeType){
                    "asset"->{
                        optionImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                resources.getIdentifier(
                                    productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    requireContext().packageName
                                )
                            )
                        )
                    }
                    else->{
                        optionImage.setImageBitmap(
                            ImageConvertor.stringToBitmap(productAndCategoryAndImage.image.bitmap)
                        )
                        optionImage.setOnClickListener { Navigation.findNavController(requireView()).navigate(TrackerDetailsDirections.actionGlobalImagePreview(productAndCategoryAndImage.image)) }
                    }
                }
                when(productAndCategoryAndImage.categoryAndImage.image.mimeType){
                    "asset" ->{
                        categoryImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                resources.getIdentifier(
                                    productAndCategoryAndImage.categoryAndImage.image.imageUrl,
                                    "drawable",
                                    requireContext().packageName
                                )
                            )
                        )
                    }
                    else->{
                        categoryImage.setImageBitmap(
                            ImageConvertor.stringToBitmap(productAndCategoryAndImage.categoryAndImage.image.bitmap)
                        )
                        categoryImage.setOnClickListener { Navigation.findNavController(requireView()).navigate(TrackerDetailsDirections.actionGlobalImagePreview(productAndCategoryAndImage.categoryAndImage.image)) }

                    }
                }


                val expiryDate = tracker.expiryDate
                val mfgDate = tracker.mfgDate

                val dateToday = LocalDateTime.now()

                val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
                val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


                val progressValue =
                    (spentHours.toFloat() / totalHours.toFloat()) * 100
                itemProgressbar.apply {
                    val layerDrawable = progressDrawable as LayerDrawable
                    val progressDrawableClip = layerDrawable.getDrawable(1)
                    when {
                        progressValue >= 100->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                            statusText.text = getString(R.string.expired)
                        }
                        progressValue >= 80 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                            statusText.text = getString(R.string.expiring)
                        }
                        progressValue < 80 && progressValue >= 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                            statusText.text = getString(R.string.still_ok)
                        }
                        progressValue < 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                            statusText.text = getString(R.string.fresh)
                        }
                    }
                    ObjectAnimator.ofInt(itemProgressbar,"progress",progressValue.toInt()).setDuration(1500).start()
                }


                editExpiryDateButton.setOnClickListener {
                    pickedFor = PickingFor.EXPIRY
                    DatePickerDialog(requireContext(),this@TrackerDetails,expiryDate!!.year,expiryDate.monthValue-1,expiryDate.dayOfMonth).show()
                }
                editReminderButton.setOnClickListener {
                    pickedFor = PickingFor.REMINDER
                    val current = Calendar.getInstance()
                    tracker.reminderDate?.let { reminderDate->
                        DatePickerDialog(requireContext(),this@TrackerDetails,reminderDate.year,reminderDate.monthValue-1,reminderDate.dayOfMonth).show()
                    } ?: kotlin.run {
                        DatePickerDialog(requireContext(),this@TrackerDetails,current.get(Calendar.YEAR),current.get(Calendar.MONTH),current.get(Calendar.DAY_OF_MONTH)).show()
                    }
                }
                editMfgDateButton.setOnClickListener {
                    pickedFor = PickingFor.MFG
                    DatePickerDialog(requireContext(),this@TrackerDetails,mfgDate!!.year,mfgDate.monthValue-1,mfgDate.dayOfMonth).show()
                }
                deleteButton.setOnClickListener {
                     delete(tracker,progressValue)
                    activity?.onBackPressed()
                }
                markUsedButton.setOnClickListener {
                    markTrackerAsUsedBasedOnProgress(progressValue, tracker)
                    activity?.onBackPressed()
                }
                reminderOnOffToggle.isChecked = tracker.reminderOn
                reminderOnOffToggle.setOnCheckedChangeListener { _, isChecked ->
                    setReminderOnValue(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker, isChecked)
                }
            }

            if(SharedPref.isUserAPro){
                bind.adLayout.isGone = true
                youtubeBtn.isGone = false
                playStoreBtn.isGone = false
                facebookBtn.isGone = false
            }
            else{
                youtubeBtn.isGone = true
                playStoreBtn.isGone = true
                facebookBtn.isGone = true
                adLoader = AdLoader.Builder(requireContext(), Constants.TRACKER_DETAIL_NATIVE_INLINE_AD_ID)
                    .forNativeAd { ad : NativeAd ->
                        // Show the ad.
                        val adView =  layoutInflater.inflate(R.layout.native_ad_view_layout,container, false) as NativeAdView

                        populateAdVIew(ad,adView)
                        bind.adLayout.isGone = false
                        bind.adLayout.removeAllViews()
                        bind.adLayout.addView(adView)
                        if(activity?.isDestroyed == true){
                            ad.destroy()
                            return@forNativeAd
                        }
                    }.withAdListener(object : AdListener() {

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            bind.adLayout.isGone = true
                            Log.d("Log for - Ad Failure ","$adError")
                        }
                    }).build()
                adLoader.loadAd(AdRequest.Builder().build())
            }

            reviewManager = ReviewManagerFactory.create(requireContext())
            val managerInfoTask = reviewManager.requestReviewFlow()
            managerInfoTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reviewInfo = task.result
                } else {
                    Toast.makeText(requireContext(), "review failed to start", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            playStoreBtn.setOnClickListener {
                if(SharedPref.reviewCompleted){

                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.parse("market://details?id=com.baljeet.expirytracker")
                        startActivity(intent)
                    } catch (error: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.baljeet.expirytracker")
                            )
                        )
                    }
                }else
                {
                    SharedPref.reviewCompleted = true
                    reviewInfo?.let {
                        reviewManager.launchReviewFlow(requireActivity(),it)
                    }?: kotlin.run {
                        Toast.makeText(requireContext(),"review info not initiated", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            youtubeBtn.setOnClickListener {
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:4an6gXLwJhc"))
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=4an6gXLwJhc")
                )
                try {
                    startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    startActivity(webIntent)
                }
            }

            facebookBtn.setOnClickListener {
                val facebookId = "fb://page/215172462242076"
                val urlPage = "http://www.facebook.com/88KBDev"

                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)))
                } catch (e: Exception) {
                    Log.e("Log for- facebook ", "Application not installed.")
                    //Open url web page.
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)))
                }
            }
        }
    }

    private fun updateProgressValue() {
        bind.apply {
            with(trackerViewModel.getTrackerById(navArgs.selectedTrackerId)){

                val expiryDate = tracker.expiryDate
                val mfgDate = tracker.mfgDate

                val dateToday = LocalDateTime.now()

                val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
                val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


                val progressValue =
                    (spentHours.toFloat() / totalHours.toFloat()) * 100
                itemProgressbar.apply {
                    val layerDrawable = progressDrawable as LayerDrawable
                    val progressDrawableClip = layerDrawable.getDrawable(1)
                    when {
                        progressValue >= 100->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                            statusText.text = getString(R.string.expired)
                        }
                        progressValue >= 80 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                            statusText.text = getString(R.string.expiring)
                        }
                        progressValue < 80 && progressValue >= 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                            statusText.text = getString(R.string.still_ok)
                        }
                        progressValue < 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                            statusText.text = getString(R.string.fresh)
                        }
                    }
                    ObjectAnimator.ofInt(itemProgressbar,"progress",progressValue.toInt()).setDuration(1500).start()
                }
            }
        }
    }


    private fun populateAdVIew(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline


        nativeAd.mediaContent?.let {
            adView.mediaView?.setMediaContent(it)
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating?.toFloat()?:0F
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }



    private fun delete(tracker : Tracker, progressValue: Float){
            tracker.isArchived = true
            if(progressValue >= 100 ){
                tracker.gotExpired = true
                tracker.isUsed = true
                tracker.usedDate = LocalDateTime.now()
            }
        updateTracker(tracker)
    }

    

    private fun markTrackerAsUsedBasedOnProgress(progressValue : Float, tracker: Tracker){
        when {
            progressValue >=100->{
                tracker.gotExpired = true
            }
            progressValue >= 80 && progressValue <100 -> {
                tracker.usedNearExpiry = true
            }
            progressValue < 80 && progressValue >= 50 -> {
                tracker.usedWhileOk = true
            }
            progressValue < 50 -> {
                tracker.usedWhileFresh = true
            }
        }
        tracker.isUsed = true
        tracker.usedDate = LocalDateTime.now()
        updateTracker(tracker)
    }

    private fun setReminderOnValue(tracker: Tracker, isChecked: Boolean) {
        if (isChecked) {
            tracker.reminderDate?.let {
                NotificationUtil.setReminderForProducts(
                    it,
                    requireContext(),
                    tracker.trackerId
                )
            }
            Toast.makeText(requireContext(),"Reminder on", Toast.LENGTH_SHORT).show()
        } else {
            tracker.reminderDate?.let {
                NotificationUtil.removeReminderForProduct(
                    requireContext(),
                    tracker.trackerId
                )
            }
            Toast.makeText(requireContext(),"Reminder off", Toast.LENGTH_SHORT).show()
        }
        tracker.apply {
            reminderOn = isChecked
        }
        updateTracker(tracker)
    }

    private fun updateTracker(tracker : Tracker){
        trackerViewModel.updateTracker(tracker)
        updateProgressValue()
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        tempDateTime = LocalDateTime.of(year,month+1, dayOfMonth, 0, 0)
        val currentTime = LocalDateTime.now()
        when (pickedFor) {
            PickingFor.REMINDER -> {
                TimePickerDialog(requireContext(), this, currentTime.hour, currentTime.minute, false).show()
            }
            PickingFor.EXPIRY -> {
                updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply {
                    expiryDate = tempDateTime
                })
                bind.expiryDateValue.text = tempDateTime?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
            }
            else -> {
                updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply { mfgDate = tempDateTime })
                bind.manufactureDateValue.text = tempDateTime?.let {
                    getString(R.string.date_string_with_month_name, it.month.name.substring(0,3).uppercase(),it.dayOfMonth,it.year)
                }
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
         tempDateTime = LocalDateTime.of(tempDateTime!!.year, tempDateTime!!.monthValue, tempDateTime!!.dayOfMonth, hourOfDay, minute,0)
        updateTracker(trackerViewModel.getTrackerById(navArgs.selectedTrackerId).tracker.apply {
            reminderDate = tempDateTime
            reminderOn = true
        })
        bind.reminderOnOffToggle.isChecked = true
        bind.reminderDateValue.text = tempDateTime?.let {
            NotificationUtil.setReminderForProducts(it,requireContext(),navArgs.selectedTrackerId)
            getString(
                R.string.date_string_with_month_name_and_time_2_lined,
                it.month.name.substring(0, 3).uppercase(),
                it.dayOfMonth,
                it.year,
                TimeConvertor.getTime(it.hour,it.minute,true)
            )
        }
    }
}