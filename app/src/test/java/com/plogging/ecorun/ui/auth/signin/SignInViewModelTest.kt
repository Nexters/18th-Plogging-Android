package com.plogging.ecorun.ui.auth.signin

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.util.extension.isValidEmail
import com.plogging.ecorun.util.extension.isValidPassword
import io.reactivex.observers.TestObserver
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInViewModelTest : TestCase() {
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var isSavedUser: TestObserver<Boolean>


    @Before
    fun setUpViewModel() {
        val user1 = User(
            userId = "testUser1@gmail.com:gmail",
            userName = "testUser1",
            userType = "google",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
        )
        val user2 = User(
            userId = "testUser2@naver.com:naver",
            userName = "testUser2",
            userType = "naver",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3"
        )
        val user3 = User(
            userId = "testUser3@nate.com:custom",
            userName = "testUser3",
            userType = "custom",
            userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
            secretKey = "1q2w3e4r"
        )

        isSavedUser = TestObserver.create()
        authRepository = FakeAuthRepository()
        authRepository.addUser(user1, user2, user3)
        signInViewModel = SignInViewModel(authRepository)
    }

    @Test
    fun emailAndPassword_input_ableSignInButton() {
        val email = "testUser3@nate.com"
        val password = "1q2w3e4r"
        val isValidEmail = email.isValidEmail()
        val isValidPassword = password.isValidPassword()
        val testSignInButtonSubject: TestObserver<Boolean> = TestObserver.create()
        signInViewModel.isSignInButtonEnableSubject.subscribe(testSignInButtonSubject)
        signInViewModel.isSignInButtonEnable()
        signInViewModel.isValidIdSubject.onNext(isValidEmail == true)
        signInViewModel.isValidPwSubject.onNext(isValidPassword == true)
        assertEquals(true, isValidEmail)
        assertEquals(true, isValidPassword)
        assertEquals(true, testSignInButtonSubject.values().last())
    }

    @Test
    fun customEmailAndPassword_clickButton_signIn() {
        signInViewModel.id.value = "testUser3@nate.com:custom"
        signInViewModel.pw.value = "1q2w3e4r"
        signInViewModel.signIn()
        assertTrue(signInViewModel.customSignInSuccess.value!!)
    }

    @Test
    fun socialEmail_clickButton_signIn() {
        signInViewModel.id.value = "testUser1@gmail.com:gmail"
        signInViewModel.isSavedUserSubject.subscribe(isSavedUser)
        signInViewModel.isSavedSocialUser()
        assertTrue(isSavedUser.values().first())
    }

    @Test
    fun naverEmail_clickButton_signIn() {
        signInViewModel.id.value = "testUser2@naver.com:naver"
        signInViewModel.isSuccessNaverSignInSubject.subscribe(isSavedUser)
        signInViewModel.naverSignIn("testUser2@naver.com:naver")
        assertTrue(isSavedUser.values().first())
    }
}