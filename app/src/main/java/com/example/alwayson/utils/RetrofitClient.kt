package com.example.alwayson.utils

import com.example.alwayson.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RetrofitClient {

    companion object {
        fun getClient(path: String): Retrofit {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()
            } else {
                OkHttpClient
                    .Builder()
                    .build()
            }
            return Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                //.client(client)
                .build()
        }


       /* fun getClientErrorSites(path:String): Retrofit {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()
            } else {
                OkHttpClient
                    .Builder()
                    .build()
            }
            return Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                //.client(client)
                .build()
        }*/

    }
}