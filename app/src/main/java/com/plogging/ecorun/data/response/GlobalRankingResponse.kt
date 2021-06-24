package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName
import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.data.model.RankingPageNumber

data class GlobalRankingResponse(
    @SerializedName("data")
    val `data`: List<GlobalRank>,
    @SerializedName("meta")
    val rankingPageNumber: RankingPageNumber,
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String
)