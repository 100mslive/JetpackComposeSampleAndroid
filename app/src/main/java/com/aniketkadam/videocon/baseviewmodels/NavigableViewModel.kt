package com.aniketkadam.videocon.baseviewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.aniketkadam.videocon.navigation.Screen

open class NavigableViewModel : ViewModel() {
    protected val _navigate = mutableStateOf<Screen?>(null)
    val navigate: State<Screen?> = _navigate
}

@Composable
fun NavHelper(navController: NavHostController, key: Screen?) {
    remember(key) {
        key?.navigate(navController)
        key
    }
}