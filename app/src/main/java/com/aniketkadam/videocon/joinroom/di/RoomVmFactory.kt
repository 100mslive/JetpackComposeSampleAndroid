package com.aniketkadam.videocon.joinroom.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aniketkadam.videocon.joinroom.RoomRepository
import com.aniketkadam.videocon.joinroom.RoomVm
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class RoomVmFactory @AssistedInject constructor(
    @Assisted("userName") private val userName: String,
    val roomRepository: RoomRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomVm::class.java)) {
            return RoomVm(roomRepository, userName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}