package com.plogging.ecorun.util.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

// 공백 포함 특수문자 체크
private val PATTERN = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]")

fun String.isValidEmail() = when (this.isEmpty()) {
    true -> null
    false -> EMAIL_ADDRESS_PATTERN.matcher(this).matches()
}

fun String.isValidPassword(): Boolean? {
    var count = 0
    if (this.isEmpty()) return null
    if (this.any { it in 'A'..'Z' || it in 'a'..'z' }) count += 1
    if (this.any { it in '0'..'9' }) count += 1
    if (this.any { it in "[ !@#$%^&*(),.?\":{}|<>]" }) count += 1
    if (count >= 2 && this.length >= 8) return true
    return false
}

fun String?.isCustomType(): Boolean {
    return this?.contains(":") == true && this.split(":")[1] == "custom"
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Int.toSplitTime(): String {
    val hour = this / 3600
    val minute = (this % 3600) / 60
    val second = this % 60
    return (when {
        hour > 0 -> String.format("%01d:%02d:%02d", hour, minute, second)
        else -> String.format("%02d:%02d", minute, second)
    }).toString()
}

fun Float.meterToKilometer(): String {
    return String.format("%.2f", this / 1000f)
}

fun Float.meterToCalorie(): Double {
    return String.format("%.1f", this * 0.0625).toDouble()
}

