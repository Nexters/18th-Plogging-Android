package com.plogging.ecorun.network

import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.data.response.UserResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface UserApiService {

    @POST("/user/sign-in")
    fun signIn(@Body user: User): Single<UserResponse>

    @POST("/user/social")
    fun socialSignIn(@Body user: User): Single<UserResponse>

    @POST("/user")
    fun saveUserInfo(@Body user: User): Single<UserResponse>

    @DELETE("/user")
    fun deleteUser(): Single<BaseResponse>

    @FormUrlEncoded
    @POST("/user/check")
    fun isSavedUser(@Field("userId") userId: String): Single<BaseResponse>

    @GET("/user/{id}")
    fun getUserInfo(@Path("id") id: String): Single<UserDetailResponse>

    @FormUrlEncoded
    @PUT("/user/name")
    fun changeNickname(@Field("userName") userName: String): Single<UserResponse>

    @Multipart
    @PUT("/user/image")
    fun changeProfile(@Part profileImg: MultipartBody.Part?): Single<UserResponse>

    @FormUrlEncoded
    @PUT("/user/password")
    fun changePassword(
        @Field("existedSecretKey") existedSecretKey: String,
        @Field("newSecretKey") newSecretKey: String
    ): Single<BaseResponse>

    @FormUrlEncoded
    @PUT("/user/password-temp")
    fun tempPassword(@Field("email") email: String): Single<BaseResponse>

    @PUT("/user/sign-out")
    fun signOut(): Single<BaseResponse>

    @GET("/v1/nid/me")
    fun getNaverUser(
        @Header("Authorization") token: String
    ): Single<NaverUserResponse>

}