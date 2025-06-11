package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.io.IOException

class UserAllergyViewModel (application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)


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
}