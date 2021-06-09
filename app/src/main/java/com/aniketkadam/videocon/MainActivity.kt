package com.aniketkadam.videocon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aniketkadam.videocon.joinroom.LoginState
import com.aniketkadam.videocon.joinroom.RoomVm
import com.aniketkadam.videocon.joinroom.VideosListComposable
import com.aniketkadam.videocon.loading.LoadingScreen
import com.aniketkadam.videocon.login.LoginScreen
import com.aniketkadam.videocon.ui.theme.VideoConTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoConTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    val vm by viewModels<RoomVm>()

                    NavHost(navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(vm::login)
                        }

                        composable("loading") {
                            LoadingScreen()
                        }

                        composable("room") {
                            VideosListComposable(vm.peers)
                        }
                    }

                    LaunchedEffect(key1 = vm.loginState.value) {
                        when (vm.loginState.value) {
                            is LoginState.Error -> { // Just showing login again
                                navController.navigate("login")
                            }
                            LoginState.LOADING -> navController.navigate("loading")
                            LoginState.LoggedIn -> navController.navigate("room")
                            LoginState.IDLE -> {
                            }
                        }
                    }
                }
            }
        }
    }
}
