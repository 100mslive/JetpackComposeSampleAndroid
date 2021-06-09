package com.aniketkadam.videocon.tokenretriever

import com.aniketkadam.videocon.BuildConfig
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRequestApi {
    @POST(BuildConfig.TOKEN_ENDPOINT)
    fun getToken(@Body tokenRequest: TokenRequest): Observable<TokenResponse>
}