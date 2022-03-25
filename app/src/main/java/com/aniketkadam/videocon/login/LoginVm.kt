package com.aniketkadam.videocon.login

import com.aniketkadam.videocon.baseviewmodels.NavigableViewModel
import com.aniketkadam.videocon.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginVm @Inject constructor() : NavigableViewModel() {
    fun login(name: String) {
        val safeName = if (name.isEmpty()) "Android User" else name
        _navigate.value = Screen.Room(safeName)
    }
}