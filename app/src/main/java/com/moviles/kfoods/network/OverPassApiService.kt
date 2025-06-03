package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.OverpassResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApiService {
    @POST("interpreter")
    @FormUrlEncoded
    suspend fun getNearbySupermarkets(
        @Field("data") query: String
    ): OverpassResponse
}
