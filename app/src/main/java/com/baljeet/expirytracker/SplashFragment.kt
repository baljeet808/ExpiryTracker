package com.baljeet.expirytracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.baljeet.expirytracker.widgets.SplashViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class SplashFragment : Fragment() {

    private lateinit var cardLayout : MaterialCardView

    private val viewModel : SplashViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedPref.init(requireContext())
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        cardLayout = view.findViewById(R.id.card_layout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkForActivePurchases(requireContext())
        viewModel.userInfoChecked.observe(viewLifecycleOwner){
            if(it){
                moveToMain()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }



    private fun moveToMain(){
        Handler(Looper.getMainLooper()).postDelayed({
            if(SharedPref.hasOnboarded) {
                (activity as OnBoarding).moveToMainActivity()
            }else {
                Navigation.findNavController(requireView()).navigate(SplashFragmentDirections.actionSplashFragmentToWelcomePage())
            }
        },1000)

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}