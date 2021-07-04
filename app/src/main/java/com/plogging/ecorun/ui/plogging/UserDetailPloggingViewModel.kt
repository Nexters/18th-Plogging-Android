package com.plogging.ecorun.ui.plogging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.repository.plogging.PloggingRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDetailPloggingViewModel @Inject constructor(private val repository: PloggingRepository) :
    BaseViewModel() {
    val ploggingImageName = MutableLiveData<String>()
    val ploggingId = MutableLiveData<String>()
    val responseCode = MutableLiveData<Int>()

    fun deleteMyPlogging() {
        ploggingId.value ?: return
        ploggingImageName.value ?: return
        repository.deleteMyPlogging(ploggingId.value!!, ploggingImageName.value!!)
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    responseCode.value = response.rc
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.d("stackTraceToString", "${e.stackTraceToString()}")
                }
            })
    }
}