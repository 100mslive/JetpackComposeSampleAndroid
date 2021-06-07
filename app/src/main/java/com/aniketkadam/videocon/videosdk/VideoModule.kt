package com.aniketkadam.videocon.videosdk

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.models.enums.HMSAnalyticsEventLevel
import live.hms.video.utils.HMSLogger
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object VideoModule {

    @Singleton
    @Provides
    fun getVideoSdk(application: Application): HMSSDK {

        return HMSSDK
            .Builder(application)
            .setAnalyticEventLevel(HMSAnalyticsEventLevel.ERROR)
            .setLogLevel(HMSLogger.LogLevel.VERBOSE)
            .build()
    }
}