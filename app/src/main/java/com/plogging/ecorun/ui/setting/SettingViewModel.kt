package com.plogging.ecorun.ui.setting

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.UserResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: AuthRepository) :
    BaseViewModel() {
    val responseCode = MutableLiveData<Int>()
    var profile: MultipartBody.Part? = null

    fun changeProfile() {
        profile ?: return
        repository.changeProfile(profile!!)
            .subscribe(object : DefaultSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    responseCode.value = response.rc
                }
            })
    }
}