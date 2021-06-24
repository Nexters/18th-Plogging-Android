package com.plogging.ecorun.data.repository.auth

import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.remote.auth.AuthDataSource
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.data.response.UserResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authDataSource: AuthDataSource) :
    AuthRepository {

    override fun saveUserInfo(user: User): Single<UserResponse> =
        authDataSource.saveUserInfo(user)

    override fun isSavedUser(userId: String): Single<BaseResponse> =
        authDataSource.isSavedUser(userId)

    override fun socialSignIn(user: User): Single<UserResponse> =
        authDataSource.socialSignIn(user)

    override fun signIn(user: User): Single<UserResponse> =
        authDataSource.signIn(user)

    override fun changeNickname(nickname: String): Single<UserResponse> =
        authDataSource.changeNickname(nickname)

    override fun signOut(): Single<BaseResponse> =
        authDataSource.signOut()

    override fun deleteUser(): Single<BaseResponse> =
        authDataSource.deleteUser()

    override fun changePassword(oldPassword: String, newPassword: String): Single<BaseResponse> =
        authDataSource.changePassword(oldPassword, newPassword)

    override fun tempPassword(email: String): Single<BaseResponse> =
        authDataSource.tempPassword(email)

    override fun changeProfile(userImage: MultipartBody.Part): Single<UserResponse> =
        authDataSource.changeProfile(userImage)

    override fun getUserInfo(userId: String): Single<UserDetailResponse> =
        authDataSource.getUserInfo(userId)

    override fun getNaverUser(token: String): Single<NaverUserResponse> =
        authDataSource.getNaverUser(token)
}