package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName

data class NaverUser(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)