package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

data class RankingPageNumber(
    @SerializedName("currentPageNumber")
    val currentPageNumber: Int,
    @SerializedName("endPageNumber")
    val endPageNumber: Int,
    @SerializedName("startPageNumber")
    val startPageNumber: Int
)