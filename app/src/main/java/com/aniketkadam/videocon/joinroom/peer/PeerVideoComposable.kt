package com.aniketkadam.videocon.joinroom.peer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import live.hms.video.media.tracks.HMSVideoTrack
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.utils.SharedEglContext
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber

@Composable
fun PeerVideoComposable(peer: HMSPeer) {

    val composeableId by remember { mutableStateOf(Math.random().toString()) }

    var previousActivePeer by remember { mutableStateOf(peer) }
    var previousVideoTrack by remember { mutableStateOf<HMSVideoTrack?>(null) }

    Box {
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                    setEnableHardwareScaler(true)
                }
            },
            update = {
                Timber.d("ContainerId: $composeableId, previous: ${previousActivePeer.peerID}/${previousActivePeer.name}, new: ${peer.peerID}/${peer.name}")
                // Peer changed, tile is rebound to a new peer.
                if (previousActivePeer.peerID != peer.peerID) {
                    if (previousVideoTrack != null) {
                        Timber.d("ContainerId: $composeableId Releasing video, removing sink")
                        previousVideoTrack?.removeSink(it)
                        it.release()
                    } else {
                        Timber.d("ContainerId: $composeableId, not releasing video since it was never enabled")
                    }

                    previousActivePeer = peer
                }

                if (peer.videoTrack == null) {
                    Timber.d("Peer ${peer.peerID} name:${peer.name} had no video")
                } else if (previousVideoTrack == null) {
                    it.init(SharedEglContext.context, null)
                    Timber.d("ContainerId: $composeableId, peer ${peer.name} had video, adding")
                    peer.videoTrack?.addSink(it)
                    previousVideoTrack = peer.videoTrack
                }

            }
        )
        Text(
            peer.name, modifier = Modifier
                .background(Color(0x80CCCCCC))
                .padding(4.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
    }

}