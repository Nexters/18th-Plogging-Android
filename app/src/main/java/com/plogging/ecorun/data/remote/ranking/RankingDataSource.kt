package com.plogging.ecorun.data.remote.ranking

import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import io.reactivex.Single

interface RankingDataSource {

    fun getGlobalRanking(
        rankType: String,
        rankCntPerPage: Int?,
        pageNumber: Int?
    ): Single<GlobalRankingResponse>

    fun getUserRanking(rankType: String, id: String): Single<UserRankingResponse>
}