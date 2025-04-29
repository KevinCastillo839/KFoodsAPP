package com.moviles.kfoods.network

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

interface ApiService {
//    @GET("api/event")
//    suspend fun getEvents(): List<Event>
//
//    @Multipart
//    @POST("api/event")
//    suspend fun addEvent(
//        @Part("Name") name: RequestBody,
//        @Part("Location") location: RequestBody,
//        @Part("Description") description: RequestBody,
//        @Part("Date") date: RequestBody,
//        @Part file: MultipartBody.Part? // Esto es para el archivo
//    ): Event
//
//    @PUT("api/event/{id}")
//    suspend fun updateEvent(@Path("id") id: Int?, @Body eventDto: Event): Event
//
//    @DELETE("api/event/{id}")
//    suspend fun deleteEvent(@Path("id") id: Int?): Response<Unit>
}
