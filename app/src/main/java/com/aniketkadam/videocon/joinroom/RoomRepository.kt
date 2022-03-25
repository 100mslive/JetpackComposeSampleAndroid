package com.aniketkadam.videocon.joinroom

import androidx.lifecycle.SavedStateHandle
import com.aniketkadam.videocon.tokenretriever.TokenRequest
import com.aniketkadam.videocon.tokenretriever.TokenRequestApi
import com.aniketkadam.videocon.tokenretriever.TokenResponse
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import live.hms.video.error.HMSException
import live.hms.video.sdk.HMSActionResultListener
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import javax.inject.Inject


class RoomRepository @Inject constructor(
    private val tokenRequestApi: TokenRequestApi,
    private val hmssdk: HMSSDK,
    private val savedStateHandle: SavedStateHandle
) {

    fun login(
        userName: String = savedStateHandle.get<String>("userName")!!,
        roomId: String = "620c893b71bd215ae042225e"
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

    fun leaveRoom(onComplete: () -> Unit) {
        hmssdk.leave(object : HMSActionResultListener {
            override fun onError(error: HMSException) {
                onComplete()
            }

            override fun onSuccess() {
                onComplete()
            }
        })
    }

    fun getName() = savedStateHandle.get<String>("userName")!!
}