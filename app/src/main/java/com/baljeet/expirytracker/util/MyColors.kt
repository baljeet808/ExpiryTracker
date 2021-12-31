package com.baljeet.expirytracker.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

object MyColors {
    fun getColorByAttr(context : Context, attr : Int, defaultColor : Int):Int{
          val typedValue = TypedValue()
        val theme : Resources.Theme = context.theme
        val gotIt : Boolean = theme.resolveAttribute(attr,typedValue,true)
        return if(gotIt) typedValue.data else context.getColor(defaultColor)
    }
}