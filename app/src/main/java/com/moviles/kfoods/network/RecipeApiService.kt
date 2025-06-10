package com.moviles.kfoods.network

import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeApiService {
    @GET("api/recipe/user/{userId}")
    suspend fun getRecipesForUser(@Path("userId") id: Int): Response<List<Recipe>>

    @GET("api/recipe")
    suspend fun getRecipes(): Response<List<Recipe>>

    @GET("api/recipe/{id}")
    suspend fun getRecipesById(@Path("id") id: Int?): Response<Recipe>
}