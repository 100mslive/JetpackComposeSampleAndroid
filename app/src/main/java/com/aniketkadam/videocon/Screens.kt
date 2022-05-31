package com.aniketkadam.videocon

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aniketkadam.videocon.baseviewmodels.NavHelper
import com.aniketkadam.videocon.joinroom.RoomVm
import com.aniketkadam.videocon.joinroom.VideosListComposable
import com.aniketkadam.videocon.loading.LoadingScreen
import com.aniketkadam.videocon.login.LoginScreen
import com.aniketkadam.videocon.login.LoginVm
import com.aniketkadam.videocon.navigation.Screen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.getLoginScreen(navController: NavHostController) {
    composable(Screen.LOGIN.route) {
        val loginVm = hiltViewModel<LoginVm>()
        NavHelper(navController, loginVm.navigate.value)
        LoginScreen(loginVm::login)
    }
}

fun NavGraphBuilder.getRoomScreen(
    navController: NavHostController
) {
    composable(
        Screen.Room.paramsRoute,
        arguments = listOf(navArgument("userName") {
            nullable = false
            type = NavType.StringType
        })
    ) {
        val roomVm = hiltViewModel<RoomVm>()
        NavHelper(navController, roomVm.navigate.value)
        if (roomVm.loadingState.value) {
            LoadingScreen()
        } else {
            // Leave the room when the user swipes back.
            BackHandler {
                roomVm.leave {
                    MainScope().launch {
                        navController.navigate(Screen.LOGIN.route)
                    }
                }
            }
            Column {
                // A leave button at the top
                Button(onClick = {
                    roomVm.leave {
                        MainScope().launch {
                            navController.navigate(Screen.LOGIN.route)
                        }
                    }
                }) {
                    Text("Leave")
                }

                // Peers and their video tiles
                VideosListComposable(roomVm.peers)
            }
        }
    }
}