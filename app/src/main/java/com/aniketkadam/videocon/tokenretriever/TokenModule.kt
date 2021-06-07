package com.aniketkadam.videocon.tokenretriever

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@InstallIn(ViewModelComponent::class)
@Module
object TokenModule {
    @Provides
    fun provideTokenApi(retrofit: Retrofit) = retrofit.create(TokenRequestApi::class.java)
}