package com.plogging.ecorun.ui.running.active

import com.plogging.ecorun.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RunningViewModel : BaseViewModel() {
    val runningState = BehaviorSubject.createDefault(RunningState.INITIAL)
    val distanceMeter = BehaviorSubject.createDefault(0.001f)
    val runningSeconds = BehaviorSubject.createDefault(1)
    val lastDistance = PublishSubject.create<Float>()
    val readySeconds = PublishSubject.create<Int>()

    fun readyTimer() =
        Observable.intervalRange(0, 4, 0, 1, TimeUnit.SECONDS)
            .map(Long::toInt)
            .doOnComplete { runningState.onNext(RunningState.START) }
            .subscribe({ readySeconds.onNext(3 - it) }, {})
            .addTo(compositeDisposable)

    fun runningTimer() =
        Observable.interval(1, TimeUnit.SECONDS)
            .map(Long::toInt)
            .filter { runningState.value == RunningState.ACTIVE }
            .subscribe({ runningSeconds.onNext(runningSeconds.value?.plus(1)!!) }, {})
            .addTo(compositeDisposable)

    fun getDistance() =
        lastDistance.subscribeOn(Schedulers.computation())
            .subscribe({ distanceMeter.onNext(it + distanceMeter.value!!) }, {})
            .addTo(compositeDisposable)

    enum class RunningState {
        INITIAL, START, ACTIVE, PAUSE, STOP
    }
}