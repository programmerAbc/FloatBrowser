package com.practice.floatbrowser

import android.content.res.Resources

fun dp2px(dp: Float): Int {
    return Math.ceil((Resources.getSystem().displayMetrics.density * dp).toDouble()).toInt()
}