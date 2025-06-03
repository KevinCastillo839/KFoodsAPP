package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.WeeklyMenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MenuApiService {
    @GET("api/menu/weekly/user/{userId}")
    suspend fun getWeeklyMenu(@Path("userId") userId: Int): Response<List<WeeklyMenuResponse>>
}
