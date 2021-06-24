package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

class UserRank(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("profileImg")
    val profileImg: String,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("score")
    val score: String,
    @SerializedName("userId")
    val userId: String
)