package com.example.alwayson.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ErrorSitesAPI {
    @POST("api/PLAlwaysOn-BlockedURL")
    suspend fun getErrorSites(
        @Query("code") code:String,
        @Query("clientId") clientId:String,
        @Query("name") name: String,
        @Query("domain") domain: String
    ): Response<ResponseBody>
}
