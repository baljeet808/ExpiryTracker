package com.baljeet.expirytracker.fragment.onboarding

import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentImpressiveTrackersBinding


class ImpressiveTrackers : Fragment() {

    private lateinit var bind: FragmentImpressiveTrackersBinding

    private var animationFinished = false
    private var favMarked = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentImpressiveTrackersBinding.inflate(inflater, container, false)
        bind.apply {
            itemProgressbar.apply {
                val layerDrawable = progressDrawable as LayerDrawable
                val progressDrawableClip = layerDrawable.getDrawable(1)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                    trackingStatus.text = getString(R.string.fresh)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 30)
                        .setDuration(300).start()
                }, 200)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                    trackingStatus.text = getString(R.string.still_ok)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 50)
                        .setDuration(400).start()
                }, 600)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                    trackingStatus.text = getString(R.string.expiring)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 80)
                        .setDuration(400).start()
                }, 1000)


                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                    trackingStatus.text = getString(R.string.expired)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 100)
                        .setDuration(400).start()

                    coloredProgress.apply {
                        isGone = false
                        animate().alpha(0f).setDuration(500)
                            .withEndAction {
                                alpha = 1f
                                favAnimation()
                            }
                    }
                }, 1400)
                trackingStatus.setOnClickListener {
                    if (animationFinished) {
                        when (itemProgressbar.progress) {
                            100 -> {
                                progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                                trackingStatus.text = getString(R.string.fresh)
                                ObjectAnimator.ofInt(itemProgressbar, "progress", 30)
                                    .setDuration(300).start()
                            }
                            30 -> {
                                progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                                trackingStatus.text = getString(R.string.still_ok)
                                ObjectAnimator.ofInt(itemProgressbar, "progress", 50)
                                    .setDuration(300).start()
                            }
                            50 -> {
                                progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                                trackingStatus.text = getString(R.string.expiring)
                                ObjectAnimator.ofInt(itemProgressbar, "progress", 80)
                                    .setDuration(300).start()
                            }
                            80 -> {
                                progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                                trackingStatus.text = getString(R.string.expired)
                                ObjectAnimator.ofInt(itemProgressbar, "progress", 100)
                                    .setDuration(300).start()
                            }
                        }
                    }
                }
            }
            favoriteButton.setOnClickListener {
                if (animationFinished) {
                    favoriteButton.isChecked = !favMarked
                    favMarked = !favMarked
                }
            }
            optionCard.setOnClickListener {
                if (animationFinished) {
                    buttonsLayout.isGone = !buttonsLayout.isGone
                    expiringMonthAndDay.isGone = !buttonsLayout.isGone
                }
            }

        }
        return bind.root
    }



    private fun favAnimation() {
        bind.apply {
            Handler(Looper.getMainLooper()).postDelayed({
                favoriteButton.isChecked = true
            }, 400)
            Handler(Looper.getMainLooper()).postDelayed({
                favoriteButton.isChecked = false
            }, 800)
            Handler(Looper.getMainLooper()).postDelayed({
                favoriteButton.isChecked = true
            }, 1200)
            Handler(Looper.getMainLooper()).postDelayed({
                favoriteButton.isChecked = false
                markFav.isGone = false
                quickPanelAnimation()
            }, 1400)
        }
    }

    private fun quickPanelAnimation() {
        bind.apply {
            Handler(Looper.getMainLooper()).postDelayed({
                buttonsLayout.isGone = false
                expiringMonthAndDay.isGone = true
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed({
                buttonsLayout.isGone = true
                expiringMonthAndDay.isGone = false
            }, 1000)
            Handler(Looper.getMainLooper()).postDelayed({
                buttonsLayout.isGone = false
                expiringMonthAndDay.isGone = true
                deleteUsedMenu.isGone = false
                animationFinished = true
            }, 1500)
        }
    }

}