package com.moviles.kfoods.network

import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.Recipe
import com.moviles.kfoods.models.dto.CreateRecipeRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface RecipeApiService {

    @GET("api/recipe/user/{userId}")
    suspend fun getRecipesForUser(@Path("userId") id: Int): Response<List<Recipe>>

    @GET("api/recipe/user/{userId}/recipes")
    suspend fun getMyRecipes(@Path("userId") id: Int): Response<List<Recipe>>

    @GET("api/recipe")
    suspend fun getRecipes(): Response<List<Recipe>>

    @GET("api/recipe/{id}")
    suspend fun getRecipesById(@Path("id") id: Int?): Response<Recipe>

    @Multipart
    @POST("api/recipe/createmyrecipe")
    suspend fun createMyRecipe(
        @Part("name") name: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("category") category: RequestBody,
        @Part("preparation_time") preparationTime: RequestBody,
        @Part("created_at") createdAt: RequestBody,
        @Part("updated_at") updatedAt: RequestBody?,
        @Part("user_id") userId: RequestBody,
        @Part("Recipe_IngredientsJson") recipeIngredientsJson: RequestBody,
        @Part image: MultipartBody.Part? // Imagen opcional
    ): Response<Recipe>

    @DELETE("api/recipe/{id}")
    suspend fun deleteRecipe(
        @Path("id") id: Int
    ): Response<Unit>

    @Multipart
    @PUT("api/recipe/{id}")
    suspend fun updateRecipe(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("category") category: RequestBody,
        @Part("preparation_time") preparationTime: RequestBody,
        @Part("updated_at") updatedAt: RequestBody,
        @Part("Recipe_IngredientsJson") recipeIngredientsJson: RequestBody,
        @Part image: MultipartBody.Part? // Imagen opcional
    ): Response<Unit>



}