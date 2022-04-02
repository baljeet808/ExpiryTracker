package com.baljeet.expirytracker.fragment.onboarding

import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentImpressiveTrackersBinding


class ImpressiveTrackers : Fragment() {
    private lateinit var bind: FragmentImpressiveTrackersBinding


    private lateinit var handler : Handler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentImpressiveTrackersBinding.inflate(inflater, container, false)
        return bind.root
    }


    var i = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        i = 1
        handler.post(trackerAnimation)
    }


    private var trackerAnimation = object : Runnable{
        override fun run() {
            bind.apply {
                itemProgressbar.apply {
                    val layerDrawable = progressDrawable as LayerDrawable
                    val progressDrawableClip = layerDrawable.getDrawable(1)

                    when (i) {
                        1->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                            trackingStatus.text = getString(R.string.fresh)
                            ObjectAnimator.ofInt(itemProgressbar, "progress", 30)
                                .setDuration(750).start()
                        }
                        2->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                            trackingStatus.text = getString(R.string.still_ok)
                            ObjectAnimator.ofInt(itemProgressbar, "progress", 60)
                                .setDuration(750).start()
                        }
                        3->{
                            progressDrawableClip.setTint(context.getColor(R.color.red_orange))
                            trackingStatus.text = requireContext().getString(R.string.expiring)
                            ObjectAnimator.ofInt(itemProgressbar, "progress", 90)
                                .setDuration(750).start()
                        }
                        4->{
                            progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                            trackingStatus.text = getString(R.string.expired)
                            ObjectAnimator.ofInt(itemProgressbar, "progress", 100)
                                .setDuration(750).start()
                        }
                    }
                    i++
                }
            }
            handler.postDelayed(this,750)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(trackerAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(trackerAnimation)
    }
    
}