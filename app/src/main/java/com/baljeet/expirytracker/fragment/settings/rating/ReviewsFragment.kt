package com.baljeet.expirytracker.fragment.settings.rating

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.databinding.FragmentReviewsBinding
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory


class ReviewsFragment : Fragment() {

    private lateinit var bind: FragmentReviewsBinding

    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentReviewsBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

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
            return bind.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.reviewButton.setOnClickListener {
            startReviewFlow()
        }
        bind.submitButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"Expiry Tracker")
            intent.putExtra(Intent.EXTRA_TEXT,"\nLet me recommend this android application to you\n\n Expiry Tracker \n\n https://play.google.com/store/apps/details?id=com.baljeet.expirytracker")
            val chooser = Intent.createChooser(intent,"Share using....")
            startActivity(chooser)
        }
    }

    private fun startReviewFlow(){
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

}