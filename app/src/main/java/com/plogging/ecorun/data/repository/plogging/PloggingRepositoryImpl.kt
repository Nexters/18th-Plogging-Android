package com.plogging.ecorun.data.repository.plogging

import com.plogging.ecorun.data.remote.plogging.PloggingDataSource
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class PloggingRepositoryImpl @Inject constructor(private val ploggingDataSource: PloggingDataSource) :
    PloggingRepository {
    override fun getScore(ploggingData: String): Single<ScoreResponse> =
        ploggingDataSource.getScore(ploggingData)

    override fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: MultipartBody.Part
    ): Single<BaseResponse> = ploggingDataSource.savePlogging(ploggingImg, ploggingData)

    override fun deleteMyPlogging(
        ploggingId: String,
        ploggingImgName: String
    ): Single<BaseResponse> = ploggingDataSource.deleteMyPlogging(ploggingId, ploggingImgName)
}