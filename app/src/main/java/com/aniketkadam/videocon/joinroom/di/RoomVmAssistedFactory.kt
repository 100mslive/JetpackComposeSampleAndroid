package com.aniketkadam.videocon.joinroom.di

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface RoomVmAssistedFactory {
    fun create(@Assisted("userName") userName: String): RoomVmFactory
}