package com.plogging.ecorun.util.observer

import com.plogging.ecorun.event.ErrorHandler
import io.reactivex.subscribers.DisposableSubscriber

abstract class DefaultFlowableObserver<T>: DisposableSubscriber<T>(){
    override fun onError(e: Throwable) {
        ErrorHandler.handle(e)
    }
}