package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.ShoppingListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ShoppingListApiService {

    @GET("api/shoppinglist/by-user/{userId}")
    suspend fun getShoppingListByUserId(
        @Path("userId") userId: Int
    ): Response<ShoppingListDto>
}