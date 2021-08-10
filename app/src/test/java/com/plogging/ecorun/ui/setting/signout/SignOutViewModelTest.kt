package com.plogging.ecorun.ui.setting.signout

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.User
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignOutViewModelTest : TestCase() {
    private lateinit var signOutViewModel: SignOutViewModel
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUpViewModel() {
        authRepository = FakeAuthRepository()
        authRepository.addUser(
            User(
                userId = "ploggingteam@gmail.com:google",
                userName = "nickName",
                secretKey = "password"
            )
        )
        signOutViewModel = SignOutViewModel(authRepository)
    }

    @Test
    fun user_signOut_returnStatusCode() {
        SharedPreference.setUserEmail(
            ApplicationProvider.getApplicationContext(),
            "ploggingteam@gmail.com:google"
        )
        signOutViewModel.signOut()
        assertEquals(200, signOutViewModel.responseCode.value)
    }
}