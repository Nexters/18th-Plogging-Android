package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String
)