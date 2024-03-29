package com.baljeet.expirytracker.util
import android.content.Context
import android.content.SharedPreferences
import com.baljeet.expirytracker.fragment.settings.pro.SubscribeType

object SharedPref {
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var sharedPref: SharedPreferences



    fun init(context : Context) {
        sharedPref = context.getSharedPreferences("Expiry_tracker", MODE )
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }
    var isAlertEnabled: Boolean
        get() = sharedPref.getBoolean("isAlertEnabled",true)
        set(value) = sharedPref.edit {
            it.putBoolean("isAlertEnabled",value)
        }

    var isDailyAlertEnabled: Boolean
        get() = sharedPref.getBoolean("isDailyAlertEnabled",true)
        set(value) = sharedPref.edit {
            it.putBoolean("isDailyAlertEnabled",value)
        }
    var isDailyAlertSetup: Boolean
        get() = sharedPref.getBoolean("isDailyAlertSetup",false)
        set(value) = sharedPref.edit {
            it.putBoolean("isDailyAlertSetup",value)
        }

    var hasBeenSeeded: Boolean
        get() = sharedPref.getBoolean("hasBeenSeeded",false)
        set(value) = sharedPref.edit {
            it.putBoolean("hasBeenSeeded",value)
        }

    var hasOnboarded: Boolean
        get() = sharedPref.getBoolean("hasOnboarded",false)
        set(value) = sharedPref.edit {
            it.putBoolean("hasOnboarded",value)
        }

    var isNightModeOn: Boolean
        get() = sharedPref.getBoolean("isNightModeOn",false)

        set(value)  = sharedPref.edit{
            it.putBoolean("isNightModeOn",value)
        }

    var doNotAskBeforeDeletingCategory : Boolean
        get() = sharedPref.getBoolean("doNotAskBeforeDeletingCategory", false)

        set(value) = sharedPref.edit {
            it.putBoolean("doNotAskBeforeDeletingCategory",value)
        }

    var selectedStatus: String?
        get() = sharedPref.getString("selectedStatus","All")
        set(value) = sharedPref.edit { it.putString("selectedStatus",value) }

    var isUserAPro : Boolean
        get() = sharedPref.getBoolean("isUserAPro", false)
        set(value) = sharedPref.edit { it.putBoolean("isUserAPro", value) }

    var subscriptionIsMonthly : Boolean
        get() = sharedPref.getBoolean("subscribedMonthly", false)
        set(value) = sharedPref.edit{ it.putBoolean("subscribedMonthly", value)}

    var subscriptionIsYearly : Boolean
        get() = sharedPref.getBoolean("subscribedYearly", false)
        set(value) = sharedPref.edit{ it.putBoolean("subscribedYearly", value)}

    var themeName : String
    get() = sharedPref.getString("themeName",Constants.BLUE)!!
    set(value) = sharedPref.edit{it.putString("themeName",value)}

    var usingTab : Boolean
        get() = sharedPref.getBoolean("usingTab", false)
        set(value) = sharedPref.edit{ it.putBoolean("usingTab", value)}

    var reviewCompleted : Boolean
        get() = sharedPref.getBoolean("reviewCompleted", false)
        set(value) = sharedPref.edit{ it.putBoolean("reviewCompleted", value)}

}