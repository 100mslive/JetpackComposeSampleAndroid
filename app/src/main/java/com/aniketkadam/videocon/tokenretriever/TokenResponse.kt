package com.aniketkadam.videocon.tokenretriever

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token") val token: String
)