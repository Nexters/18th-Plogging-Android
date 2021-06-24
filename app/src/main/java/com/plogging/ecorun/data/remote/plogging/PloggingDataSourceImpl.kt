package com.plogging.ecorun.data.remote.plogging

import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import com.plogging.ecorun.network.PloggingApiService
import com.plogging.ecorun.util.extension.composeSchedulers
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class PloggingDataSourceImpl @Inject constructor(private val ploggingApi: PloggingApiService) :
    PloggingDataSource {

    override fun getScore(ploggingData: String): Single<ScoreResponse> =
        ploggingApi.getScore(ploggingData).composeSchedulers()

    override fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: MultipartBody.Part
    ): Single<BaseResponse> =
        ploggingApi.savePlogging(ploggingImg, ploggingData).composeSchedulers()

    override fun deleteMyPlogging(
        ploggingId: String,
        ploggingImgName: String
    ): Single<BaseResponse> =
        ploggingApi.deleteMyPlogging(ploggingId, ploggingImgName).composeSchedulers()
}