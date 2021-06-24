package com.plogging.ecorun.util.observer

import com.plogging.ecorun.event.ErrorHandler
import io.reactivex.observers.DisposableSingleObserver

abstract class DefaultSingleObserver<T> : DisposableSingleObserver<T>() {
    override fun onError(e: Throwable) {
        ErrorHandler.handle(e)
    }
}
