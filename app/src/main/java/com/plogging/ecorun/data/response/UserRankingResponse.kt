package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName
import com.plogging.ecorun.data.model.UserRank

data class UserRankingResponse(
    @SerializedName("data")
    val `data`: UserRank,
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String
)