package com.aniketkadam.videocon.navigation

sealed class Screen(val route: String) {

    object ROOM : Screen("room")
    object LOGIN : Screen("login")
    data class Loading(val userName: String) : Screen("loading")

}

