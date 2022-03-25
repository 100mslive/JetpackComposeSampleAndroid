package com.aniketkadam.videocon

import android.Manifest
import android.app.PictureInPictureParams
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.InternalComposeApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.aniketkadam.videocon.navigation.Screen
import com.aniketkadam.videocon.permissions.NeedsPermission
import com.aniketkadam.videocon.permissions.checkSelfPermissionState
import com.aniketkadam.videocon.ui.theme.VideoConTheme
import dagger.hilt.android.AndroidEntryPoint

@InternalComposeApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoConTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    val cameraPermission = checkSelfPermissionState(
                        this,
                        Manifest.permission.CAMERA
                    )

                    NeedsPermission(
                        cameraPermission,
                        "Please grant Camera permissions",
                        "For a video conference, camera permission has to be granted"
                    ) {
                        NavHost(navController, startDestination = Screen.LOGIN.route) {

                            getLoginScreen(navController)

                            getRoomScreen(navController, ::runOnUiThread)
                        }
                    }
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }

}

