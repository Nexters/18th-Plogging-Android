package com.plogging.ecorun.network

import android.content.Context
import com.plogging.ecorun.data.local.SharedPreference
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpCookie

class CookieInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.headers("Set-Cookie").isNotEmpty()) {
            val httpCookie = HttpCookie.parse(response.header("Set-Cookie"))[0]
            SharedPreference.setUserCookie(context, httpCookie.toString())
        }
        return response
    }
}