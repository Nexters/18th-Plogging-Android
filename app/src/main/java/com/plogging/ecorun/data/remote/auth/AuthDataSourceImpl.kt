package com.plogging.ecorun.data.remote.auth

import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.di.AuthRetrofit
import com.plogging.ecorun.di.NaverRetrofit
import com.plogging.ecorun.network.UserApiService
import com.plogging.ecorun.util.extension.composeSchedulers
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    @AuthRetrofit private var userApiService: UserApiService,
    @NaverRetrofit private var naverApiService: UserApiService
) : AuthDataSource {

    override fun saveUserInfo(user: User): Single<UserResponse> =
        userApiService.saveUserInfo(user).composeSchedulers()

    override fun isSavedUser(userId: String): Single<BaseResponse> =
        userApiService.isSavedUser(userId).composeSchedulers()

    override fun socialSignIn(user: User): Single<UserResponse> =
        userApiService.socialSignIn(user).composeSchedulers()

    override fun signIn(user: User): Single<UserResponse> =
        userApiService.signIn(user).composeSchedulers()

    override fun changeNickname(nickname: String): Single<UserResponse> =
        userApiService.changeNickname(nickname).composeSchedulers()

    override fun signOut(): Single<BaseResponse> =
        userApiService.signOut().composeSchedulers()

    override fun deleteUser(): Single<BaseResponse> =
        userApiService.deleteUser().composeSchedulers()

    override fun changePassword(oldPassword: String, newPassword: String): Single<BaseResponse> =
        userApiService.changePassword(oldPassword, newPassword).composeSchedulers()

    override fun tempPassword(email: String): Single<BaseResponse> =
        userApiService.tempPassword(email).composeSchedulers()

    override fun changeProfile(userImage: MultipartBody.Part): Single<UserResponse> =
        userApiService.changeProfile(userImage).composeSchedulers()

    override fun getUserInfo(userId: String): Single<UserDetailResponse> =
        userApiService.getUserInfo(userId).composeSchedulers()

    override fun getNaverUser(token: String): Single<NaverUserResponse> =
        naverApiService.getNaverUser("Bearer $token").composeSchedulers()
}