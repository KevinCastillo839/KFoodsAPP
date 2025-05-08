package com.moviles.kfoods.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AllergyViewModel : ViewModel() {

    private val _allergies = MutableStateFlow<List<Allergy>>(emptyList())
    val allergies: StateFlow<List<Allergy>> get() = _allergies

    fun fetchAllergies() {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getAllAllergies()
                _allergies.value = result
                Log.i("AllergyViewModel", "Fetched allergies: $result")
            } catch (e: Exception) {
                Log.e("AllergyViewModelError", "Error fetching allergies: ${e.message}", e)
            }
        }
    }

    fun addAllergy(allergy: Allergy) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addAllergy(
                    name = allergy.name ?: "",
                    description = allergy.description ?: ""
                )
                if (response.isSuccessful) {
                    fetchAllergies()
                } else {
                    Log.e("AddAllergy", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AddAllergy", "Exception: ${e.message}")
            }
        }
    }

    fun updateAllergy(allergy: Allergy) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateAllergy(allergy.id, allergy)
                _allergies.value = _allergies.value.map {
                    if (it.id == response.id) response else it
                }
                Log.i("AllergyViewModel", "Allergy updated: $response")
            } catch (e: Exception) {
                Log.e("AllergyViewModelError", "Error updating allergy: ${e.message}", e)
            }
        }
    }

    fun deleteAllergy(allergyId: Int?) {
        allergyId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteAllergy(id)
                    _allergies.value = _allergies.value.filter { it.id != id }
                    Log.i("AllergyViewModel", "Allergy deleted with id: $id")
                } catch (e: Exception) {
                    Log.e("AllergyViewModelError", "Error deleting allergy: ${e.message}")
                }
            }
        } ?: Log.e("AllergyViewModelError", "Error: allergyId is null")
    }
}
