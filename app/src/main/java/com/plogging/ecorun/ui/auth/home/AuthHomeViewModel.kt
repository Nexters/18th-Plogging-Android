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
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthHomeViewModel @Inject constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    val isSuccessSocialSignInSubject = PublishSubject.create<Int>()
    val isSuccessNaverSignInSubject = PublishSubject.create<Boolean>()
    val isSavedUserSubject = PublishSubject.create<Boolean>()
    val userType = MutableLiveData<String>() // 닉네임에 전달해 주기 위해 사용
    val userName = MutableLiveData<String>()
    val responseCode = MutableLiveData<Int>()
    val userId = MutableLiveData<String>()
    val uri = MutableLiveData<String>()
    val user = MutableLiveData<User>()

    fun isSavedSocialUser() {
        authRepository.isSavedUser(userId.value.toString())
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    when (response.rc) {
                        200 -> isSavedUserSubject.onNext(false)     // DB에 없는 회원 닉네임 설정으로 이동
                        201 -> isSavedUserSubject.onNext(true)      // DB에 있는 회원 소셜 로그인
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    fun socialSignIn() {
        userId.value ?: return
        if (userName.value.isNullOrBlank()) userName.value = "에코런"
        authRepository.socialSignIn(User(userId = userId.value!!, userName = userName.value))
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    userName.value = response.userName!!
                    uri.value = response.userImg!!
                    isSuccessSocialSignInSubject.onNext(response.rc)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    isSuccessSocialSignInSubject.onNext(500)
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
                    isSuccessNaverSignInSubject.onNext(true)
                    isSavedSocialUser()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    isSuccessNaverSignInSubject.onNext(false)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }
}
