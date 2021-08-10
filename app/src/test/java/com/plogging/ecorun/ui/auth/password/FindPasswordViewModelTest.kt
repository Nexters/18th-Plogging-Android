package com.plogging.ecorun.ui.auth.password

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.model.User
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindPasswordViewModelTest : TestCase() {
    private lateinit var findPasswordViewModel: FindPasswordViewModel
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUpViewModel() {
        authRepository = FakeAuthRepository()
        authRepository.addUser(User(userId = "ploggingteam@gmail.com"))
        findPasswordViewModel = FindPasswordViewModel(authRepository)
    }

    @Test
    fun password_clickButton_receiveSuccessStateCode() {
        findPasswordViewModel.email.value = "ploggingteam@gmail.com"
        findPasswordViewModel.tempPassword()
        assertEquals(200, findPasswordViewModel.responseCode.value)
    }
}