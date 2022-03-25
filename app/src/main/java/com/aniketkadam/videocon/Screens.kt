package com.aniketkadam.videocon

import androidx.activity.compose.BackHandler
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

fun NavGraphBuilder.getLoginScreen(navController: NavHostController) {
    composable(Screen.LOGIN.route) {
        val loginVm = hiltViewModel<LoginVm>()
        NavHelper(navController, loginVm.navigate.value)
        LoginScreen(loginVm::login)
    }
}

fun NavGraphBuilder.getRoomScreen(
    navController: NavHostController,
    runOnUiThread: (Runnable) -> Unit
) {
    composable(
        "room?userName={userName}",
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
            BackHandler {
                roomVm.leave {
                    runOnUiThread {
                        navController.navigate(Screen.LOGIN.route)
                    }
                }
            }
            VideosListComposable(roomVm.peers)
        }
    }
}