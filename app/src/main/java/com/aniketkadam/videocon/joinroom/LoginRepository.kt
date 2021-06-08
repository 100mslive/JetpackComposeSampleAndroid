package com.aniketkadam.videocon.joinroom

import com.aniketkadam.videocon.tokenretriever.TokenRequest
import com.aniketkadam.videocon.tokenretriever.TokenRequestApi
import com.aniketkadam.videocon.tokenretriever.TokenResponse
import io.reactivex.rxjava3.core.Observable
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import javax.inject.Inject


class LoginRepository @Inject constructor(
    private val tokenRequestApi: TokenRequestApi,
    private val hmssdk: HMSSDK
) {

    fun login(userName: String): Observable<TokenResponse> {
        return tokenRequestApi.getToken(TokenRequest(userId = userName))
    }

    fun joinRoom(userName: String, authToken: String, updateListener : HMSUpdateListener) {
        val config = HMSConfig(userName, authToken)
        hmssdk.join(config,updateListener)
    }

}