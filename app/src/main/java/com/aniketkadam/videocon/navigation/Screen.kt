package com.aniketkadam.videocon.navigation

sealed class Screen(val route: String) {

    object LOGIN : Screen("login")
    data class Room(val userName: String) : Screen("room")

}

