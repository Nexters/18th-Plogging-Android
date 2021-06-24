package com.plogging.ecorun.ui.setting.withdraw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    val responseCode = MutableLiveData<Int>()

    fun withdraw() = repository.deleteUser()
        .subscribe(object : DefaultSingleObserver<BaseResponse>(){
            override fun onSuccess(response: BaseResponse) {
                responseCode.value = response.rc
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                if(e is HttpException) responseCode.value = e.code()
            }
        })
}