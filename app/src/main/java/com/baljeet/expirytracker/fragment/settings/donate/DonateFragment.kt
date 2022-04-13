package com.baljeet.expirytracker.fragment.settings.donate

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.billingclient.api.SkuDetails
import com.baljeet.expirytracker.databinding.FragmentDonateBinding
import com.baljeet.expirytracker.databinding.ThanksPopUpLayoutBinding
import com.baljeet.expirytracker.util.Constants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class DonateFragment : Fragment() {

    private lateinit var bind: FragmentDonateBinding

    private var mRewardedAd: RewardedAd? = null

    private val viewModel : DonateViewModel by activityViewModels()

    private lateinit var thanksDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDonateBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }
            adCard.setOnClickListener {
                showRewardAD()
            }
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(requireContext(),
                Constants.TEST_REWARDED_AD_ID, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd

                    }
                })

            viewModel.skuLiveList.observe(viewLifecycleOwner){
                 setUpPrices(it)
            }
            viewModel.purchaseComplete.observe(viewLifecycleOwner){
                if(it){
                    showThankYouPopup()
                }
            }
        }
        return bind.root
    }

    private fun showRewardAD() {
        if (mRewardedAd != null) {
            mRewardedAd?.show(requireActivity()) {
                showThankYouPopup()
            }
        } else {
            Log.d("MainActivity", "The rewarded ad wasn't ready yet.")
        }
    }



    private fun showThankYouPopup() {
        dialogBuilder = AlertDialog.Builder(requireContext())
        val popUpBinding = ThanksPopUpLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBuilder.setView(popUpBinding.root)
        thanksDialog = dialogBuilder.setCancelable(true).create()
        thanksDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        thanksDialog.show()
    }

    private fun setUpPrices(list : MutableList<SkuDetails>){
        bind.apply {
            for (sku in list){
                try {
                    when {
                        sku.title.contains("burger", true) -> {
                            burgerPrice.text = sku.price
                            burgerCard.setOnClickListener {
                                viewModel.purchaseItem(requireActivity(),sku)
                            }
                        }
                        sku.title.contains("candy", true) -> {
                            candyPrice.text = sku.price
                            candyCard.setOnClickListener {
                                viewModel.purchaseItem(requireActivity(),sku)
                            }
                        }
                        sku.title.contains("coffee", true) -> {
                            coffeePrice.text = sku.price
                            coffeeCard.setOnClickListener {
                                viewModel.purchaseItem(requireActivity(),sku)
                            }
                        }
                        sku.title.contains("chocolate", true) -> {
                            chocolatePrice.text = sku.price
                            chocolateCard.setOnClickListener {
                                viewModel.purchaseItem(requireActivity(),sku)
                            }
                        }
                        sku.title.contains("dinner", true) -> {
                            mealPrice.text = sku.price
                            mealCard.setOnClickListener {
                                viewModel.purchaseItem(requireActivity(),sku)
                            }
                        }
                        else -> Unit
                    }
                }catch (e: Exception){
                    Log.d("Log for - ","${e.message} \n\n\n\n\n ${e.printStackTrace()}")
                }
            }
        }
    }

}