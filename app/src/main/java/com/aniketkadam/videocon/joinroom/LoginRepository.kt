package com.aniketkadam.videocon.joinroom

import com.aniketkadam.videocon.tokenretriever.TokenRequest
import com.aniketkadam.videocon.tokenretriever.TokenRequestApi
import com.aniketkadam.videocon.tokenretriever.TokenResponse
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import javax.inject.Inject


class LoginRepository @Inject constructor(
    private val tokenRequestApi: TokenRequestApi,
    private val hmssdk: HMSSDK
) {

    fun login(
        userName: String,
        roomId: String = "60bcef7810772c81240d84d8"
    ): Observable<TokenResponse> {
        return tokenRequestApi.getToken(TokenRequest(userId = userName, roomId = roomId))
    }

    fun joinRoom(userName: String, authToken: String, updateListener: HMSUpdateListener) {
        val info = JsonObject().apply { addProperty("name", userName) }
        val config = HMSConfig(
            userName = userName,
            authtoken = authToken,
            metadata = info.toString()
        )
        hmssdk.join(config, updateListener)
    }

}