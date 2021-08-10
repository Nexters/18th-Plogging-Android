package com.plogging.ecorun.ui.setting.withdraw

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
class WithdrawViewModelTest : TestCase() {
    private lateinit var withdrawViewModel: WithdrawViewModel
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
        withdrawViewModel = WithdrawViewModel(authRepository)
    }

    @Test
    fun user_withDraw_returnStatusCode() {
        SharedPreference.setUserEmail(
            ApplicationProvider.getApplicationContext(),
            "ploggingteam@gmail.com:google"
        )
        withdrawViewModel.withdraw()
        assertEquals(200, withdrawViewModel.responseCode.value)
    }
}