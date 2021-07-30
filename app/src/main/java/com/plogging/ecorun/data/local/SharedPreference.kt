package com.plogging.ecorun.data.local

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.plogging.ecorun.R


object SharedPreference {
    private const val ACCOUNT = "my account"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_CONFIRM_FIRST_USER = "isFirstUser"
    private const val KEY_LATITUDE = "lat"
    private const val KEY_LONGITUDE = "lon"
    private const val KEY_USER_IMAGE = "userImg"
    private const val KEY_TRACKING_PREFERENCE = "isSaved"
    private const val KEY_USER_COOKIE = "cookie"
    private const val KEY_PERMIT_LOCATION = "location"

    fun setUserName(ctx: Context, name: String) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    //type이 뒤에 붙어있다.
    fun setUserEmail(ctx: Context, email: String) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun setFirstUsed(ctx: Context) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putBoolean(KEY_CONFIRM_FIRST_USER, false)
            .apply()
    }

    fun setLatitude(ctx: Context, lat: Float) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putFloat(KEY_LATITUDE, lat)
            .apply()
    }

    fun setLongitude(ctx: Context, lon: Float) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putFloat(KEY_LONGITUDE, lon)
            .apply()
    }

    fun setUserImage(ctx: Context, uri: Uri?) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putString(KEY_USER_IMAGE, uri.toString())
            .apply()
    }

    fun setUserCookie(ctx: Context, cookie: String) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putString(KEY_USER_COOKIE, cookie)
            .apply()
    }

    fun saveLocationTrackingPref(ctx: Context, state: Boolean) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putBoolean(KEY_TRACKING_PREFERENCE, state)
            .apply()
    }

    fun setPermitLocation(ctx: Context, state: Boolean) {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        preference.edit()
            .putBoolean(KEY_PERMIT_LOCATION, state)
            .apply()
    }

    fun getUserName(ctx: Context): String {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getString(KEY_USER_NAME, "")!!
    }

    fun getUserEmail(ctx: Context): String {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getString(KEY_USER_EMAIL, "")!!
    }

    fun getIsFirstUser(ctx: Context): Boolean {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getBoolean(KEY_CONFIRM_FIRST_USER, true)
    }

    fun getUserImage(ctx: Context): Uri? {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return Uri.parse(
            preference.getString(
                KEY_USER_IMAGE,
                "android.resource://com.plogging.ecorun/" + R.drawable.ic_mark_basic_plogging
            )
        )
    }

    fun getCookie(ctx: Context): String {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getString(KEY_USER_COOKIE, "")!!
    }

    fun getLatitude(ctx: Context): Float {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getFloat(KEY_LATITUDE, 37.5665.toFloat())
    }

    fun getLongitude(ctx: Context): Float {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getFloat(KEY_LONGITUDE, 126.9780.toFloat())
    }

    fun getLocationTrackingPref(ctx: Context): Boolean {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getBoolean(KEY_TRACKING_PREFERENCE, false)
    }

    fun getPermitLocation(ctx: Context): Boolean {
        val preference: SharedPreferences = ctx.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)
        return preference.getBoolean(KEY_PERMIT_LOCATION, false)
    }
}