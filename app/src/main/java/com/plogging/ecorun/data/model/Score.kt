package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

data class Score(
    @SerializedName("activityScore")
    val activityScore: Int,
    @SerializedName("environmentScore")
    val environmentScore: Int,
    @SerializedName("totalScore")
    val totalScore: Int
)