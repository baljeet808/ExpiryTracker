package com.baljeet.expirytracker.widgets

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.util.Constants
import kotlinx.coroutines.launch


class SplashViewModel(app: Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()
    private var billingClient: BillingClient

    val skuLiveList = MutableLiveData<MutableList<SkuDetails>>()

    val purchaseCompleteFor = MutableLiveData("")

    init {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK ){
                purchases?.let {
                    for(purchase in purchases){
                        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                            purchaseCompleteFor.postValue(purchase.skus.get(0))
                        }
                    }
                }
            }else{
                purchaseCompleteFor.postValue("")
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
                            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS
                            ) { billingResult, mutableList ->
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    mutableList.let {
                                        for (purchase in it) {
                                            Log.d("Log for - sku - ", purchase.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}