package com.plogging.ecorun

import androidx.test.core.app.ApplicationProvider
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.NaverUser
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.NaverUserResponse
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.data.response.UserResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {

    var userServiceData: LinkedHashMap<String, User> = LinkedHashMap()

    override fun saveUserInfo(user: User): Single<UserResponse> {
        userServiceData[user.userId] = user
        return Single.just(UserResponse(200, "OK", user.userUri, user.userName))
    }

    override fun isSavedUser(userId: String): Single<BaseResponse> {
        if (userServiceData.contains(userId))
            return Single.just(BaseResponse(201, "Found"))
        return Single.just(BaseResponse(200, "Not Found"))
    }

    override fun socialSignIn(user: User): Single<UserResponse> {
        if (userServiceData.contains(user.userId))
            return Single.just(
                UserResponse(
                    rc = 200,
                    rcmsg = "OK",
                    userName = user.userName,
                    userImg = "https://eco-run.duckdns.org/profile/base/profile-3"
                )
            )
        else {
            addUser(user)
            return Single.just(
                UserResponse(
                    rc = 201,
                    rcmsg = "create",
                    userName = user.userName,
                    userImg = "https://eco-run.duckdns.org/profile/base/profile-3"
                )
            )
        }
    }

    override fun signIn(user: User): Single<UserResponse> {
        if (userServiceData.contains(user.userId) && userServiceData[user.userId]!!.secretKey == user.secretKey)
            return Single.just(
                UserResponse(
                    rc = 200,
                    rcmsg = "OK",
                    userName = userServiceData[user.userId]!!.userName,
                    userImg = userServiceData[user.userId]!!.secretKey
                )
            )
        return Single.error(Exception())
    }

    override fun changeNickname(nickname: String): Single<UserResponse> {
        val userId = SharedPreference.getUserEmail(ApplicationProvider.getApplicationContext())
        userServiceData[userId] = User(userId = userId, userName = nickname)
        return Single.just(UserResponse(200, "OK", userName = nickname))
    }

    override fun signOut(): Single<BaseResponse> {
        val userId = SharedPreference.getUserEmail(ApplicationProvider.getApplicationContext())
        userServiceData.remove(userId)
        return Single.just(BaseResponse(200, "OK"))
    }

    override fun deleteUser(): Single<BaseResponse> {
        val userId = SharedPreference.getUserEmail(ApplicationProvider.getApplicationContext())
        userServiceData.remove(userId)
        return Single.just(BaseResponse(200, "OK"))
    }

    override fun changePassword(oldPassword: String, newPassword: String): Single<BaseResponse> {
        val userId = SharedPreference.getUserEmail(ApplicationProvider.getApplicationContext())
        if (userServiceData[userId]?.secretKey == oldPassword)
            userServiceData[userId] = User(userId = userId, secretKey = newPassword)
        else return Single.error(Exception())
        return Single.just(BaseResponse(200, "OK"))
    }

    override fun tempPassword(email: String): Single<BaseResponse> {
        return Single.just(BaseResponse(200, "OK"))
    }

    override fun changeProfile(userImage: MultipartBody.Part): Single<UserResponse> {
        val userId = SharedPreference.getUserEmail(ApplicationProvider.getApplicationContext())
        val user = User(userId = userId, userUri = userServiceData[userId]?.userUri)
        return Single.just(UserResponse(200, "OK", userImg = userServiceData[userId]?.userUri))
    }

    override fun getUserInfo(userId: String): Single<UserDetailResponse> {
        val user = UserDetailResponse(
            rc = 200,
            rcmsg = "OK",
            distanceMonthly = "200",
            distanceWeekly = "10",
            scoreMonthly = "3000",
            scoreWeekly = "3000",
            trashMonthly = "3000",
            trashWeekly = "3000",
            userId = "ploggingteam@naver.com",
            userImg = "https://eco-run.duckdns.org/profile/base/profile-3",
            userName = "ecorun",
        )
        return Single.just(user)
    }

    override fun getNaverUser(token: String): Single<NaverUserResponse> {
        if (userServiceData.contains(token))
            return Single.just(
                NaverUserResponse(
                    message = "success",
                    naverUser = NaverUser(
                        email = "ploggingteam@naver.com",
                        id = "naver",
                        name = "네이버"
                    ),
                    resultcode = "200"
                )
            )
        return Single.error(Exception())
    }

    fun addUser(vararg users: User) {
        for (user in users) {
            userServiceData[user.userId] = user
        }
    }
}