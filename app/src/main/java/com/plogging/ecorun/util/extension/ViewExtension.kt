package com.plogging.ecorun.util.extension

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import com.plogging.ecorun.R


fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
    if (v.layoutParams is MarginLayoutParams) {
        val p = v.layoutParams as MarginLayoutParams
        p.setMargins(l, t, r, b)
        v.requestLayout()
    }
}

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

fun View.setRedBorder() = this.setBackgroundResource(R.drawable.bg_round_border_red_4)
fun View.setGreenBorder() = this.setBackgroundResource(R.drawable.bg_round_border_green_4)
fun View.setGrayBorder() = this.setBackgroundResource(R.drawable.bg_round_border_gray_4)