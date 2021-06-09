package com.aniketkadam.videocon.joinroom

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    private val disposable = CompositeDisposable()
    private val userName: MutableState<String> = mutableStateOf("")
    private val _loginState: MutableState<LoginState> = mutableStateOf(LoginState.IDLE)
    val loginState: State<LoginState> = _loginState

    private val roomName: MutableState<String> = mutableStateOf("")
    private val _peers: MutableState<List<HMSPeer>> = mutableStateOf(emptyList<HMSPeer>())
    val peers: State<List<HMSPeer>> = _peers

    private val chatMessages: MutableState<List<HMSMessage>> = mutableStateOf(emptyList())

    fun login(name: String) {
        _loginState.value = LoginState.LOADING
        disposable.add(
            joinRoom(name)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {},
                    { e -> _loginState.value = LoginState.Error(e) }
                )
        )
    }

    private fun joinRoom(name: String): Observable<Unit> = loginRepository.login(name)
        .doOnError {
            Timber.e("Error getting token: ${it.message}")
        }
        .map {
            Timber.d("A token response was received")
            loginRepository.joinRoom(name, it.token, object : HMSUpdateListener {
                override fun onError(error: HMSException) {
                    Timber.e("There was an error ${error.description}")
                    _loginState.value = LoginState.Error(error)
                }

                override fun onJoin(room: HMSRoom) {
                    Timber.d("Room joined")
                    // Loading complete, move to display
                    roomName.value = room.name
                    _peers.value = room.peerList.asList()
                    _loginState.value = LoginState.LoggedIn
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
            })
        }

    override fun onCleared() {
        loginRepository.leaveRoom()
        disposable.dispose()
        super.onCleared()
    }
}

sealed class LoginState {
    object IDLE : LoginState()
    object LOADING : LoginState()
    object LoggedIn : LoginState()
    data class Error(val exception: Throwable) : LoginState()
}