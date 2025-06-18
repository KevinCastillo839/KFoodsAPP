package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.WeeklyMenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MenuApiService {
    @GET("api/menu/weekly/user/{userId}")
    suspend fun getWeeklyMenu(@Path("userId") userId: Int): Response<WeeklyMenuResponse>

    @POST("api/menu/weekly/user/{user_id}/generate")
    suspend fun generateWeeklyMenu(@Path("user_id") userId: Int): Response<ApiResponse>
}
