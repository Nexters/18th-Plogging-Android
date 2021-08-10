package com.plogging.ecorun.ui.main.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.repository.plogging.PloggingPagingRepository
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@HiltViewModel
class UserViewModel @ExperimentalCoroutinesApi
@Inject constructor(
    private val ploggingPagingRepository: PloggingPagingRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    val userData = MutableLiveData<UserDetailResponse>()
    val searchType = MutableLiveData<Int>()
    val userId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    fun getUserPloggingData(): Flowable<PagingData<MyDatabasePlogging>>? {
        userId.value ?: return null
        searchType.value ?: return null
        return ploggingPagingRepository.getUserPloggingData(
            userId = userId.value!!,
            searchType = searchType.value!!
        )
            .cachedIn(viewModelScope)
    }

    fun getUserData() {
        userId.value ?: return
        authRepository.getUserInfo(userId.value!!)
            .subscribe(object : DefaultSingleObserver<UserDetailResponse>() {
                override fun onSuccess(data: UserDetailResponse) {
                    Log.d("userData", "${data}")
                    userData.value = data
                }
            })
    }
}
