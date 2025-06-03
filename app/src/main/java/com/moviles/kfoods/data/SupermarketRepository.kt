package com.moviles.kfoods.data

import com.moviles.kfoods.models.dto.OverpassElement
import com.moviles.kfoods.network.OverpassRetrofitInstance

class SupermarketRepository {
    suspend fun fetchNearbySupermarkets(lat: Double, lon: Double, radiusMeters: Int = 1000): List<OverpassElement> {
        // Consulta Overpass en lenguaje OverpassQL para supermercados dentro de un radio
        val query = """
        [out:json];
        (
          node["shop"="supermarket"](around:$radiusMeters,$lat,$lon);
          way["shop"="supermarket"](around:$radiusMeters,$lat,$lon);
          relation["shop"="supermarket"](around:$radiusMeters,$lat,$lon);
        );
        out center;
    """.trimIndent()

        val response = OverpassRetrofitInstance.api.getNearbySupermarkets(query)
        return response.elements
    }

}