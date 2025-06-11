package com.moviles.kfoods.network

import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.CreatePreferenceRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreferenceApiService {

    // Preferences
    @GET("api/preference")
    suspend fun getPreferences(): Response<List<Preference>>

    @GET("api/preference/{id}")
    suspend fun getPreferenceById(@Path("id") id: Int): Response<Preference>

    @POST("api/preference")
    suspend fun createPreference(@Body request: CreatePreferenceRequestDto): Response<Preference>


    @PUT("api/preference/{id}")
    suspend fun updatePreference(@Path("id") id: Int, @Body request: Preference): Response<Preference>

    @DELETE("api/preference/{id}")
    suspend fun deletePreference(@Path("id") id: Int): Response<ApiResponse>

    @GET("api/dietary_restriction")
    suspend fun getDietaryRestriction(): Response<List<DietaryRestriction>>

    @GET("api/dietary_goal")
    suspend fun getDietaryGoal(): Response<List<DietaryGoal>>

//    @POST("api/dietary_restriction")
//    suspend fun createDietaryRestriction(@Body request: UserDietaryRestriction): Response<UserDietaryRestriction>
//
//    @POST("api/dietary_goal")
//    suspend fun createDietaryGoal(@Body request: UserDietaryGoal): Response<UserDietaryGoal>
    @POST("api/dietary_restriction")
    suspend fun createDietaryRestriction(@Body request: UserDietaryRestriction): Response<ApiResponse>

    @POST("api/dietary_goal")
    suspend fun createDietaryGoal(@Body request: UserDietaryGoal): Response<ApiResponse>


}