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
                if (response.isSuccessful) {
                    _allergies.value = response.body() ?: emptyList()
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
}
