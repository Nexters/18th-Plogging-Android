package com.plogging.ecorun.data.model


import com.google.gson.annotations.SerializedName

data class SendPlogging(
    @SerializedName("meta")
    val meta: SendPloggingDetail,
    @SerializedName("trash_list")
    val trashList: List<Trash>
)