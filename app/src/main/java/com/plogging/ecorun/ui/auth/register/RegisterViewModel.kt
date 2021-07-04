package com.plogging.ecorun.ui.auth.register

import com.plogging.ecorun.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

class RegisterViewModel : BaseViewModel() {
    val isValidPwSubject = PublishSubject.create<Boolean>()
    val isValidIdSubject = PublishSubject.create<Boolean>()
    val isMatchedPwSubject = PublishSubject.create<Boolean>()
    val buttonEnableSubject = PublishSubject.create<Boolean>()

    fun resisterButtonEnable() {
        Observable.combineLatest(
            isValidIdSubject, isValidPwSubject, isMatchedPwSubject,
            { id, pw, match -> id && pw && match }
        )
            .subscribe { buttonEnableSubject.onNext(it) }
            .addTo(compositeDisposable)
    }
}