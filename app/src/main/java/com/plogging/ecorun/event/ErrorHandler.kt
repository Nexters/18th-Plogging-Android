package com.plogging.ecorun.event

import com.plogging.ecorun.network.NetworkConnectionInterceptor
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

object ErrorHandler {
    fun handle(t: Throwable) {
        if (t is HttpException) {
            // 401은 토큰이 만료되었다고 약속을 했다
            if (t.code() == 401) {
                RxBus.post(EventImpl.LogoutEvent())
            }
        } else if (t is NetworkConnectionInterceptor.NoConnectivityException) {
            RxBus.post(EventImpl.NetworkErrorEvent())
        } else if (t is ConnectException) {
            RxBus.post(EventImpl.ServerErrorEvent())
        } else if (t is SocketTimeoutException)
            RxBus.post(EventImpl.TimeoutErrorEvent())
    }
}