package com.plogging.ecorun.data.remote.plogging

import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody

interface PloggingDataSource {
    fun getScore(ploggingData: String): Single<ScoreResponse>
    fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: MultipartBody.Part
    ): Single<BaseResponse>

    fun deleteMyPlogging(ploggingId: String, ploggingImgName: String): Single<BaseResponse>
}