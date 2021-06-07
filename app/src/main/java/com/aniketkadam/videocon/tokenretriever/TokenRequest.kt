package com.aniketkadam.videocon.tokenretriever

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("room_id") val roomId : String = "60bcef7810772c81240d84d8",
    @SerializedName("user_id") val userId : String,
    @SerializedName("role") val role : String = "Student"
)