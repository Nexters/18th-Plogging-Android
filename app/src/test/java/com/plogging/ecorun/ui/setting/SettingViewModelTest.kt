package com.plogging.ecorun.ui.setting

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeAuthRepository
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.User
import junit.framework.TestCase
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingViewModelTest : TestCase() {
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUpViewModel() {
        authRepository = FakeAuthRepository()
        authRepository.addUser(
            User(
                userId = "testUser1@gmail.com:gmail",
                userName = "testUser1",
                userUri = "https://eco-run.duckdns.org/profile/base/profile-3",
                userType = "google"
            )
        )
        settingViewModel = SettingViewModel(authRepository)
    }

    @Test
    fun newProfilePicture_change_returnStatusCode() {
        SharedPreference.setUserEmail(
            ApplicationProvider.getApplicationContext(),
            "testUser1@gmail.com:gmail"
        )
        settingViewModel.profile = MultipartBody.Part.createFormData("parameterName", "Image")
        settingViewModel.changeProfile()
        assertEquals(200, settingViewModel.responseCode.value)
    }
}