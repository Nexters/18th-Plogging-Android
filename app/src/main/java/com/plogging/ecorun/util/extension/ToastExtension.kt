package com.plogging.ecorun.util.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Context.toast(@StringRes resId: Int) = toast(getString(resId))
fun Context.toast(@StringRes resId: Int, msg: String) = toast(getString(resId) + msg)
fun Context.toast(msg: String, @StringRes resId: Int) = toast(msg + getString(resId))