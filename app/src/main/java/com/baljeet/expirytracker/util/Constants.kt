package com.baljeet.expirytracker.util

import kotlinx.datetime.TimeZone


object Constants {

    //Status
    const val PRODUCT_STATUS_ALL ="All"
    const val PRODUCT_STATUS_FRESH ="Fresh"
    const val PRODUCT_STATUS_EXPIRED ="Expired"
    const val PRODUCT_STATUS_EXPIRING ="Expiring"

    //Timezone
    val TIMEZONE = TimeZone.UTC

    //Favourite options
    const val SHOW_ALL = 1
    const val SHOW_ONLY_FAVOURITE = 2
    const val SHOW_ONLY_NON_FAVOURITE = 3

    //Periods types
    const val PERIOD_DAILY =1
    const val PERIOD_WEEKLY =2
    const val PERIOD_MONTHLY =3
    const val PERIOD_YEARLY =4

    //Charts for
    const val TOTAL_TRACKED =1
    const val USED_NEAR_EXPIRY =2
    const val USED_WHEN_FRESH = 3
    const val EXPIRED = 4

    //Donation amounts
    const val AD = 0.00
    const val CANDY = 1.29
    const val CHOCOLATE = 2.50
    const val COFFEE = 6.88
    const val BURGER = 12.07
    const val MEAL = 29.99

    //Theme names
    const val GREEN  = "GREEN"
    const val BLUE  = "BLUE"
    const val PEACH  = "PEACH"
    const val YELLOW  = "YELLOW"
    const val BLACK  = "BLACK"
    const val WHITE  = "WHITE"
    const val PURPLE  = "PURPLE"
    const val PINK  = "PINK"
    const val TEAL  = "TEAL"

    //Ad-Unit ids
    const val REWARDED_AD_ID = "ca-app-pub-1506373039840442/1726823090"
    const val NATIVE_INLINE_AD_ID = "ca-app-pub-1506373039840442/4902245808"

    //Test Ad-Unit ids
    const val TEST_REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_NATIVE_INLINE_AD_ID = "ca-app-pub-3940256099942544/2247696110"

    //in app products ids
    const val CANDY_ID = "candy_purchase"
    const val CHOCOLATE_ID = "chocolate_purchase"
    const val COFFEE_ID = "coffee_purchase"
    const val BURGER_ID = "burger_purchase"
    const val DINNER_ID = "dinner_purchase"

    //subscriptions ids
    const val MONTHLY_SUBSCRIPTION = "pro_user"
    const val YEARLY_SUBSCRIPTION = "pro_user_lifetime"


}