package com.example.alwayson.data


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface UrlListApi {
    @GET
    suspend fun getUrls(@Url path: String): Response<UrlList>

   /* @GET
    fun getVbtop(@Url url:String): Call<VbtopUrl>*/

}