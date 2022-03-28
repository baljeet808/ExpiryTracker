package com.baljeet.expirytracker.fragment.settings.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.baljeet.expirytracker.databinding.FragmentWidgetMenuBinding
import com.baljeet.expirytracker.util.SharedPref
import com.baljeet.expirytracker.widgets.TrackersViewWidget
import com.baljeet.expirytracker.widgets.TrackingInfoWidget


class WidgetMenu : Fragment() {

    private lateinit var bind : FragmentWidgetMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind =  FragmentWidgetMenuBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }
            proWidgetOption.proIcon.isGone = SharedPref.isUserAPro
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            proWidgetOption.addButton.setOnClickListener {
                if(SharedPref.isUserAPro){
                     val appWidgetManager = AppWidgetManager.getInstance(requireContext())
                    val myProvider = ComponentName(requireContext(),TrackersViewWidget::class.java)
                    if(appWidgetManager.isRequestPinAppWidgetSupported){
                        appWidgetManager.requestPinAppWidget(myProvider,null,null)
                    }
                }else{
                    Navigation.findNavController(requireView()).navigate(WidgetMenuDirections.actionWidgetMenuToBePro())
                }
            }
            freeWidgetOption.addButton.setOnClickListener {
                val appWidgetManager = AppWidgetManager.getInstance(requireContext())
                val myProvider = ComponentName(requireContext(),TrackingInfoWidget::class.java)
                if(appWidgetManager.isRequestPinAppWidgetSupported){
                    appWidgetManager.requestPinAppWidget(myProvider,null,null)
                } 
            }
        }
    }
}