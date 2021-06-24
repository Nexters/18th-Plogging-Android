package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName
import com.plogging.ecorun.data.model.Score

data class ScoreResponse(
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String,
    @SerializedName("score")
    val score: Score
)