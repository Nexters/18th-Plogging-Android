package com.plogging.ecorun.data.repository.ranking

import com.plogging.ecorun.data.remote.ranking.RankingDataSource
import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import io.reactivex.Single
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(private val rankingDataSource: RankingDataSource) :
    RankingRepository {
    override fun getGlobalRanking(
        rankType: String,
        rankCntPerPage: Int?,
        pageNumber: Int?
    ): Single<GlobalRankingResponse> =
        rankingDataSource.getGlobalRanking(rankType, rankCntPerPage, pageNumber)

    override fun getUserRanking(rankType: String, id: String): Single<UserRankingResponse> =
        rankingDataSource.getUserRanking(rankType, id)
}