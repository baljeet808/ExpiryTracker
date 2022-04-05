package com.baljeet.expirytracker.fragment.settings.faq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.databinding.FragmentFAQBinding
import com.baljeet.expirytracker.listAdapters.QnAAdapter
import com.baljeet.expirytracker.model.QnA


class FAQ : Fragment() {

    private lateinit var bind: FragmentFAQBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentFAQBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }
            feedbackButton.setOnClickListener{
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")

                val deviceName = android.os.Build.MODEL
                val apiVersion = android.os.Build.VERSION.SDK_INT
                val appVersion = requireContext().packageManager.getPackageInfo(requireContext().packageName,0).versionName

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("88KBDev@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[ExpiryTracker]-Feedback (DeviceName-${deviceName}, AppVersion-${appVersion}),ApiCode-${apiVersion}")
                emailIntent.selector = selectorIntent

                startActivity(Intent.createChooser(emailIntent, "Send email..."))

            }
            questionAnswersRecycler.layoutManager = LinearLayoutManager(requireContext())
            questionAnswersRecycler.adapter = QnAAdapter(getQnAList(),requireContext())
        }
        return bind.root
    }


    private fun getQnAList():ArrayList<QnA>{
        val qnAList = ArrayList<QnA>()
        qnAList.add(QnA(
            question = "Pro version is still not activated after purchase.",
            answer = "This situation may be due to a delay in the synchronization of google's purchase information. Please be patient! You can also try as the following:\n\n1. Check the purchase status of the app on google play.\n\n2. Update google play.\n\n3. Restart the app\n\n4. Change network, etc.\n"
        ))
        qnAList.add(QnA(
            question = "Reminder notifications are not showing.",
            answer = "If you did not receive notifications, it may be because you did not give authority to receive notifications from the app. \n\n1. Please try giving permissions to Expiry tracker from you phone or tablet settings.\n\n2. Your phone might be in power saving mode, which puts the active reminders to sleep in your phone or tablet. Please try setting off the power saving mode.\n"
        ))
        qnAList.add(QnA(
            question = "How to add widgets?",
            answer = "Please follow the instructions below : \n\n1. Go back to 'settings' page in app and open widget screens.\n\n2. Tap on 'Add' button for desired widget there. Which will show a pop-up window. Then click Automatically add.\n\nVia home screen : -\n\n1. Long press the blank area on home screen\n\n2. Click on the Widgets button that appear on main screen.\n\n3. Find Expiry tracker app there.\n\n4. Long press a widget and drag it to the position you want.\n"
        ))
        qnAList.add(QnA(
            question = "How to switch to dark mode?",
            answer = "To switch between Dark and Light theme color do as follow:\n\n1. Go back to 'settings' page in app\n\n2. Tap on Sun or Moon icon on the top of screen.\n"
        ))
        qnAList.add(QnA(
            question = "Other issues",
            answer = "If you have any other questions or suggestions, you can click on Email button in top right corner and send us an email, thank you!\n"
        ))
        return qnAList
    }


}