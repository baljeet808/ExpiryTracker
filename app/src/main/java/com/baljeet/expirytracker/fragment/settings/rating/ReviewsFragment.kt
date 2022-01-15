package com.baljeet.expirytracker.fragment.settings.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentReviewsBinding


class ReviewsFragment : Fragment() {

    private lateinit var bind : FragmentReviewsBinding
    private val viewModel : ReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentReviewsBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

            bind.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                when(rating){
                    1F ->{
                        expressionFace.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.heart_crying))
                        reviewCommentLayout.hint = getString(R.string._1_rating_hint)
                    }
                    2F->{
                        expressionFace.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.heart_fainted))
                        reviewCommentLayout.hint = getString(R.string._2_rating_hint)
                    }
                    3F->{
                        expressionFace.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.heart_sad))
                        reviewCommentLayout.hint = getString(R.string._3_rating_hint)
                    }
                    4F->{
                        expressionFace.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.heart_excited))
                        reviewCommentLayout.hint = requireContext().getString(R.string.your_thoughts)
                    }
                    5F->{
                        expressionFace.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.heart_happy))
                        reviewCommentLayout.hint = getString(R.string._5_rating_hint)
                    }
                }
                submitButton.isEnabled = true
            }

        }
        return bind.root
    }
}