package com.plogging.ecorun.network

import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.PloggingResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface PloggingApiService {

    @FormUrlEncoded
    @POST("/plogging/score")
    fun getScore(@Field("ploggingData") ploggingData: String): Single<ScoreResponse>

    @Multipart
    @POST("/plogging")
    fun savePlogging(
        @Part ploggingImg: MultipartBody.Part,
        @Part ploggingData: MultipartBody.Part
    ): Single<BaseResponse>

    @GET("plogging/{targetUserId}")
    fun getUserPloggingData(
        @Path("targetUserId") id: String,
        @Query("searchType") searchType: Int,
        @Query("ploggingCntPerPage") itemPerPage: Int,
        @Query("pageNumber") position: Int,
    ): Single<PloggingResponse>

    @DELETE("plogging")
    fun deleteMyPlogging(
        @Query("ploggingId") ploggingId: String,
        @Query("ploggingImgName") ploggingImgName: String
    ): Single<BaseResponse>
}