package com.baljeet.expirytracker.fragment.settings.donate

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.Constants
import kotlinx.coroutines.launch

class DonateViewModel(app : Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()

    private var billingClient: BillingClient

    val skuLiveList = MutableLiveData<MutableList<SkuDetails>>()

    val purchaseComplete = MutableLiveData(false)

    init {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK ){
                purchases?.let {
                    purchaseComplete.postValue(true)
                }
            }else{
                purchaseComplete.postValue(false)
            }
        }
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        connectToGooglePlayBilling()
    }
    fun purchaseItem(activity : Activity, sku: SkuDetails){
        billingClient.launchBillingFlow(activity,
            BillingFlowParams.newBuilder().setSkuDetails(sku)
                .build() )
    }


    private fun connectToGooglePlayBilling(){
        viewModelScope.launch {
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        connectToGooglePlayBilling()
                    }

                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK){
                            getProductDetails()
                        }
                    }
                }
            )
        }
    }


    private fun getProductDetails(){
        val skuList = ArrayList<String>()
        skuList.clear()
        skuList.add(Constants.CANDY_ID)
        skuList.add(Constants.CHOCOLATE_ID)
        skuList.add(Constants.COFFEE_ID)
        skuList.add(Constants.BURGER_ID)
        skuList.add(Constants.DINNER_ID)
        val productsDetailQuery =  SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(
            productsDetailQuery
        ) { result, list ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                list?.let {
                    skuLiveList.postValue(it)
                }
            }
        }
    }


}