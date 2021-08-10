package com.plogging.ecorun.ui.auth.signin

import android.util.Log
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
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: AuthRepository) :
    BaseViewModel() {
    val isSuccessSocialSignInSubject = PublishSubject.create<Boolean>()
    val isSuccessNaverSignInSubject = PublishSubject.create<Boolean>()
    val isSignInButtonEnableSubject = PublishSubject.create<Boolean>()
    val isSavedUserSubject = PublishSubject.create<Boolean>()
    val isValidIdSubject = PublishSubject.create<Boolean>()
    val isValidPwSubject = PublishSubject.create<Boolean>()
    val customSignInSuccess = MutableLiveData<Boolean>()
    val isSavedUser = MutableLiveData<Boolean>()
    val userType = MutableLiveData<String>()
    val responseCode = MutableLiveData<Int>()
    val name = MutableLiveData("")
    val uri = MutableLiveData<String>()
    val id = MutableLiveData<String>()
    val pw = MutableLiveData<String>()

    fun signIn() {
        id.value ?: return
        pw.value ?: return
        val userInfo = User(userId = id.value!!, secretKey = pw.value)
        repository.signIn(userInfo)
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    Log.e("userResponse", "${response.rc}, ${response.rcmsg}")
                    name.value = response.userName
                    uri.value = response.userImg!!
                    customSignInSuccess.value = true
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    customSignInSuccess.value = false
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    fun isSavedSocialUser() {
        id.value ?: return
        repository.isSavedUser(id.value.toString())
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    when (response.rc) {
                        200 -> isSavedUserSubject.onNext(false)    // DB에 없는 회원 닉네임 설정으로 이동
                        201 -> isSavedUserSubject.onNext(true)               // DB에 있는 회원 소셜 로그인
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
        id.value ?: return
        name.value ?: "에코런"
        repository.socialSignIn(User(userId = id.value!!, userName = name.value))
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    name.value = response.userName
                    uri.value = response.userImg!!
                    isSuccessSocialSignInSubject.onNext(true)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    isSuccessSocialSignInSubject.onNext(false)
                    if (e is HttpException) responseCode.value = e.code()
                    else responseCode.value = 500
                }
            })
    }

    fun naverSignIn(token: String) {
        repository.getNaverUser(token)
            .subscribe(object : DefaultSingleObserver<NaverUserResponse>() {
                override fun onSuccess(response: NaverUserResponse) {
                    id.value = response.naverUser.email + ":${NAVER}"
                    name.value = response.naverUser.name
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

    fun isSignInButtonEnable() {
        Observable.combineLatest(
            isValidIdSubject,
            isValidPwSubject,
            { isValidId, isValidPw -> isValidId && isValidPw }
        ).subscribe {
            isSignInButtonEnableSubject.onNext(it)
        }.addTo(compositeDisposable)
    }
}