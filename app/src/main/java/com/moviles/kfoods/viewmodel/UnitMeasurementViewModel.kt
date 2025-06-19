package com.moviles.kfoods.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.UnitMeasurementDto
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UnitMeasurementViewModel(

) : ViewModel() {

    private val _unitMeasurements = MutableStateFlow<List<UnitMeasurementDto>>(emptyList())
    val unitMeasurements: StateFlow<List<UnitMeasurementDto>> = _unitMeasurements

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchUnitMeasurements() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.unitMeasurementApi.getAllUnitMeasurements()
                if (response.isSuccessful) {
                    _unitMeasurements.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
