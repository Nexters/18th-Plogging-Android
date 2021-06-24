package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName
import com.plogging.ecorun.data.model.NaverUser

data class NaverUserResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("response")
    val naverUser: NaverUser,
    @SerializedName("resultcode")
    val resultcode: String
)