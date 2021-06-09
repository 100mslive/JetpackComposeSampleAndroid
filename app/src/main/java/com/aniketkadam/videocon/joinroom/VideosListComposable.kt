package com.aniketkadam.videocon.joinroom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp
import com.aniketkadam.videocon.joinroom.peer.PeerVideoComposable
import live.hms.video.sdk.models.HMSPeer

@ExperimentalFoundationApi
@Composable
fun VideosListComposable(peers: State<List<HMSPeer>>) {
    LazyVerticalGrid(cells =  GridCells.Adaptive(minSize = 128.dp)) {
        items(peers.value.size) {
            PeerVideoComposable(peers.value[it])
        }
    }
}