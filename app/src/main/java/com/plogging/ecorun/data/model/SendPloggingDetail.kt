package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

data class SendPloggingDetail(
    @SerializedName("calorie")
    val calorie: Int,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("plogging_time")
    val ploggingTime: Int
)