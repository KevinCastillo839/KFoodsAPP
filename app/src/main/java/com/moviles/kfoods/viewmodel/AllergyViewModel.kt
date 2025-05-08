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
