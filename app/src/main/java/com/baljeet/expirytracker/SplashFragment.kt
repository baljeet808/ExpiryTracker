package com.baljeet.expirytracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class SplashFragment : Fragment(), BillingProcessor.IBillingHandler {

    private lateinit var cardLayout : MaterialCardView


    private var billingProcess : BillingProcessor? = null
    private var monthlyPurchaseInfo : PurchaseInfo? = null
    private var yearlyPurchaseInfo : PurchaseInfo? = null

    private val monthlySubscriptionChecked = MutableLiveData(false)
    private val yearlySubscriptionChecked = MutableLiveData(false)


    private var subscriptionsConfirmed = MediatorLiveData<Boolean>().apply{
        this.value = false
        addSource(monthlySubscriptionChecked){
            this.value = it && yearlySubscriptionChecked.value!!
        }
        addSource(yearlySubscriptionChecked){
            this.value = it && monthlySubscriptionChecked.value!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedPref.init(requireContext())
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        cardLayout = view.findViewById(R.id.card_layout)


        subscriptionsConfirmed.observe(viewLifecycleOwner){
            if (it){
                moveToMain()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(BillingProcessor.isIabServiceAvailable(requireContext())){
            billingProcess =  BillingProcessor.newBillingProcessor(requireContext(),Constants.LICENSE_KEY ,Constants.MERCHANT_ID,this)
            billingProcess?.initialize()
        }else{
            SharedPref.isUserAPro = false
            SharedPref.subscriptionIsMonthly = false
            SharedPref.subscriptionIsYearly = false
            monthlySubscriptionChecked.postValue(true)
            yearlySubscriptionChecked.postValue(true)
        }

    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Toast.makeText(requireContext(),"Error while checking billing info,\nplease check network connection and restart", Toast.LENGTH_SHORT).show()
        SharedPref.isUserAPro = false
        SharedPref.subscriptionIsMonthly = false
        SharedPref.subscriptionIsYearly = false
        monthlySubscriptionChecked.postValue(true)
        yearlySubscriptionChecked.postValue(true)
    }

    override fun onBillingInitialized() {
        if(billingProcess?.isConnected == true) {
            monthlyPurchaseInfo = billingProcess?.getSubscriptionPurchaseInfo(Constants.MONTHLY_SUBSCRIPTION)
            monthlyPurchaseInfo?.let {
                SharedPref.isUserAPro = it.purchaseData.autoRenewing
                SharedPref.subscriptionIsMonthly = it.purchaseData.autoRenewing
                monthlySubscriptionChecked.postValue(true)
            } ?: kotlin.run {
                SharedPref.isUserAPro = false
                SharedPref.subscriptionIsMonthly = false
                monthlySubscriptionChecked.postValue(true)
            }
            yearlyPurchaseInfo = billingProcess?.getSubscriptionPurchaseInfo(Constants.YEARLY_SUBSCRIPTION)
            yearlyPurchaseInfo?.let {
                SharedPref.isUserAPro = it.purchaseData.autoRenewing
                SharedPref.subscriptionIsYearly = it.purchaseData.autoRenewing
                yearlySubscriptionChecked.postValue(true)
            } ?: kotlin.run {
                SharedPref.isUserAPro = false
                SharedPref.subscriptionIsYearly = false
                yearlySubscriptionChecked.postValue(true)
            }
        }else{
            SharedPref.isUserAPro = false
            SharedPref.subscriptionIsMonthly = false
            SharedPref.subscriptionIsYearly = false
            monthlySubscriptionChecked.postValue(true)
            yearlySubscriptionChecked.postValue(true)
        }
    }

    private fun moveToMain(){
        Handler(Looper.getMainLooper()).postDelayed({
            if(SharedPref.hasOnboarded) {
                (activity as OnBoarding).moveToMainActivity()
            }else {
                Navigation.findNavController(requireView()).navigate(SplashFragmentDirections.actionSplashFragmentToImpressiveTrackers())
            }
        },1000)

    }


    override fun onDestroy() {
        billingProcess?.release()
        super.onDestroy()
    }
}