package com.aniketkadam.videocon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.aniketkadam.videocon.baseviewmodels.NavHelper
import com.aniketkadam.videocon.joinroom.RoomVm
import com.aniketkadam.videocon.joinroom.VideosListComposable
import com.aniketkadam.videocon.joinroom.di.RoomVmAssistedFactory
import com.aniketkadam.videocon.loading.LoadingScreen
import com.aniketkadam.videocon.login.LoginScreen
import com.aniketkadam.videocon.login.LoginVm
import com.aniketkadam.videocon.navigation.Screen
import com.aniketkadam.videocon.ui.theme.VideoConTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var roomVmServiceFactory: RoomVmAssistedFactory

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoConTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = Screen.LOGIN.route) {

                        composable(Screen.LOGIN.route) {
                            val loginVm = hiltViewModel<LoginVm>()
                            NavHelper(navController, loginVm.navigate.value)
                            LoginScreen(loginVm::login)
                        }

                        composable("loading?userName={userName}",
                            arguments = listOf(navArgument("userName") {
                                nullable = false
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->

                            val userName = backStackEntry.arguments!!.getString("userName")!!

                            val roomVm: RoomVm by viewModels { roomVmServiceFactory.create(userName) }
                            NavHelper(navController, roomVm.navigate.value)
                            LoadingScreen()
                        }

                        composable(Screen.ROOM.route) {
                            val roomVm: RoomVm by viewModels()
                            VideosListComposable(roomVm.peers)
                        }
                    }

                }
            }
        }
    }

}
