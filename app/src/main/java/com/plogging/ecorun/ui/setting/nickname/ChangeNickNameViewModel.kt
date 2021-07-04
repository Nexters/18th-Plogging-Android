package com.plogging.ecorun.ui.setting.nickname

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChangeNickNameViewModel @Inject constructor(private val repository: AuthRepository) :
    BaseViewModel() {

    val responseCode = MutableLiveData<Int>()
    val nickname = MutableLiveData<String>()

    fun changeNickname() {
        nickname.value ?: return
        repository.changeNickname(nickname.value!!)
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    nickname.value = response.userName!!
                    responseCode.value = response.rc
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                }
            })
    }
}