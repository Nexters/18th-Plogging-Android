package com.plogging.ecorun.ui.auth.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.model.User
import io.reactivex.observers.TestObserver
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthHomeViewModelTest : TestCase() {
    private lateinit var authHomeViewModel: AuthHomeViewModel
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var isSavedUserObserver: TestObserver<Boolean>
    private lateinit var statusCodeObserver: TestObserver<Int>

    @Before
    fun setupViewModel() {
        authRepository = FakeAuthRepository()
        isSavedUserObserver = TestObserver.create()
        statusCodeObserver = TestObserver.create()

        val user1 = User(
            userId = "testUser1@gmail.com:gmail",
            userName = "testUser1",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
            userType = "google"
        )
        val user2 = User(
            userId = "testUser2@naver.com:naver",
            userName = "testUser2",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
            userType = "naver"
        )
        val user3 = User(
            userId = "testUser3@nate.com:custom",
            userName = "testUser3",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
            userType = "custom"
        )
        authRepository.addUser(user1, user2, user3)
        authHomeViewModel = AuthHomeViewModel(authRepository)
    }

    @Test
    fun notSavedSocialUser_signInCheck_returnNotSave() {
        val unKnownUser = User(userId = "unKnownUser@gmail.com:gmail", userName = "unKnownUser")
        authHomeViewModel.userId.value = unKnownUser.userId
        authHomeViewModel.isSavedUserSubject.subscribe(isSavedUserObserver)
        authHomeViewModel.isSavedSocialUser()
        assertEquals(false, isSavedUserObserver.values().first())
    }

    @Test
    fun savedSocialUser_signInCheck_returnSavedUser() {
        val savedUser = User(userId = "testUser1@gmail.com:gmail", userName = "savedUser")
        authHomeViewModel.userId.value = savedUser.userId
        authHomeViewModel.isSavedUserSubject.subscribe(isSavedUserObserver)
        authHomeViewModel.isSavedSocialUser()
        assertEquals(true, isSavedUserObserver.values().first())
    }

    @Test
    fun socialUserInfoInDB_signIn_getUser() {
        val socialUser = User(userId = "testUser1@gmail.com:gmail", userName = "socialUser")
        authHomeViewModel.userId.value = socialUser.userId
        authHomeViewModel.isSuccessSocialSignInSubject.subscribe(statusCodeObserver)
        authHomeViewModel.socialSignIn()
        assertEquals(200, statusCodeObserver.values().first())
    }

    @Test
    fun noSocialUserInfoInDB_signIn_createUser() {
        val socialUser = User(userId = "testUser@gmail.com:gmail", userName = "socialUser")
        authHomeViewModel.userId.value = socialUser.userId
        authHomeViewModel.isSuccessSocialSignInSubject.subscribe(statusCodeObserver)
        authHomeViewModel.socialSignIn()
        assertEquals(201, statusCodeObserver.values().first())
    }

    @Test
    fun naverUserInfo_signIn_saveUser() {
        val naverUser = User(userId = "testUser2@naver.com:naver", userName = "naverUser")
        authHomeViewModel.userId.value = naverUser.userId
        authHomeViewModel.isSuccessNaverSignInSubject.subscribe(isSavedUserObserver)
        authHomeViewModel.naverSignIn(naverUser.userId)
        assertEquals(true, isSavedUserObserver.values().first())
    }

    @Test
    fun naverUserInfo_signIn_failGetUser() {
        val naverUser = User(userId = "noUser@naver.com:naver", userName = "naverUser")
        authHomeViewModel.userId.value = naverUser.userId
        authHomeViewModel.isSuccessNaverSignInSubject.subscribe(isSavedUserObserver)
        authHomeViewModel.naverSignIn(naverUser.userId)
        assertEquals(false, isSavedUserObserver.values().first())
    }
}
