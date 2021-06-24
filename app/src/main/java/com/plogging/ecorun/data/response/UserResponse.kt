package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String,
    @SerializedName("userImg")
    val userImg: String? = null,
    @SerializedName("userName")
    val userName: String? = null
)