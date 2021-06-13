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
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.utils.SharedEglContext
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber

@Composable
fun PeerVideoComposable(peer: HMSPeer) {

    var previousActivePeer by remember { mutableStateOf(peer) }
    var previousVideoEnabled by remember { mutableStateOf(false) }

    Box {
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    init(SharedEglContext.context, null)
                    setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                    setEnableHardwareScaler(true)
                }
            },
            update = {
                Timber.d("Previous: ${previousActivePeer.peerID}/${previousActivePeer.name}, new: ${peer.peerID}/${peer.name}")
                // Peer changed, tile is rebound to a new peer.
                if (previousActivePeer.peerID != peer.peerID) {
                    if (previousVideoEnabled) {
                        Timber.d("Releasing video, removing sink")
                        previousActivePeer.videoTrack?.removeSink(it)
                        it.release()
                    } else {
                        Timber.d("Not releasing video since it was never enabled")
                    }
                    previousActivePeer = peer
                } else {
                    // Peer's the same or new, just re-rendered or ran the first time.
                    if (previousVideoEnabled && peer.videoTrack != null) {
                        Timber.d("Peer ${peer.peerID} name:${peer.name} had no video")
                    } else if (!previousVideoEnabled && peer.videoTrack != null) {
                        peer.videoTrack?.addSink(it)
                        Timber.d("Peer ${peer.name} had video, adding")
                    }
                }

                previousVideoEnabled = peer.videoTrack == null
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