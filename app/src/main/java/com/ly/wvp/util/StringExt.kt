package com.ly.wvp.util

import android.content.Context
import android.widget.Toast
import com.ly.wvp.R

fun Int.shortToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.shortToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun Int.toString(context: Context): String{
    return context.resources.getString(this)
}