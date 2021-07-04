package com.plogging.ecorun.ui.main.rank

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.data.model.UserRank
import com.plogging.ecorun.data.repository.ranking.RankingRepository
import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import com.plogging.ecorun.util.constant.Constant.MONTHLY
import com.plogging.ecorun.util.constant.Constant.WEEKLY
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(private val rankingRepository: RankingRepository) :
    BaseViewModel() {

    val isRequestGlobalRanking = MutableLiveData(false)
    val isRequestUserRanking = MutableLiveData(false)
    val monthRankList = MutableLiveData<List<GlobalRank>>()
    val weekRankList = MutableLiveData<List<GlobalRank>>()
    var isEmptyMyData = MutableLiveData<Boolean>()
    val userRankData = MutableLiveData<UserRank>()
    var isEmptyWeeklyList = MutableLiveData<Boolean>()
    var isEmptyMonthlyList = MutableLiveData<Boolean>()
    val isSignIn = MutableLiveData<Boolean>()
    val rankType = MutableLiveData(WEEKLY)
    val userId = MutableLiveData<String>()

    fun getGlobalRanking() {
        rankType.value ?: return
        isRequestGlobalRanking.value = true
        rankingRepository.getGlobalRanking(rankType.value!!, 10, 0)
            .subscribe(object : DefaultSingleObserver<GlobalRankingResponse>() {
                override fun onSuccess(response: GlobalRankingResponse) {
                    if (rankType.value == WEEKLY) weekRankList.value = response.data
                    else if (rankType.value == MONTHLY) monthRankList.value = response.data
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (rankType.value == WEEKLY) isEmptyWeeklyList.value = true
                    else if (rankType.value == MONTHLY) isEmptyMonthlyList.value = true
                }
            })
    }

    fun getMyRanking() {
        rankType.value ?: return
        userId.value ?: return
        isRequestUserRanking.value = true
        rankingRepository.getUserRanking(rankType.value!!, userId.value!!)
            .subscribe(object : DefaultSingleObserver<UserRankingResponse>() {
                override fun onSuccess(response: UserRankingResponse) {
                    userRankData.value = response.data
                    isEmptyMyData.value = false
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    isEmptyMyData.value = true
                }
            })
    }
}