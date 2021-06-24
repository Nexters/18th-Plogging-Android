package com.plogging.ecorun.data.response


import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    @SerializedName("distanceMonthly")
    val distanceMonthly: String,
    @SerializedName("distanceWeekly")
    val distanceWeekly: String,
    @SerializedName("rc")
    val rc: Int,
    @SerializedName("rcmsg")
    val rcmsg: String,
    @SerializedName("scoreMonthly")
    val scoreMonthly: String,
    @SerializedName("scoreWeekly")
    val scoreWeekly: String,
    @SerializedName("trashMonthly")
    val trashMonthly: String,
    @SerializedName("trashWeekly")
    val trashWeekly: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userImg")
    val userImg: String,
    @SerializedName("userName")
    val userName: String
)