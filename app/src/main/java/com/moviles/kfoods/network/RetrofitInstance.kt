package com.moviles.kfoods.network

import ApiService
import android.content.Context
//import com.google.firebase.appdistribution.gradle.ApiService
import com.moviles.kfoods.MyApplication
import com.moviles.kfoods.common.Constants.API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
   
    private fun getAuthToken(): String? {
        val sharedPreferences =
            MyApplication.instance.getSharedPreferences("KFoodsPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

   
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
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

    val recipeApi: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }

    val preferenceApi: PreferenceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PreferenceApiService::class.java)
    }

    val ingredientApi: IngredientApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IngredientApiService::class.java)
    }

    //val shoppingListApi: ShoppingListApiService by lazy {

    val unitMeasurementApi: UnitMeasurementApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


  
            .create(UnitMeasurementApiService::class.java)
    }
    val shoppingListApi: ShoppingListApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

            //.create(ShoppingListApiService::class.java)

            .create(ShoppingListApiService::class.java)
    }
}
