package com.plogging.ecorun.ui.setting.password

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val repository: AuthRepository) :
    BaseViewModel() {
    val isNotEmptyCurrentPwSubject = PublishSubject.create<Boolean>()
    val isValidNewPwSubject = PublishSubject.create<Boolean>()
    val isMatchedPwSubject = PublishSubject.create<Boolean>()
    val buttonEnableSubject = PublishSubject.create<Boolean>()
    val oldPassword = MutableLiveData<String?>()
    val newPassword = MutableLiveData<String?>()
    val responseCode = MutableLiveData<Int>()

    fun changePwButtonEnable() = Observable.combineLatest(
        isNotEmptyCurrentPwSubject,
        isValidNewPwSubject,
        isMatchedPwSubject,
        { currentPw, newPw, matchedPw -> currentPw && newPw && matchedPw }
    ).subscribe { buttonEnableSubject.onNext(it) }
        .addTo(compositeDisposable)

    fun changePassword() {
        oldPassword.value ?: return
        newPassword.value ?: return
        repository.changePassword(oldPassword.value!!, newPassword.value!!)
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    responseCode.value = response.rc
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                }
            })
    }
}