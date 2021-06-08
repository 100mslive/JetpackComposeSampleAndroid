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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoomVm @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    private val userName : MutableState<String> = mutableStateOf("")
    private val loginState : MutableState<LoginState> = mutableStateOf(LoginState.IDLE)

    private val roomName : MutableState<String> = mutableStateOf("")
    private val peers : MutableState<List<HMSPeer>> = mutableStateOf(emptyList<HMSPeer>())

    private val chatMessages : MutableState<List<HMSMessage>> = mutableStateOf(emptyList())

    fun login(name : String) {
        loginRepository.login(name)
            .map{ loginRepository.joinRoom(name, it.token, object : HMSUpdateListener{
                override fun onError(error: HMSException) {
                    Timber.e("There was an error ${error.description}")
                }

                override fun onJoin(room: HMSRoom) {
                    Timber.d("Room joined")
                    // Loading complete, move to display
                    roomName.value = room.name
                    peers.value = room.peerList.asList()
                    loginState.value = LoginState.LoggedIn
                }

                override fun onMessageReceived(message: HMSMessage) {
                    chatMessages.value = chatMessages.value.plus(message)
                }

                override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
                    // Handle peer updates.
                    Timber.d("There was a peer update: $type")
                }

                override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
                    Timber.d("Room updated: $type")
                }

                override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
                    // Somebody's audio/video changed
                    Timber.d("OnTrackUpdate: $type")
                }

            }) }
    }


}

sealed class LoginState {
    object IDLE : LoginState()
    object LOADING : LoginState()
    object LoggedIn : LoginState()
    data class Error(val exception : Exception) : LoginState()
}