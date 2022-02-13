package com.baljeet.expirytracker.fragment.shared

import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.FragmentTrackerDetailsBinding
import com.baljeet.expirytracker.util.ImageConvertor
import kotlinx.datetime.*

class TrackerDetails : Fragment() {

    private lateinit var bind : FragmentTrackerDetailsBinding
    private val navArgs : TrackerDetailsArgs by navArgs()

    private lateinit var tracker : TrackerAndProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentTrackerDetailsBinding.inflate(inflater, container, false)
        bind.apply {

            backButton.setOnClickListener { activity?.onBackPressed() }

            with(navArgs.selectedTracker){
                productName.text = productAndCategoryAndImage.product.name
                categoryNameValue.text = productAndCategoryAndImage.categoryAndImage.category.categoryName
                if(tracker.usedWhileFresh){
                    statusText.text = getString(R.string.freshly_used)
                }
                if(tracker.usedWhileOk){
                    statusText.text = getString(R.string.good_condition)
                }
                if(tracker.usedNearExpiry){
                    statusText.text = getString(R.string.used_near_expiry)
                }
                if(tracker.gotExpired){
                    statusText.text =getString(R.string.expired)
                }
                expiryDateValue.text = tracker.expiryDate.date.toString()
                manufactureDateValue.text = tracker.mfgDate.date.toString()

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
                    }
                }


                val expiryDate = tracker.expiryDate
                val mfgDate = tracker.mfgDate

                val dateToday = Clock.System.now()

                val mfgInstant = mfgDate.toInstant(TimeZone.UTC)
                val expiryInstant = expiryDate.toInstant(TimeZone.UTC)

                val totalPeriod = mfgInstant.periodUntil(expiryInstant, TimeZone.UTC)
                val periodSpent = mfgInstant.periodUntil(dateToday, TimeZone.UTC)

                val totalHours = totalPeriod.days * 24 + totalPeriod.hours
                val spentHours = periodSpent.days * 24 + periodSpent.hours

                val periodLeft = dateToday.toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).periodUntil(expiryInstant, TimeZone.UTC)
                if(periodLeft.years>0) {
                    timeLeft.text = getString(
                        R.string.period_left_var,
                        periodLeft.years,
                        periodLeft.months,
                        periodLeft.days
                    )
                }else if(periodLeft.years == 0 && periodLeft.months> 0){
                    timeLeft.text = getString(
                        R.string.period_months_left_var,
                        periodLeft.months,
                        periodLeft.days
                    )
                }else if(periodLeft.years == 0 && periodLeft.months ==0 && periodLeft.days > 0){
                    timeLeft.text = getString(
                        R.string.period_days_left_var,
                        periodLeft.days
                    )
                }

                val progressValue =
                    (spentHours.toFloat() / totalHours.toFloat()) * 100
                itemProgressbar.apply {
                    val layerDrawable = progressDrawable as LayerDrawable
                    val progressDrawableClip = layerDrawable.getDrawable(1)
                    when {
                        progressValue >= 100->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                        }
                        progressValue >= 80 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                        }
                        progressValue < 80 && progressValue >= 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                        }
                        progressValue < 50 -> {
                            progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                        }
                    }
                    ObjectAnimator.ofInt(itemProgressbar,"progress",progressValue.toInt()).setDuration(500).start()
                }
            }
        }
        showAll()
        return bind.root
    }

    private fun showAll(){
        bind.apply {
            productName.isGone = false
            optionImage.isGone = false
            timeLeft.isGone = false
            
        }
    }

}