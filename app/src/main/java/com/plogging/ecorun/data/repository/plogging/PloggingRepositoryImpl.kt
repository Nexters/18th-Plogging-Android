package com.plogging.ecorun.data.repository.plogging

import com.google.gson.Gson
import com.plogging.ecorun.data.model.SendPlogging
import com.plogging.ecorun.data.remote.plogging.PloggingDataSource
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class PloggingRepositoryImpl @Inject constructor(private val ploggingDataSource: PloggingDataSource) :
    PloggingRepository {
    override fun getScore(ploggingData: SendPlogging): Single<ScoreResponse> {
        val sendPloggingData = Gson().toJson(ploggingData)
        return ploggingDataSource.getScore(sendPloggingData)
    }

    override fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: SendPlogging
    ): Single<BaseResponse> {
        val sendPloggingData = Gson().toJson(ploggingData)
        val ploggingBody = MultipartBody.Part.createFormData("ploggingData", sendPloggingData)
        return ploggingDataSource.savePlogging(ploggingImg, ploggingBody)
    }

    override fun deleteMyPlogging(
        ploggingId: String,
        ploggingImgName: String
    ): Single<BaseResponse> = ploggingDataSource.deleteMyPlogging(ploggingId, ploggingImgName)
}