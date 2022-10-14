package com.example.alwayson.repository

import com.example.alwayson.data.ErrorSitesAPI
import com.example.alwayson.data.UrlListApi
import javax.inject.Inject

class MainRepository @Inject constructor(private  val urlListApi: UrlListApi, private  val errorSitesAPI: ErrorSitesAPI) {
    suspend fun getUrls(path:String) = urlListApi.getUrls(path)

    suspend fun postErrorSites(code:String, clientId:String,  name: String, domain: String)=errorSitesAPI.getErrorSites(code, clientId,name, domain)

   //  fun getVbtop(url:String)=urlApi.getVbtop(url)

   /*  fun getRetrofit(url:String): Retrofit {
         Log.d("getRetrofit", "getRetrofit")

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
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()
    }*/

}