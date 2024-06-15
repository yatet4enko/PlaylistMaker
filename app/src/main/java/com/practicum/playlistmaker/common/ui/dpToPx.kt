package com.practicum.playlistmaker.common.ui

import android.content.Context
import android.util.TypedValue

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}
