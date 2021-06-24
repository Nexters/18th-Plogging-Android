package com.plogging.ecorun.data.repository.auth

import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.data.response.UserResponse
import io.reactivex.Single
import okhttp3.MultipartBody

interface AuthRepository {
    fun saveUserInfo(user: User): Single<UserResponse>
    fun isSavedUser(userId: String): Single<BaseResponse>
    fun socialSignIn(user: User): Single<UserResponse>
    fun signIn(user: User): Single<UserResponse>
    fun changeNickname(nickname: String): Single<UserResponse>
    fun signOut(): Single<BaseResponse>
    fun deleteUser(): Single<BaseResponse>
    fun changePassword(oldPassword: String, newPassword: String): Single<BaseResponse>
    fun tempPassword(email: String): Single<BaseResponse>
    fun changeProfile(userImage: MultipartBody.Part): Single<UserResponse>
    fun getUserInfo(userId: String): Single<UserDetailResponse>
    fun getNaverUser(token: String): Single<NaverUserResponse>
}