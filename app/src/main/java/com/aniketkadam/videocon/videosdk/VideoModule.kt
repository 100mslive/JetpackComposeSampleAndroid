package com.aniketkadam.videocon.videosdk

import android.app.Application
import dagger.Module
import dagger.Provides
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.models.enums.HMSAnalyticsEventLevel
import live.hms.video.utils.HMSLogger

@Module
object VideoModule {

    @Provides
    fun getVideoSdk(application: Application): HMSSDK {
        return HMSSDK
            .Builder(application)
            .setAnalyticEventLevel(HMSAnalyticsEventLevel.ERROR)
            .setLogLevel(HMSLogger.LogLevel.VERBOSE)
            .build()
    }
}