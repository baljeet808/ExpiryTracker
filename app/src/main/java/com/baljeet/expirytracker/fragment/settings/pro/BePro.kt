package com.baljeet.expirytracker.fragment.settings.pro

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
class BePro : Fragment() {
    private val viewModel : BeProViewModel by activityViewModels()

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
            viewModel.purchaseCompleteFor.observe(viewLifecycleOwner){
                when (it) {
                    Constants.MONTHLY_SUBSCRIPTION -> {
                        onProductPurchased(Constants.MONTHLY_SUBSCRIPTION)
                    }
                    Constants.YEARLY_SUBSCRIPTION -> {
                        onProductPurchased(Constants.YEARLY_SUBSCRIPTION)
                    }
                    else -> {
                        Log.d("Log for - order id", it)
                    }
                }
            }
            viewModel.skuLiveList.observe(viewLifecycleOwner){
                setPrices(it)
            }

            settingsButton.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private fun setPrices(list : MutableList<com.android.billingclient.api.SkuDetails>){
        bind.apply {
            for (sku in list){
                when{
                    sku.title.contains("month",true)->{
                        monthlyPrice.text = sku.price
                        monthlyCard.setOnClickListener {
                            updateCards(false)
                            viewModel.purchaseItem(requireActivity(),sku)
                        }
                    }
                    sku.title.contains("year",true)->{
                        yearlyPrice.text = sku.price
                        yearlyCard.setOnClickListener {
                            updateCards(true)
                            viewModel.purchaseItem(requireActivity(),sku)
                        }
                    }
                }
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

    private fun onProductPurchased(productId: String) {
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

    private fun layoutChangesForPro(){
        bind.apply {
            if(SharedPref.subscriptionIsMonthly){
                yearlyCard.isGone = true

                monthlyCard.strokeWidth = 2

                heading.text = getString(R.string.proud_pro_member)
                helperHeading.text = getString(R.string.pro_member_heading)
                upgradeButton.isGone = true
                settingsButton.isGone = false
            }
            if(SharedPref.subscriptionIsYearly)
            {
                monthlyCard.isGone = true

                yearlyCard.strokeWidth = 2

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

            yearlyCard.strokeWidth = 2
            monthlyCard.strokeWidth = 0

            heading.text = getString(R.string.become_pro)
            helperHeading.text = getString(R.string.unlock_all_pro_features)
            upgradeButton.isGone = false
            settingsButton.isGone = true
        }
    }


    private fun showThankYouPopup() {
        dialogBuilder = AlertDialog.Builder(requireContext())
        val popUpBinding = ThanksPopUpLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        popUpBinding.infoIcon.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_crown_34))
        popUpBinding.thanksNote.text = getString(R.string.thanks_for_pro_upgrade)
        dialogBuilder.setView(popUpBinding.root)
        thanksDialog = dialogBuilder.setCancelable(true).create()
        thanksDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        thanksDialog.show()
    }


}