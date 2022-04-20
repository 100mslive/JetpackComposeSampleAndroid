package com.aniketkadam.videocon.navigation

import androidx.navigation.NavController

sealed class Screen(val route: String) {

    abstract fun navigate(navController: NavController)

    object LOGIN : Screen("login") {
        override fun navigate(navController: NavController) {
            navController.navigate(route)
        }
    }

    data class Room(val userName: String) : Screen("room") {
        companion object {
            const val paramsRoute = "room?userName={userName}"
        }

        override fun navigate(navController: NavController) {
            navController.navigate("${route}?userName=${userName}")
        }

    }

}

