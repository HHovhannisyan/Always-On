package com.example.alwayson.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface DomainApi {
    @GET
    fun getDomaonApi(@Url url:String): Call<Domain>
}



