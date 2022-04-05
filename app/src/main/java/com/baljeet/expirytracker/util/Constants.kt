package com.baljeet.expirytracker.util

import kotlinx.datetime.TimeZone


object Constants {

    //Status
    const val PRODUCT_STATUS_ALL ="All"
    const val PRODUCT_STATUS_FRESH ="Fresh"
    const val PRODUCT_STATUS_EXPIRED ="Expired"
    const val PRODUCT_STATUS_EXPIRING ="Expiring"

    //Timezone
    val TIMEZONE = TimeZone.currentSystemDefault()

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
    const val PDF_REWARDED_AD_ID = "ca-app-pub-9764366035521912/3783774676"
    const val DONATE_REWARDED_AD_ID = "ca-app-pub-9764366035521912/9586765699"
    const val SETTING_NATIVE_INLINE_AD_ID = "ca-app-pub-9764366035521912/4713712967"
    const val TRACKER_DETAIL_NATIVE_INLINE_AD_ID = "ca-app-pub-9764366035521912/7148304613"
    const val DASH_INTERSTITIAL_AD_ID = "ca-app-pub-9764366035521912/6605433038"


    //in app products ids
    const val CANDY_ID = "candy_purchase"
    const val CHOCOLATE_ID = "chocolate_purchase"
    const val COFFEE_ID = "coffee_purchase"
    const val BURGER_ID = "burger_purchase"
    const val DINNER_ID = "dinner_purchase"

    //subscriptions ids
    const val MONTHLY_SUBSCRIPTION = "pro_user"
    const val YEARLY_SUBSCRIPTION = "pro_user_lifetime"
    const val LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAychkXfLePi+X9Dl20Z8BCg2MmjZivszjzYaMGIDxNQ3Ad7OjnzXbaJikgU31mlDETvl8p0L0QFpBOzkfCZPuq8mCoMY9iR9G3LsR96KC7THpzLml/3eaDLfL1PMDezJvVRen0FmXdJK0fNsxkicTO0hdKh426slDmnuDstmNIHSFuIoAEw7TbKM7cKOHn+MV9Wv1p6soLXetdi32nTojAJPMK2lXOwdo0qwqBFdaOxaq0h28U5ryIApeXzKMDrNdFFk0G2ZIrNulsu4cCP3C34iS7WcpOzUMHYXGx8TOaM5b6FFIcSWQoziNAeVDCDDcKsRQFmxKRtMdikLGnjyNZQIDAQAB"

    const val MERCHANT_ID = "469387559570636006"

}