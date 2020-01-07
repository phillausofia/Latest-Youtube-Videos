package com.example.latest_youtube_videos.network

import com.example.latest_youtube_videos.utils.Utils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(Utils.CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .readTimeout(Utils.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Utils.API_BASE_URL)
    .client(okHttpClient)
    .build()

object YoutubeApi {
    val youtubeApiService: YoutubeApiService by lazy {
        retrofit.create(YoutubeApiService::class.java)
    }
}
