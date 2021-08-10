package com.plogging.ecorun

import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.data.model.RankingPageNumber
import com.plogging.ecorun.data.model.UserRank
import com.plogging.ecorun.data.repository.ranking.RankingRepository
import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import io.reactivex.Single

class FakeRankRepository : RankingRepository {
    private val rankServiceData = LinkedHashMap<String, UserRank>()

    override fun getGlobalRanking(
        rankType: String,
        rankCntPerPage: Int?,
        pageNumber: Int?
    ): Single<GlobalRankingResponse> {
        val baseRankCntPerPage = rankCntPerPage ?: 10
        val basePageNumber = pageNumber ?: 0
        val lastPosition =
            if (baseRankCntPerPage.times(basePageNumber + 1) > rankServiceData.size) rankServiceData.size
            else baseRankCntPerPage.times(basePageNumber + 1)
        val rankList = rankServiceData.values
            .sortedByDescending { it.rank }
            .subList(
                baseRankCntPerPage.times(basePageNumber),
                lastPosition
            )
        val globalList = mutableListOf<GlobalRank>()
        for (userRank in rankList)
            globalList.add(
                GlobalRank(
                    displayName = userRank.displayName,
                    profileImg = userRank.profileImg,
                    score = userRank.score,
                    userId = userRank.userId
                )
            )
        val globalPageNumber = RankingPageNumber(
            currentPageNumber = basePageNumber,
            startPageNumber = 0,
            endPageNumber = rankServiceData.size / baseRankCntPerPage + 1
        )
        return Single.just(
            GlobalRankingResponse(globalList.toList(), globalPageNumber, 200, "OK")
        )
    }

    override fun getUserRanking(rankType: String, id: String): Single<UserRankingResponse> {
        return if (rankServiceData.contains(id))
            Single.just(UserRankingResponse(rankServiceData[id]!!, 200, "OK"))
        else Single.error(Exception())
    }

    fun addRankUser(vararg users: UserRank) {
        for (user in users) rankServiceData[user.userId] = user
    }
}