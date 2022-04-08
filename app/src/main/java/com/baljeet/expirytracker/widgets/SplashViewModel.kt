package com.baljeet.expirytracker.widgets

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.baljeet.expirytracker.util.SharedPref
import java.util.*


class SplashViewModel(app: Application) : AndroidViewModel(app) {

    val userInfoChecked  =MutableLiveData(false)

    fun checkForActivePurchases(context: Context) {
        val listener = PurchasesUpdatedListener { p0, p1 -> }
        val billingClient = BillingClient.newBuilder(context)
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
                        SharedPref.isUserAPro = true
                        SharedPref.subscriptionIsMonthly = true
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


}