package com.plogging.ecorun.data.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("appleIdentifier")
    val appleIdentifier: String? = null,
    @SerializedName("secretKey")
    val secretKey: String? = null,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String? = null,
    @SerializedName("userType")
    val userType: String? = null,
    @SerializedName("userUri")
    val userUri: String? = null
)