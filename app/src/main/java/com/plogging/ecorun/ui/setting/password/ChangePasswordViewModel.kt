package com.plogging.ecorun.ui.setting.password

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.util.extension.isValidPassword
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val repository: AuthRepository) :
    BaseViewModel() {
    val clickableButton = MutableLiveData(false)
    val passwordCheck = MutableLiveData<Boolean>()
    val passwordMatch = MutableLiveData<Boolean>()
    val oldPassword = MutableLiveData<String?>()
    val newPassword = MutableLiveData<String?>()
    val responseCode = MutableLiveData<Int>()

    fun isPassword(password: String) {
        passwordCheck.value = password.isValidPassword()!! && password.length >= 8
    }

    fun isPasswordMatched() {
        passwordMatch.value = oldPassword.value == newPassword.value
    }

    fun isClickableButton() {
        clickableButton.value =
            !oldPassword.value.isNullOrBlank() &&
                    passwordCheck.value == true &&
                    passwordMatch.value == true
    }

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
                    if(e is HttpException) responseCode.value = e.code()
                }
            })
    }
}