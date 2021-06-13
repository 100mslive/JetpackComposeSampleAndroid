package com.aniketkadam.videocon.tokenretriever

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import retrofit2.Retrofit

@InstallIn(ActivityRetainedComponent::class)
@Module
object TokenModule {
    @Provides
    fun provideTokenApi(retrofit: Retrofit) = retrofit.create(TokenRequestApi::class.java)
}