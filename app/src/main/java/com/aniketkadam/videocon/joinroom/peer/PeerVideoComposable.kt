package com.aniketkadam.videocon.joinroom.peer

import androidx.compose.runtime.*
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

}