package com.aniketkadam.videocon.permissions

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*


@InternalComposeApi
@Composable
fun checkSelfPermissionState(
    activity: ComponentActivity,
    permission: String
): PermissionState {
    val key = currentComposer.compoundKeyHash.toString()
    val call = remember(activity, permission) {
        PermissionResultCall(key, activity, permission)
    }

    // drive initialCheck and unregister from composition lifecycle
    DisposableEffect(call) {
        call.initialCheck()
        onDispose {
            call.unregister()
        }
    }

    return call.checkSelfPermission()
}