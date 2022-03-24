package com.aniketkadam.videocon.permissions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi

@SuppressLint("MissingPermission")
@Composable
fun NeedsPermission(
    permissionsState: PermissionState,
    initialRationaleText: String,
    deniedPermissionRationaleText: String,
    permissionGrantedContent: @Composable () -> Unit
) {

    // read the current permission state using collectAsState (this will automatically
    // collect changes and trigger recomposition)
    val hasPermission = permissionsState.hasPermission.collectAsState(null).value ?: return
    if (hasPermission) {
        permissionGrantedContent()
    } else Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // user hasn't granted permission
        permissionsState.shouldShowRationale.collectAsState(null).value?.let { showPrompt ->
            if (showPrompt) {
                Text(deniedPermissionRationaleText)
                Spacer(Modifier.height(4.dp))
                Button(onClick = { permissionsState.launchPermissionRequest() }) {
                    Text("Give permissions")
                }
            } else {
                Text(initialRationaleText)
                Spacer(Modifier.height(4.dp))
                Button(onClick = { permissionsState.launchPermissionRequest() }) {
                    Text("OK")
                }
            }
        }

    }
}