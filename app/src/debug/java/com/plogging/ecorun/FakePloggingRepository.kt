package com.plogging.ecorun

import com.plogging.ecorun.data.model.Plogging
import com.plogging.ecorun.data.model.PloggingDetail
import com.plogging.ecorun.data.model.Score
import com.plogging.ecorun.data.model.SendPlogging
import com.plogging.ecorun.data.repository.plogging.PloggingRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.LinkedHashMap

class FakePloggingRepository : PloggingRepository {
    private val ploggingServiceData = LinkedHashMap<String, Plogging>()

    override fun getScore(ploggingData: SendPlogging): Single<ScoreResponse> {
        val activityScore =
            ploggingData.meta.calorie + ploggingData.meta.distance + ploggingData.meta.ploggingTime
        val environmentScore = ploggingData.trashList.sumOf { it.pickCount }
        return Single.just(
            ScoreResponse(
                200,
                "OK",
                Score(activityScore, environmentScore, activityScore + environmentScore)
            )
        )
    }

    override fun savePlogging(
        ploggingImg: MultipartBody.Part,
        ploggingData: SendPlogging
    ): Single<BaseResponse> {
        val ploggingId = (ploggingServiceData.size + 1).toString()
        val activityScore =
            ploggingData.meta.calorie + ploggingData.meta.distance + ploggingData.meta.ploggingTime
        val environmentScore = ploggingData.trashList.sumOf { it.pickCount }
        val ploggingDetail = PloggingDetail(
            calories = ploggingData.meta.calorie,
            createdTime = Calendar.getInstance().time.toString(),
            distance = ploggingData.meta.distance,
            ploggingActivityScore = activityScore,
            ploggingEnvironmentScore = environmentScore,
            ploggingImg = ploggingImg.toString(),
            ploggingTime = ploggingData.meta.ploggingTime,
            ploggingTotalScore = activityScore + environmentScore,
            userId = "ploggingteam@gmail.com:google"
        )
        ploggingServiceData[ploggingId] = Plogging(
            id = ploggingId,
            ploggingDetail = ploggingDetail,
            trashList = ploggingData.trashList
        )
        return Single.just(BaseResponse(200, "OK"))
    }

    override fun deleteMyPlogging(
        ploggingId: String,
        ploggingImgName: String
    ): Single<BaseResponse> {
        return if (ploggingServiceData.contains(ploggingId)) {
            ploggingServiceData.remove(ploggingId)
            Single.just(BaseResponse(200, "OK"))
        } else {
            Single.error(Exception())
        }
    }

    fun addPlogging(plogging: Plogging) {
        ploggingServiceData[plogging.id] = plogging
    }
}