package com.plogging.ecorun.di

import android.content.Context
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class NaverRetrofit

@Qualifier
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val BASE_URL = "https://eco-run.duckdns.org/"
    private const val TEST_URL = "http://192.168.219.101:20000/"
    private const val NAVER_URL = "https://openapi.naver.com/"

    @Provides
    fun okHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        val networkCheck = NetworkConnectionInterceptor(context)
        logger.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("cookie", SharedPreference.getCookie(context))
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(networkCheck)
            .addInterceptor(logger)
            .addInterceptor(CookieInterceptor(context))
            .build()
    }

    @NaverRetrofit
    @Provides
    @Singleton
    fun provideNaverService(@ApplicationContext context: Context): UserApiService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(interceptor)
            .addInterceptor(NetworkConnectionInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(NAVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitService(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @AuthRetrofit
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun ploggingApiService(retrofit: Retrofit): PloggingApiService =
        retrofit.create(PloggingApiService::class.java)

    @Provides
    @Singleton
    fun rankingApiService(retrofit: Retrofit): RankingApiService =
        retrofit.create(RankingApiService::class.java)
}