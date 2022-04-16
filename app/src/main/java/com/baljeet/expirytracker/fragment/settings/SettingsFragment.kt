package com.baljeet.expirytracker.fragment.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentSettingsBinding
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory


class SettingsFragment : Fragment() {

    private lateinit var bind: FragmentSettingsBinding

    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SharedPref.init(requireContext())
        bind = FragmentSettingsBinding.inflate(inflater, container, false)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.lightModeButton.translationX = 0f
        bind.lightModeButton.alpha = 0f
        bind.nightModeButton.translationX = 0f
        bind.nightModeButton.alpha = 0f
        if(SharedPref.isNightModeOn){
            bind.lightModeButton.visibility = View.VISIBLE
            showMoon()
        }else{
            bind.nightModeButton.visibility = View.VISIBLE
            showSun()
        }

        bind.nightModeButton.setOnClickListener {
            hideSun()
            Handler(Looper.getMainLooper()).postDelayed({
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedPref.isNightModeOn = true
            },1000)
        }
        bind.lightModeButton.setOnClickListener {
            hideMoon()
            Handler(Looper.getMainLooper()).postDelayed({
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SharedPref.isNightModeOn = false
            },1000)
        }

        bind.apply {

            proTextview.text = if(SharedPref.isUserAPro) getString(R.string.proud_pro_member) else getString(R.string.become_pro)

            qnaTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToFAQ())
            }
            proTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToBePro())
            }
            donateTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToDonateFragment())
            }
            feedbackTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToReviewsFragment())
            }
            widgetsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToWidgetMenu())
            }
            personalizationTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToPersonalize())
            }
            categoriesTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageCategories())
            }
            productsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageProducts())
            }
            notificationsTextview.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(SettingsFragmentDirections.actionSettingsFragmentToManageNotifications())
            }

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

    private fun showMoon(){
        bind.lightModeButton.apply {
            animate().translationX(300F).alpha(1f).setDuration(1000).
                    withEndAction {
                        translationX = 300f
                        alpha = 1f
                    }
        }
    }
    private fun hideSun(){
        bind.nightModeButton.apply {
            animate().translationX(0F).alpha(0f).setDuration(1000).
            withEndAction {
                translationX = 0f
                alpha = 0F
                showMoon()
            }
        }
    }
    private fun showSun(){
        bind.nightModeButton.apply {
            animate().translationX(-300F).alpha(1f).setDuration(1000).
            withEndAction {
                translationX = -300f
                alpha = 1f
            }
        }
    }
    private fun hideMoon(){
        bind.lightModeButton.apply {
            animate().translationX(0F).alpha(0f).setDuration(1000).
            withEndAction {
                translationX = 0f
                alpha = 0F
                showSun()
            }
        }
    }


}