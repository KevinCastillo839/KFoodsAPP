package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class AllergyViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _allergies = MutableStateFlow<List<Allergy>>(emptyList())
    val allergies: StateFlow<List<Allergy>> = _allergies
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)

    fun getAllergies() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getAllergies()
                if (response.isSuccessful && response.body() != null) {
                    _allergies.value = response.body()!!
                    Log.i("AllergyViewModel", "Fetched allergies: ${response.body()}")
                } else {
                    Log.e("AllergyViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AllergyViewModelError", "Error fetching allergies: ${e.message}", e)
            }
        }
    }

    fun addAllergy(allergy: Allergy) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createAllergy(allergy)
                if (response.isSuccessful) {
                    response.body()?.let { newAllergy ->
                        _allergies.value = _allergies.value + newAllergy
                    }
                    Log.d("AllergyViewModel", "Alergias obtenidas: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AllergyViewModel", "Error al obtener Alergias: $errorBody")
                    errorMessage.value = "Error al obtener Alergias: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("AllergyViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("AllergyViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateAllergy(allergy: Allergy) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateAllergy(allergy.id, allergy)
                if (response.isSuccessful && response.body() != null) {
                    val updated = response.body()!!
                    _allergies.value = _allergies.value.map {
                        if (it.id == updated.id) updated else it
                    }
                    Log.i("AllergyViewModel", "Allergy updated: $updated")
                } else {
                    Log.e("UpdateAllergy", "Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateAllergy", "Error: ${e.message}")
            }
        }
    }

    fun deleteAllergy(allergyId: Int?) {
        allergyId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.deleteAllergy(id)
                    if (response.isSuccessful) {
                        _allergies.value = _allergies.value.filter { it.id != id }
                        Log.i("AllergyViewModel", "Allergy deleted with id: $id")
                    } else {
                        Log.e("DeleteAllergy", "Failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("DeleteAllergy", "Error: ${e.message}")
                }
            }
        } ?: Log.e("DeleteAllergy", "Error: allergyId is null")
    }
}
