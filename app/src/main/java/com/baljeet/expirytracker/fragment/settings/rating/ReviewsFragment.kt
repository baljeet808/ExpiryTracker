package com.baljeet.expirytracker.fragment.settings.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.baljeet.expirytracker.databinding.FragmentReviewsBinding
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task


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
            val managerInfoTask: Task<ReviewInfo> = reviewManager.requestReviewFlow()
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
        bind.submitButton.setOnClickListener {
            startReviewFlow()
        }
    }

    private fun startReviewFlow(){
        reviewInfo?.let {
            val flow = reviewManager.launchReviewFlow(requireActivity(),it)
            flow.addOnCompleteListener {
                Toast.makeText(requireContext(), "review uploaded", Toast.LENGTH_SHORT).show()
            }
        }

    }

}