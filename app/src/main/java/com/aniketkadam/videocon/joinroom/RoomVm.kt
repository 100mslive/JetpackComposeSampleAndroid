package com.aniketkadam.videocon.joinroom

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import com.aniketkadam.videocon.baseviewmodels.NavigableViewModel
import com.aniketkadam.videocon.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.media.tracks.HMSTrackType
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSMessage
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.sdk.models.HMSRoleChangeRequest
import live.hms.video.sdk.models.HMSRoom
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoomVm @Inject constructor(private val roomRepository: RoomRepository) :
    NavigableViewModel() {

    private val peerComparator = Comparator<HMSPeer> { o1, o2 ->
        Timber.d("Sorting")
        if (o1.isLocal && o2.isLocal) {
            throw Exception("Two locals can't be present at the same time")
        }
        when {
            o1.isLocal -> -1
            o2.isLocal -> 1
            else -> o1.peerID.compareTo(o2.peerID)
        }
    }

    private val disposable = CompositeDisposable()

    private val _peers: MutableState<List<HMSPeer>> =
        mutableStateOf(emptyList(), neverEqualPolicy())
    val peers: State<List<HMSPeer>> = _peers

    private val _loadingState: MutableState<Boolean> = mutableStateOf(true)
    val loadingState: State<Boolean> = _loadingState

    private val chatMessages: MutableState<List<HMSMessage>> = mutableStateOf(emptyList())

    init {
        disposable.add(
            roomUpdatesObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {},
                    { _navigate.value = Screen.LOGIN } // return to login on error
                )
        )
    }

    private fun roomUpdatesObservable(): Observable<Unit> = roomRepository.login()
        .doOnError {
            Timber.e("Error getting token: ${it.message}")
        }
        .map {
            Timber.d("A token response was received")
            roomRepository.joinRoom(roomRepository.getName(), it.token, object : HMSUpdateListener {
                override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {

                }

                override fun onError(error: HMSException) {
                    Timber.e("There was an error ${error.description}")
//                    Error doesn't necessarily mean an unrecoverable error.
                }

                override fun onJoin(room: HMSRoom) {
                    Timber.d("Room joined")
                    // Loading complete, move to display
                    _peers.value = room.peerList.asList()
//                    _navigate.value = Screen.ROOM(roomRepository.getName())
                    _loadingState.value = false
                }

                override fun onMessageReceived(message: HMSMessage) {
                    chatMessages.value = chatMessages.value.plus(message)
                }

                override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
                    // Handle peer updates.
                    when (type) {
                        HMSPeerUpdate.PEER_JOINED -> _peers.value =
                            _peers.value.plus(peer).sortedWith(peerComparator)
                        HMSPeerUpdate.PEER_LEFT -> _peers.value =
                            _peers.value.filter { currentPeer -> currentPeer.peerID != peer.peerID }
                        HMSPeerUpdate.VIDEO_TOGGLED -> {
                            Timber.d("${peer.name} video toggled")
                        }

                        HMSPeerUpdate.AUDIO_TOGGLED,
                        HMSPeerUpdate.BECAME_DOMINANT_SPEAKER,
                        HMSPeerUpdate.NO_DOMINANT_SPEAKER,
                        HMSPeerUpdate.RESIGNED_DOMINANT_SPEAKER,
                        HMSPeerUpdate.STARTED_SPEAKING,
                        HMSPeerUpdate.STOPPED_SPEAKING -> {
                        }
                    }
                    Timber.d("There was a peer update: $type")
                }

                override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {

                }

                override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
                    Timber.d("Room updated: $type")
                }

                override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
                    // Somebody's audio/video changed
                    Timber.d("OnTrackUpdate: $type")
                    when (type) {
                        HMSTrackUpdate.TRACK_MUTED,
                        HMSTrackUpdate.TRACK_UNMUTED,
                        HMSTrackUpdate.TRACK_ADDED,
                        HMSTrackUpdate.TRACK_REMOVED
                        -> {
                            Timber.d("Checking, $type, $track")
                            if (track.type == HMSTrackType.VIDEO) {
                                _peers.value =
                                    _peers.value.filter { currentPeer -> currentPeer.peerID != peer.peerID }
                                        .plus(peer).sortedWith(peerComparator)
                            } else {
                                Timber.d("Not processed, $type, $track")
                            }
                        }
                        HMSTrackUpdate.TRACK_DESCRIPTION_CHANGED -> Timber.d("Other mute/unmute $type, $track")
                    }
                }
            })
        }

    fun leave(onSuccess: () -> Unit) {
        _peers.value = emptyList()
        roomRepository.leaveRoom(onSuccess)
    }

    override fun onCleared() {
        leave { disposable.dispose() }
        super.onCleared()
    }
}

