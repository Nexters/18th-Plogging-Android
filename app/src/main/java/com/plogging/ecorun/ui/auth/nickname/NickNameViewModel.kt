package com.plogging.ecorun.ui.auth.nickname

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@SuppressLint("CheckResult")
@HiltViewModel
class NickNameViewModel @Inject constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    val secretKey = MutableLiveData<String>()
    val userUri = MutableLiveData<String?>()
    val userName = MutableLiveData("")
    val userId = MutableLiveData("")
    val responseCode = MutableLiveData(0)

    fun saveUser() {
        val userId = userId.value ?: return
        val userName = userName.value ?: return
        val secretKey = secretKey.value ?: return
        val authUserInfo = User(userId = userId, userName = userName, secretKey = secretKey)
        authRepository.saveUserInfo(authUserInfo)
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    userUri.value = response.userImg
                    responseCode.value = response.rc
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    fun saveSocialUser() {
        val userId = userId.value ?: return
        val userName = userName.value ?: return
        val authUserInfo = User(userId = userId, userName = userName)
        authRepository.socialSignIn(authUserInfo)
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    userUri.value = response.userImg
                    responseCode.value = response.rc
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }
}