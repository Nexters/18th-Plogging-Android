package com.plogging.ecorun.data.remote.ranking

import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import com.plogging.ecorun.network.RankingApiService
import com.plogging.ecorun.util.extension.composeSchedulers
import io.reactivex.Single
import javax.inject.Inject

class RankingDataSourceImpl @Inject constructor(private val rankingApi: RankingApiService) :
    RankingDataSource {

    override fun getGlobalRanking(
        rankType: String,
        rankCntPerPage: Int?,
        pageNumber: Int?
    ): Single<GlobalRankingResponse> =
        rankingApi.getGlobalRanking(rankType, rankCntPerPage, pageNumber).composeSchedulers()

    override fun getUserRanking(rankType: String, id: String): Single<UserRankingResponse> =
        rankingApi.getUserRanking(id, rankType).composeSchedulers()
}