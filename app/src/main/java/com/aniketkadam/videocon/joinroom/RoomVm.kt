package com.aniketkadam.videocon.joinroom

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSMessage
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.sdk.models.HMSRoom
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import javax.inject.Inject

@HiltViewModel
class RoomVm @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    private val userName : MutableState<String> = mutableStateOf("")
    private val loginState : MutableState<LoginState> = mutableStateOf(LoginState.IDLE)

    private val roomState : MutableState<RoomState> = mutableStateOf(RoomState(null))

    fun login(name : String) {
        loginRepository.login(name)
            .map{ loginRepository.joinRoom(name, it.token, object : HMSUpdateListener{
                override fun onError(error: HMSException) {
                    TODO("Not yet implemented")
                    // Report errors
                }

                override fun onJoin(room: HMSRoom) {
                    TODO("Not yet implemented")
                    // Loading complete, move to display
                }

                override fun onMessageReceived(message: HMSMessage) {
                    TODO("Not yet implemented")
                }

                override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
                    TODO("Not yet implemented")
                }

                override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
                    TODO("Not yet implemented")
                }

                override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
                    TODO("Not yet implemented")
                }

            }) }
    }


}

data class RoomState(
    val currentRoom : HMSRoom?
)

sealed class LoginState {
    object IDLE : LoginState()
    object LOADING : LoginState()
    data class LoggedIn(val userName : String) : LoginState()
    data class Error(val exception : Exception) : LoginState()
}