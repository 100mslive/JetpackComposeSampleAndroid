package com.aniketkadam.videocon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.aniketkadam.videocon.navigation.Screen

@Composable
fun NavigateOnChange(key: Screen?, navController: NavHostController) {
    remember(key) {
        if (key != null) {
            navController.navigate(key.route)
        }
        key
    }
}