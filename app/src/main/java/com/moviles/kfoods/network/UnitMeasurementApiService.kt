package com.moviles.kfoods.network

import com.moviles.kfoods.models.dto.UnitMeasurementDto
import retrofit2.Response
import retrofit2.http.GET


interface UnitMeasurementApiService {

    @GET("api/unit_measurement")
    suspend fun getAllUnitMeasurements(): Response<List<UnitMeasurementDto>>
}

