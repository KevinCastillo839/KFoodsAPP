package com.moviles.kfoods.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context

import android.util.Log
import com.moviles.kfoods.models.dto.OverpassElement

import com.moviles.kfoods.data.SupermarketRepository
import android.location.Location
import getCurrentLocation


class SupermarketViewModel : ViewModel() {

    private val repository = SupermarketRepository()

    private val _supermarkets = MutableStateFlow<List<OverpassElement>>(emptyList())
    val supermarkets: StateFlow<List<OverpassElement>> = _supermarkets

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    fun loadNearbySupermarkets(context: Context, radiusMeters: Int = 5000) {
        viewModelScope.launch {
            try {
                val location = getCurrentLocation(context)
                Log.d("SupermarketViewModel", "Ubicación obtenida: $location")
                if (location != null) {
                    _currentLocation.value = location
                    val list = repository.fetchNearbySupermarkets(
                        location.latitude,
                        location.longitude,
                        radiusMeters
                    )
                    Log.d("SupermarketViewModel", "Supermercados obtenidos: ${list.size}")
                    _supermarkets.value = list
                } else {
                    Log.e("SupermarketViewModel", "Ubicación es null")
                    _errorMessage.value = "No se pudo obtener tu ubicación"
                }
            } catch (e: Exception) {
                Log.e("SupermarketViewModel", "Error al cargar supermercados: ${e.message}", e)
                _errorMessage.value = e.message ?: "Unknown error"
            }
        }
    }

}
