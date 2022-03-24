package com.aniketkadam.videocon.joinroom

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.aniketkadam.videocon.joinroom.peer.PeerVideoComposable
import live.hms.video.sdk.models.HMSPeer

@Composable
fun VideosListComposable(peers: State<List<HMSPeer>>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(peers.value.size) {
            PeerVideoComposable(peers.value[it])
        }
    }
}