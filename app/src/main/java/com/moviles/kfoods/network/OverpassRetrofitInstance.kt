package com.moviles.kfoods.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient

object OverpassRetrofitInstance {

    private val client = OkHttpClient.Builder()
        .build()

    val api: OverpassApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://overpass-api.de/api/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OverpassApiService::class.java)
    }
}
