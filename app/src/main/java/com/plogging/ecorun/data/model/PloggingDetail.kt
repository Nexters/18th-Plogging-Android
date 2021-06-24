package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

data class PloggingDetail(
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("created_time")
    val createdTime: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("plogging_activity_score")
    val ploggingActivityScore: Int,
    @SerializedName("plogging_environment_score")
    val ploggingEnvironmentScore: Int,
    @SerializedName("plogging_img")
    val ploggingImg: String,
    @SerializedName("plogging_time")
    val ploggingTime: Int,
    @SerializedName("plogging_total_score")
    val ploggingTotalScore: Int,
    @SerializedName("user_id")
    val userId: String
)
