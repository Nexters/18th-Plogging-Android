package com.plogging.ecorun.base

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.plogging.ecorun.R
import com.plogging.ecorun.event.EventImpl
import com.plogging.ecorun.event.RxBus
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        setErrorHandler()
    }

    private fun setErrorHandler() = RxBus.observe()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when (it) {
                is EventImpl.NetworkErrorEvent -> toast(getString(R.string.error_network))
                is EventImpl.ServerErrorEvent -> toast(getString(R.string.error_server))
                is EventImpl.LogoutEvent -> toast(getString(R.string.fail_sign_in))
                is EventImpl.TimeoutErrorEvent -> toast(getString(R.string.retry_api))
            }
        }

    override fun onTerminate() {
        setErrorHandler().dispose()
        super.onTerminate()
    }
}