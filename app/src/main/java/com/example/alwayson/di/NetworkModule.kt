package com.example.alwayson.di

import com.example.alwayson.BuildConfig
import com.example.alwayson.data.ErrorSitesAPI
import com.example.alwayson.data.UrlListApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideBuildlogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthInterceptorOkHttpClient

      @Qualifier
      @Retention(AnnotationRetention.BINARY)
      annotation class OtherInterceptorOkHttpClient

    @Provides
    @Singleton
    @AuthInterceptorOkHttpClient
    fun provideOkHttpClient1() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
         //   .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))

            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }


       @Provides
        @Singleton
       @OtherInterceptorOkHttpClient
       fun provideOkHttpClient2() = if (BuildConfig.DEBUG) {
           val loggingInterceptor = HttpLoggingInterceptor()
           loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
           OkHttpClient.Builder()
               .addInterceptor(loggingInterceptor)
         // .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
           .build()
       } else {
           OkHttpClient
               .Builder()
               .build()
       }

    @Provides
    @Singleton
    @Named("base")
    fun provideRetrofitBuilder(
        @Named("base")
        alwaysonUrl: String,     @AuthInterceptorOkHttpClient
         okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(alwaysonUrl)
            .client(okHttpClient)

            .build()
    }


    @Provides
    @Singleton
    @Named("errorSites")
    fun provideRetrofitBuilderErrorSites(
        @Named("errorSites") errorSites: String,        @OtherInterceptorOkHttpClient
         okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
          //  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(errorSites)
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideApi(@Named("base") retrofit: Retrofit): UrlListApi {
        return retrofit.create(UrlListApi::class.java)
    }


    @Provides
    @Singleton
    fun provideErrorSitesApi(@Named("errorSites") retrofit: Retrofit): ErrorSitesAPI {
        return retrofit.create(ErrorSitesAPI::class.java)
    }


  /*  @Provides
    @Singleton
    fun provideApiVbtop(@Named("vbtop") retrofit: Retrofit): VbtopApi {
        return retrofit.create(VbtopApi::class.java)
    }*/


    @Provides
    @Named("base")
    fun provideBase() = "https://plawlaysonprod/"


    // @Named("vbttop")
   /* @Provides
    fun vbttopUrl(): String = "https://vbttopp.online/"*/
  //   private var vbttopUrl=""


    @Provides
    @Named("errorSites")
    fun errrorSites():String=BuildConfig.BASE_URL2

}