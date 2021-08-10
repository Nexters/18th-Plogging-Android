package com.plogging.ecorun.ui.auth.nickname

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.model.User
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NickNameViewModelTest : TestCase() {
    private lateinit var nickNameViewModel: NickNameViewModel
    private lateinit var repository: FakeAuthRepository

    @Before
    fun setUpViewModel() {
        repository = FakeAuthRepository()
        nickNameViewModel = NickNameViewModel(repository)
    }

    @Test
    fun customSignIn() {
        val userId = "ploggingteam@nate.com"
        val userName = "runner"
        val secretKey = "1q2w3e4r"
        nickNameViewModel.userId.value = userId
        nickNameViewModel.userName.value = userName
        nickNameViewModel.secretKey.value = secretKey
        repository.addUser(
            User(
                userId = userId,
                userName = userName,
                secretKey = secretKey
            )
        )
        nickNameViewModel.saveUser()
        assertEquals(200, nickNameViewModel.responseCode.value)
    }

    @Test
    fun socialSignIn() {
        val userId = "ploggingteam@gmail.com"
        val userName = "runner"
        nickNameViewModel.userId.value = userId
        nickNameViewModel.userName.value = userName
        repository.addUser(
            User(
                userId = userId,
                userName = userName
            )
        )
        nickNameViewModel.saveSocialUser()
        assertEquals(200, nickNameViewModel.responseCode.value)
    }
}