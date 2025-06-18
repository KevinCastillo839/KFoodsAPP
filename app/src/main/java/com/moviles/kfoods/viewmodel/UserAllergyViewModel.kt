package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserAllergyViewModel (application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)
    private val _userAllergy= MutableStateFlow<List<UserAllergy>>(emptyList())
    val userAllergy: StateFlow<List<UserAllergy>> = _userAllergy

    fun fetchUserAllergy(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getAllergiesByUserId(userId)
                if (response.isSuccessful && response.body() != null) {
                    _userAllergy.value = response.body()!!
                    Log.i("UserAllergyViewModel", "Fetched UserAllergy: ${response.body()}")
                } else {
                    Log.e("UserAllergyViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserAllergyViewModelError", "Error fetching UserAllergy: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
    fun createUserAllergy(request: UserAllergy) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.createUserAllergy(request)
                if (response.isSuccessful) {
                    successMessage.value = "Alergia guardada con éxito"
                    Log.d("UserAllergyViewModel", "Alergia creada: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserAllergyViewModel", "Error al crear Alergia: $errorBody")
                    errorMessage.value = "Error al crear Alergia: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("UserAllergyViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("UserAllergyViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    fun updateUserAllergy(userId: Int,userAllergy: UserAllergy) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateUserAllergy(userId,userAllergy)
                if (response.isSuccessful && response.body() != null) {
                    val updatedUserAllergy = response.body()!!
                    successMessage.value = "Goal actualizada con éxito"
                    Log.i("UserAllergyViewModel", "Dietary goal updated: $updatedUserAllergy")
                } else {
                    Log.e("UpdateUserAllergy", "Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateUserAllergy", "Error: ${e.message}")
            }
        }
    }

}