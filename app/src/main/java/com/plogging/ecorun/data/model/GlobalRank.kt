package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GlobalRank(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("profileImg")
    val profileImg: String,
    @SerializedName("score")
    val score: String,
    @SerializedName("userId")
    val userId: String
) : Serializable