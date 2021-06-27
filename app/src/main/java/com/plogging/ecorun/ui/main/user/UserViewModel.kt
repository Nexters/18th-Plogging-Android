package com.plogging.ecorun.ui.main.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.repository.plogging.PloggingPagingRepository
import com.plogging.ecorun.data.response.UserDetailResponse
import com.plogging.ecorun.util.extension.composeSchedulers
import com.plogging.ecorun.util.observer.DefaultFlowableObserver
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val ploggingPagingRepository: PloggingPagingRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    val plogging = MutableLiveData<PagingData<MyDatabasePlogging>>()
    val isRequestUserPlogging = MutableLiveData(false)
    val userData = MutableLiveData<UserDetailResponse>()
    val searchType = MutableLiveData<Int>()
    val userId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    fun getUserPloggingData() {
        userId.value ?: return
        searchType.value ?: return
        ploggingPagingRepository.getUserPloggingData(
            userId = userId.value!!,
            searchType = searchType.value!!
        )
            .cachedIn(viewModelScope)
            .subscribe(object : DefaultFlowableObserver<PagingData<MyDatabasePlogging>>() {
                override fun onComplete() {
                    isRequestUserPlogging.value = true
                }

                override fun onNext(data: PagingData<MyDatabasePlogging>?) {
                    plogging.value = data!!
                }
            })
    }

    fun getUserData() {
        userId.value ?: return
        authRepository.getUserInfo(userId.value!!)
            .subscribe(object : DefaultSingleObserver<UserDetailResponse>() {
                override fun onSuccess(data: UserDetailResponse) {
                    userData.value = data
                    isRequestUserPlogging.value = true
                }
            })
    }
}