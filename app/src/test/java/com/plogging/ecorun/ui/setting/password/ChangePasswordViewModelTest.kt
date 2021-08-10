package com.plogging.ecorun.ui.setting.password

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.User
import com.plogging.ecorun.util.extension.isValidPassword
import io.reactivex.observers.TestObserver
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangePasswordViewModelTest : TestCase() {
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var testButtonEnableSubject: TestObserver<Boolean>

    @Before
    fun setUpViewModel() {
        authRepository = FakeAuthRepository()
        testButtonEnableSubject = TestObserver.create()
        authRepository.addUser(
            User(
                userId = "ploggingteam@gmail.com:google",
                userName = "nickName",
                secretKey = "password"
            )
        )
        changePasswordViewModel = ChangePasswordViewModel(authRepository)
    }

    @Test
    fun newPassword_change_returnStatusCode() {
        SharedPreference.setUserEmail(
            ApplicationProvider.getApplicationContext(),
            "ploggingteam@gmail.com:google"
        )
        changePasswordViewModel.oldPassword.value = "password"
        changePasswordViewModel.newPassword.value = "newPassword"
        changePasswordViewModel.changePassword()
        assertEquals(200, changePasswordViewModel.responseCode.value)
    }

    @Test
    fun inputPassword_verifiedPassword_returnTrue() {
        changePasswordViewModel.buttonEnableSubject.subscribe(testButtonEnableSubject)
        changePasswordViewModel.changePwButtonEnable()
        val oldPassword = "password@"
        val confirmPassword = "password@"
        val newPassword = "newPassword@"
        changePasswordViewModel.isNotEmptyCurrentPwSubject.onNext(oldPassword.isNotBlank())
        changePasswordViewModel.isValidNewPwSubject.onNext(newPassword.isValidPassword()!!)
        changePasswordViewModel.isMatchedPwSubject.onNext(oldPassword == confirmPassword)
        assertTrue(testButtonEnableSubject.values().last())
    }
}