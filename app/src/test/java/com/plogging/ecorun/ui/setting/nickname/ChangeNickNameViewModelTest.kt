package com.plogging.ecorun.ui.setting.nickname

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
class ChangeNickNameViewModelTest : TestCase() {

    private lateinit var changeNickNameViewModel: ChangeNickNameViewModel
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUpViewModel() {
        authRepository = FakeAuthRepository()
        authRepository.addUser(
            User(
                userId = "ploggingteam@gmail.com:google",
                userName = "nickName"
            )
        )
        changeNickNameViewModel = ChangeNickNameViewModel(authRepository)
    }

    @Test
    fun nickName_change_returnResponseCode() {
        SharedPreference.setUserEmail(
            ApplicationProvider.getApplicationContext(),
            "ploggingteam@gmail.com:google"
        )
        changeNickNameViewModel.nickname.value = "changedNickName"
        changeNickNameViewModel.changeNickname()
        assertEquals(200, changeNickNameViewModel.responseCode.value)
    }
}