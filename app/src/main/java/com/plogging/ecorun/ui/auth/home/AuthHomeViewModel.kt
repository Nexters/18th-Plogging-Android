package com.plogging.ecorun.ui.auth.home

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.util.constant.Constant.NAVER
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthHomeViewModel @Inject constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    val isSavedUser = MutableLiveData<Boolean>()
    val userType = MutableLiveData<String>() // 닉네임에 전달해 주기 위해 사용
    val userName = MutableLiveData<String>()
    val responseCode = MutableLiveData<Int>()
    val userId = MutableLiveData<String>()
    val uri = MutableLiveData<String>()

    fun isSavedSocialUser() {
        authRepository.isSavedUser(userId.value.toString())
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    when (response.rc) {
                        200 -> isSavedUser.value = false     // DB에 없는 회원 닉네임 설정으로 이동
                        201 -> socialSignIn()                // DB에 있는 회원 소셜 로그인
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    private fun socialSignIn() {
        userId.value ?: return
        userName.value ?: "에코런"
        authRepository.socialSignIn(User(userId = userId.value!!, userName = userName.value))
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    userName.value = response.userName!!
                    uri.value = response.userImg!!
                    isSavedUser.value = true
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    fun naverSignIn(token: String) {
        authRepository.getNaverUser(token)
            .subscribe(object : DefaultSingleObserver<NaverUserResponse>() {
                override fun onSuccess(response: NaverUserResponse) {
                    userId.value = response.naverUser.email + ":$NAVER"
                    userType.value = NAVER
                    userName.value = response.naverUser.name
                    isSavedSocialUser()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }
}
