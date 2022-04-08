package com.baljeet.expirytracker.widgets

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import java.util.*


class SplashViewModel(app: Application) : AndroidViewModel(app) {

    val userInfoChecked  =MutableLiveData(false)
    lateinit var billingClient: BillingClient

    fun checkForActivePurchases(context: Context) {
        val listener = PurchasesUpdatedListener { p0, p1 -> }
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(listener)
            .build()
        Log.d("Log for - ", "in billing client setup")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                billingClient.queryPurchaseHistoryAsync(
                    BillingClient.SkuType.SUBS
                ) { billingResult1: BillingResult, list: List<PurchaseHistoryRecord?>? ->
                    Log.d(
                        "billingprocess",
                        "purchasesResult.getPurchasesList():" + purchasesResult.purchasesList
                    )
                    if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK &&
                        Objects.requireNonNull(purchasesResult.purchasesList).isNotEmpty()
                    ) {
                        Log.d("Log for - purchases ", purchasesResult.purchasesList.toString())
                        for (purchase in purchasesResult.purchasesList!!){
                            if(purchase.isAcknowledged){
                                 if(purchase.skus[0].contains(Constants.MONTHLY_SUBSCRIPTION)){
                                     SharedPref.isUserAPro = true
                                     SharedPref.subscriptionIsMonthly = true
                                     SharedPref.subscriptionIsYearly = false
                                     userInfoChecked.postValue(true)
                                 }else if(purchase.skus[0].contains(Constants.YEARLY_SUBSCRIPTION)){
                                     SharedPref.isUserAPro = true
                                     SharedPref.subscriptionIsYearly = true
                                     SharedPref.subscriptionIsMonthly = false
                                     userInfoChecked.postValue(true)
                                 }
                            }else{
                                val ackParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken)
                                    .build()

                                val listen = ConsumeResponseListener { _, s ->
                                    Log.d("Log for - completed acknowledgment ","acknowledged")
                                    if(purchase.skus[0].contains(Constants.MONTHLY_SUBSCRIPTION)){
                                        SharedPref.isUserAPro = true
                                        SharedPref.subscriptionIsMonthly = true
                                        SharedPref.subscriptionIsYearly = false
                                        userInfoChecked.postValue(true)
                                    }else if(purchase.skus[0].contains(Constants.YEARLY_SUBSCRIPTION)){
                                        SharedPref.isUserAPro = true
                                        SharedPref.subscriptionIsYearly = true
                                        SharedPref.subscriptionIsMonthly = false
                                        userInfoChecked.postValue(true)
                                    }
                                }
                                billingClient.consumeAsync(ackParams,listen)
                            }
                        }

                        userInfoChecked.postValue(true)
                    } else {
                        Log.d("Log for - purchases", "no subs")
                        SharedPref.isUserAPro = false
                        userInfoChecked.postValue(true)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("billingprocess", "onBillingServiceDisconnected")
            }
        })
    }

    fun closeBillingConnection(){
        if(billingClient.isReady){
            billingClient.endConnection()
        }
    }


}