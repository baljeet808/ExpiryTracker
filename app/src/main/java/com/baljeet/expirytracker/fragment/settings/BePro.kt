package com.baljeet.expirytracker.fragment.settings

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.SkuDetails
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentBeProBinding
import com.baljeet.expirytracker.databinding.ThanksPopUpLayoutBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref


enum class SubscribeType{
    MONTH,YEAR
}
class BePro : Fragment(), BillingProcessor.IBillingHandler {


    private var billingProcess : BillingProcessor? = null
    private var purchaseInfo : PurchaseInfo? = null


    private lateinit var thanksDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder



    private lateinit var bind : FragmentBeProBinding

    private var subscribeFor : SubscribeType = SubscribeType.YEAR

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentBeProBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        getSubscriptionDetails()
        if (SharedPref.isUserAPro){
            layoutChangesForPro()
        }else{
            layoutChangesForNormalUser()
        }

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            backButton.setOnClickListener{
                activity?.onBackPressed()
            }

            monthlyCheckbox.setOnCheckedChangeListener { _, isChecked ->
                   updateCards(!isChecked)
                    yearlyCheckbox.isChecked = !isChecked
            }
            yearlyCheckbox.setOnCheckedChangeListener { _, isChecked ->
                updateCards(isChecked)
                monthlyCheckbox.isChecked = !isChecked
            }

            billingProcess =  BillingProcessor.newBillingProcessor(requireContext(),Constants.LICENSE_KEY,this@BePro)
            billingProcess?.initialize()

            upgradeButton.setOnClickListener {
                billingProcess?.subscribe(requireActivity(),if(subscribeFor == SubscribeType.MONTH) Constants.MONTHLY_SUBSCRIPTION else Constants.YEARLY_SUBSCRIPTION)
            }

            settingsButton.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }


    private fun updateCards(isYearly : Boolean){
            if(isYearly){
                bind.yearlyCard.strokeWidth = 2
                bind.monthlyCard.strokeWidth = 0
                subscribeFor = SubscribeType.YEAR
            }else{
                bind.yearlyCard.strokeWidth = 0
                bind.monthlyCard.strokeWidth = 2
                subscribeFor = SubscribeType.MONTH
            }
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        showThankYouPopup()
        if(productId == Constants.MONTHLY_SUBSCRIPTION){
            SharedPref.isUserAPro = true
            SharedPref.subscriptionIsMonthly = true
        }
        if(productId == Constants.YEARLY_SUBSCRIPTION){
            SharedPref.isUserAPro = true
            SharedPref.subscriptionIsYearly = true
        }
        if (SharedPref.isUserAPro){
            layoutChangesForPro()
        }else{
            layoutChangesForNormalUser()
        }
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
    }

    override fun onBillingInitialized() {
    }

    override fun onDestroy() {
        billingProcess?.release()
        super.onDestroy()
    }

    private fun getSubscriptionDetails(){
        if(billingProcess?.isConnected == true){
            billingProcess?.getSubscriptionListingDetailsAsync(Constants.MONTHLY_SUBSCRIPTION,object : BillingProcessor.ISkuDetailsResponseListener{
                override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                      products?.let {
                          if(it.any()){
                              it.firstOrNull()?.let { product ->
                                   bind.apply {
                                       monthlyPrice.text  = product.priceValue.toString().plus( " ${product.currency}")
                                   }
                              }
                          }
                      }
                }

                override fun onSkuDetailsError(error: String?) {
                }

            })
            billingProcess?.getSubscriptionListingDetailsAsync(Constants.YEARLY_SUBSCRIPTION,object : BillingProcessor.ISkuDetailsResponseListener{
                override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {
                    products?.let {
                        if(it.any()){
                            it.firstOrNull()?.let { product ->
                                bind.apply {
                                    yearlyPrice.text  = product.priceValue.toString().plus( " ${product.currency}")
                                }
                            }
                        }
                    }
                }

                override fun onSkuDetailsError(error: String?) {
                }

            })
        }
    }

    private fun layoutChangesForPro(){
        bind.apply {
            if(SharedPref.subscriptionIsMonthly){
                yearlyCard.isGone = true
                yearlyCheckbox.isGone = true
                monthlyCard.strokeWidth = 2
                monthlyCheckbox.isGone = true
                heading.text = getString(R.string.proud_pro_member)
                helperHeading.text = getString(R.string.pro_member_heading)
                upgradeButton.isGone = true
                settingsButton.isGone = false
            }
            if(SharedPref.subscriptionIsYearly)
            {
                monthlyCard.isGone = true
                yearlyCheckbox.isGone = true
                yearlyCard.strokeWidth = 2
                monthlyCheckbox.isGone = true
                heading.text = getString(R.string.proud_pro_member)
                helperHeading.text = getString(R.string.pro_member_heading)
                upgradeButton.isGone = true
                settingsButton.isGone = false
            }
        }
    }

    private fun layoutChangesForNormalUser(){
        bind.apply {
            monthlyCard.isGone = false
            yearlyCard.isGone = false
            yearlyCheckbox.isGone = false
            yearlyCard.strokeWidth = 2
            monthlyCard.strokeWidth = 0
            monthlyCheckbox.isGone = false
            yearlyCheckbox.isChecked = true
            monthlyCheckbox.isChecked = false
            heading.text = getString(R.string.become_pro)
            helperHeading.text = getString(R.string.unlock_all_pro_features)
            upgradeButton.isGone = false
            settingsButton.isGone = true
        }
    }


    private fun showThankYouPopup() {
        dialogBuilder = AlertDialog.Builder(requireContext())
        val popUpBinding = ThanksPopUpLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        popUpBinding.infoIcon.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.crown_512))
        popUpBinding.thanksNote.text = getString(R.string.thanks_for_pro_upgrade)
        dialogBuilder.setView(popUpBinding.root)
        thanksDialog = dialogBuilder.setCancelable(true).create()
        thanksDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        thanksDialog.show()
    }


}