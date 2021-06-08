package com.aniketkadam.videocon.joinroom.peer

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import live.hms.video.sdk.models.HMSPeer
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

@Composable
fun PeerVideoComposeable(peer: HMSPeer) {

    AndroidView(
        factory = { context ->
            SurfaceViewRenderer(context).apply {
                init(EglBase.create().eglBaseContext, null)
            }
        },
        update = {
            it.release() // Line 148 of egl renderer checks, so it's *always* safe to release without acquiring
            peer.videoTrack?.addSink(it)
        }
    )

}