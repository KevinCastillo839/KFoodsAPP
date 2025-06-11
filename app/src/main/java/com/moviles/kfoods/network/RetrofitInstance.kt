package com.moviles.kfoods.network

import ApiService
import android.content.Context
import com.moviles.kfoods.MyApplication
import com.moviles.kfoods.common.Constants.API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Función para obtener el token de SharedPreferences
    private fun getAuthToken(): String? {
        val sharedPreferences = MyApplication.instance.getSharedPreferences("KFoodsPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }


    // Crear el cliente OkHttp con el interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = getAuthToken()
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    // Crear la instancia de Retrofit
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    val menuApi: MenuApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MenuApiService::class.java)
    }
    val preferenceApi: PreferenceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PreferenceApiService::class.java)
    }

}
