package com.plogging.ecorun.event

import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject

object RxBus {
    val bus = BehaviorSubject.create<Event>()
    fun post(event: Event) = bus.onNext(event)
    fun observe() = bus.toFlowable(BackpressureStrategy.BUFFER)
}
