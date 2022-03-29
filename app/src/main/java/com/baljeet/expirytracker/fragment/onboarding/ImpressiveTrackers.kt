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
                        .setDuration(750).start()
                }, 750)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                    trackingStatus.text = getString(R.string.still_ok)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 60)
                        .setDuration(750).start()
                }, 1500)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                    trackingStatus.text = getString(R.string.expiring)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 90)
                        .setDuration(750).start()
                }, 2250)


                Handler(Looper.getMainLooper()).postDelayed({
                    progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                    trackingStatus.text = getString(R.string.expired)
                    ObjectAnimator.ofInt(itemProgressbar, "progress", 100)
                        .setDuration(750).start()
                }, 3000)

            }


            return bind.root
        }
    }
}