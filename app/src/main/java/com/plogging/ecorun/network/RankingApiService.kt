package com.plogging.ecorun.network

import com.plogging.ecorun.data.response.GlobalRankingResponse
import com.plogging.ecorun.data.response.UserRankingResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RankingApiService {

    @GET("/rank/global")
    fun getGlobalRanking(
        @Query("rankType") rankType: String,
        @Query("rankCntPerPage") rankCntPerPage: Int?,
        @Query("pageNumber") pageNumber: Int?
    ): Single<GlobalRankingResponse>

    @GET("/rank/users/{id}")
    fun getUserRanking(
        @Path("id") id: String,
        @Query("rankType") rankType: String
    ): Single<UserRankingResponse>
}