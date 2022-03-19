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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentDonateBinding
import com.baljeet.expirytracker.databinding.ThanksPopUpLayoutBinding
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.MyColors
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


enum class DonationOptions {
    AD, CANDY, CHOCOLATE, COFFEE, BURGER, DINNER, EMPTY
}

class DonateFragment : Fragment() {

    private lateinit var bind: FragmentDonateBinding

    private val viewModel: DonateViewModel by viewModels()

    private var selectedOption: DonationOptions = DonationOptions.EMPTY

    private val optionSelected = MutableLiveData(false)

    private var mRewardedAd: RewardedAd? = null


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
                resetAllButtons()
                adLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectAd()
                adCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.AD
                optionSelected.postValue(true)
            }
            candyCard.setOnClickListener {
                resetAllButtons()
                candyLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectCandy()
                candyCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.CANDY
                optionSelected.postValue(true)
            }
            chocolateCard.setOnClickListener {
                resetAllButtons()
                chocolateLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectChocolate()
                chocolateCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.CHOCOLATE
                optionSelected.postValue(true)
            }
            coffeeCard.setOnClickListener {
                resetAllButtons()
                coffeeLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectCoffee()
                coffeeCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.COFFEE
                optionSelected.postValue(true)
            }
            burgerCard.setOnClickListener {
                resetAllButtons()
                burgerLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectBurger()
                burgerCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.BURGER
                optionSelected.postValue(true)
            }
            mealCard.setOnClickListener {
                resetAllButtons()
                mealLayout.setBackgroundColor(
                    MyColors.getColorByAttr(
                        requireContext(),
                        R.attr.card_background,
                        R.color.tab_background
                    )
                )
                viewModel.selectMeal()
                mealCheck.visibility = View.VISIBLE
                selectedOption = DonationOptions.DINNER
                optionSelected.postValue(true)
            }

            val adRequest = AdRequest.Builder().build()
            //TODO: remove test ad id before publishing
            RewardedAd.load(requireContext(),
                Constants.TEST_REWARDED_AD_ID, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd

                    }
                })


            val skuList = ArrayList<String>()

            val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->

            }


            val billingClient = BillingClient.newBuilder(requireContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()



            donateButton.setOnClickListener {
                when (selectedOption) {
                    DonationOptions.AD -> {
                        showRewardAD()
                    }
                    DonationOptions.CANDY -> {
                        skuList.clear()
                        skuList.add(Constants.CANDY_ID)
                    }
                    DonationOptions.CHOCOLATE -> {
                        skuList.clear()
                        skuList.add(Constants.CHOCOLATE_ID)
                    }
                    DonationOptions.COFFEE -> {
                        skuList.clear()
                        skuList.add(Constants.COFFEE_ID)
                    }
                    DonationOptions.BURGER -> {
                        skuList.clear()
                        skuList.add(Constants.BURGER_ID)
                    }
                    DonationOptions.DINNER -> {
                        skuList.clear()
                        skuList.add(Constants.DINNER_ID)
                    }
                    else -> Unit
                }
                if (selectedOption != DonationOptions.AD) {
                    billingClient.startConnection(object : BillingClientStateListener {
                        override fun onBillingServiceDisconnected() {

                        }

                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                val param = SkuDetailsParams.newBuilder()
                                param.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

                                billingClient.querySkuDetailsAsync(param.build()) { billingResults, skuDetailsList ->


                                    val flowPurchase = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList!![0])
                                        .build()

                                    val responseCode = billingClient.launchBillingFlow(
                                        requireActivity(),
                                        flowPurchase
                                    ).responseCode

                                }
                            }
                        }
                    })
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


    private fun resetAllButtons() {
        bind.apply {
            adLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )
            candyLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )
            chocolateLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )
            coffeeLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )
            burgerLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )
            mealLayout.setBackgroundColor(
                MyColors.getColorByAttr(
                    requireContext(),
                    R.attr.card_background,
                    R.color.tab_background
                )
            )

            adCheck.visibility = View.INVISIBLE
            candyCheck.visibility = View.INVISIBLE
            chocolateCheck.visibility = View.INVISIBLE
            coffeeCheck.visibility = View.INVISIBLE
            burgerCheck.visibility = View.INVISIBLE
            mealCheck.visibility = View.INVISIBLE

            donateButton.isEnabled = true
        }
    }

}