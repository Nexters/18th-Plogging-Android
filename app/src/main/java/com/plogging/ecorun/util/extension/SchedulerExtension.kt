package com.plogging.ecorun.util.extension

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.composeSchedulers(): Single<T> =
    compose {
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

fun <T> Observable<T>.composeSchedulers(): Observable<T> =
    compose {
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

fun <T> Flowable<T>.composeSchedulers(): Flowable<T> =
    compose {
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

fun Completable.composeSchedulers(): Completable =
    compose {
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }