package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName
import com.plogging.ecorun.data.model.Plogging

data class PloggingResponse(
    @SerializedName("plogging_list")
    val ploggingList: List<Plogging>,
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String
)