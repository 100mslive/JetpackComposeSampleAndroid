package com.aniketkadam.videocon.tokenretriever

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRequestApi {
    @POST("/")
    fun getToken(@Body tokenRequest: TokenRequest): Observable<TokenResponse>
}