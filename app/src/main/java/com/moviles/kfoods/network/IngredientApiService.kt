package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.CreateIngredientRequestDto
import com.moviles.kfoods.models.dto.IngredientDto
import com.moviles.kfoods.models.dto.UpdateIngredientRequestDto
import retrofit2.Response
import retrofit2.http.*

interface IngredientApiService {

    @GET("api/ingredient")
    suspend fun getAllIngredients(): Response<List<IngredientDto>>

    @GET("ingredient/{id}")
    suspend fun getIngredientById(@Path("id") id: Int): Response<IngredientDto>

    @POST("ingredient")
    suspend fun createIngredient(
        @Body createDto: CreateIngredientRequestDto
    ): Response<IngredientDto>

    @PUT("ingredient/{id}")
    suspend fun updateIngredient(
        @Path("id") id: Int,
        @Body updateDto: UpdateIngredientRequestDto
    ): Response<IngredientDto>

    @DELETE("ingredient/{id}")
    suspend fun deleteIngredient(@Path("id") id: Int): Response<Unit>
}
