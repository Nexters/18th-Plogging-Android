package com.plogging.ecorun.data.repository.plogging

import com.plogging.ecorun.data.model.SendPlogging
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody

interface PloggingRepository {
    fun getScore(ploggingData: SendPlogging): Single<ScoreResponse>
    fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: SendPlogging
    ): Single<BaseResponse>

    fun deleteMyPlogging(ploggingId: String, ploggingImgName: String): Single<BaseResponse>


}