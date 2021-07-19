package com.plogging.ecorun.ui.main

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.event.Event
import com.plogging.ecorun.event.RxBus
import com.plogging.ecorun.util.constant.Constant.NAVER
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: AuthRepository) : BaseViewModel() {
    val showBottomNav = MutableLiveData(false)
    val networkError = MutableLiveData<Event>()
    val responseCode = MutableLiveData<Int>()
    val name = MutableLiveData("")
    val id = MutableLiveData<String>()
    val pw = MutableLiveData<String>()

    fun customSignIn() {
        id.value ?: return
        pw.value ?: return
        id.value = id.value!!.split(":")[0]
        val userInfo = User(userId = id.value!!, secretKey = pw.value)
        repository.signIn(userInfo).subscribe(object : DefaultSingleObserver<UserResponse>() {
            override fun onSuccess(response: UserResponse) {
                responseCode.value = 200
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                networkError.value = RxBus.bus.value
            }
        })
    }

    fun socialSignIn() {
        id.value ?: return
        val userInfo = User(userId = id.value!!, userName = "에코런")
        repository.socialSignIn(userInfo).subscribe(object : DefaultSingleObserver<UserResponse>() {
            override fun onSuccess(response: UserResponse) {
                responseCode.value = 200
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                networkError.value = RxBus.bus.value
            }
        })
    }

    fun naverSignIn(token: String) {
        repository.getNaverUser(token)
            .subscribe({
                id.value = it.naverUser.email + ":" + NAVER
                socialSignIn()
            }, {})
            .addTo(compositeDisposable)
    }
}