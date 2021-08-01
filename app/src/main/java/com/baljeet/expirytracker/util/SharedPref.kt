package com.baljeet.expirytracker.util
import android.content.Context
import android.content.SharedPreferences
import java.util.jar.Attributes

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
        get() = sharedPref.getBoolean("isAlertEnabled",false)
        set(value) = sharedPref.edit {
            it.putBoolean("isAlertEnabled",value)
        }

    var isNightModeOn: Boolean
        get() = sharedPref.getBoolean("isNightModeOn",false)

        set(value)  = sharedPref.edit{
            it.putBoolean("isNightModeOn",value)
        }
}