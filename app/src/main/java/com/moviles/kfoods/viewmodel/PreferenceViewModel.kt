package com.moviles.kfoods.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.network.RetrofitInstance
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.models.UserPreference
import com.moviles.kfoods.models.dto.CreatePreferenceRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    val preferences = MutableLiveData<List<Preference>>()
    val selectedPreference = MutableLiveData<Preference?>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)
    private val _dietaryRestriction = MutableStateFlow<List<DietaryRestriction>>(emptyList())
    val dietaryRestriction: StateFlow<List<DietaryRestriction>> = _dietaryRestriction

    private val _dietaryGoal = MutableStateFlow<List<DietaryGoal>>(emptyList())
    val dietaryGoal: StateFlow<List<DietaryGoal>> = _dietaryGoal

    private val _userPreference = MutableStateFlow<UserPreference?>(null)
    val userPreference: StateFlow<UserPreference?> = _userPreference


    private val _preferenceId = MutableStateFlow<Int?>(null)
    val preferenceId: StateFlow<Int?> get() = _preferenceId

    fun fetchUserPreferences(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.getUserPreferences(userId)
                if (response.isSuccessful && response.body() != null) {
                    _userPreference.value = response.body()
                    Log.i("UserPreferenceViewModel", "Fetched UserPreferences: ${response.body()}")
                } else {
                    Log.e("UserPreferenceViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserPreferenceViewModelError", "Error fetching UserPreferences: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getAllDietaryGoal() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.getDietaryGoal()
                if (response.isSuccessful && response.body() != null) {
                    _dietaryGoal.value = response.body()!!
                    Log.i("DietaryGoalViewModel", "Fetched DietaryGoal: ${response.body()}")
                } else {
                    Log.e("DietaryGoalViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DietaryGoalViewModelError", "Error fetching DietaryGoal: ${e.message}", e)
            }
        }
    }
    fun getAllDietaryRestriction() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.getDietaryRestriction()
                if (response.isSuccessful && response.body() != null) {
                    _dietaryRestriction.value = response.body()!!
                    Log.i("DietaryRestrictionViewModel", "Fetched DietaryRestriction: ${response.body()}")
                } else {
                    Log.e("DietaryRestrictionViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DietaryRestrictionViewModelError", "Error fetching DietaryRestriction: ${e.message}", e)
            }
        }
    }


    fun createPreferences(request: CreatePreferenceRequestDto) {
        viewModelScope.launch {
            val id = createPreference(request)
            if (id != null) {
                _preferenceId.value = id
            }
        }
    }
    suspend fun createPreference(requestP: CreatePreferenceRequestDto): Int? {
        return try {
            val response = RetrofitInstance.preferenceApi.createPreference(requestP)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.id // Returns the generated ID
            } else {
                val errorBody = response.errorBody()?.string()
                val code = response.code()
                val message = response.message()

                Log.e("PreferenceViewModel", "Error al crear preferencia:")
                Log.e("PreferenceViewModel", "Código HTTP: $code")
                Log.e("PreferenceViewModel", "Mensaje: $message")
                Log.e("PreferenceViewModel", "Cuerpo error: $errorBody")

                null
            }
        } catch (e: IOException) {
            Log.e("PreferenceViewModel", "Error de red preferencia: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("PreferenceViewModel", "Error inesperado: ${e.message}")
            null
        }
    }

    fun updateDietaryGoal(dietaryGoal: UserDietaryGoal) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.preferenceApi.updateDietaryGoal(dietaryGoal)
                if (response.isSuccessful && response.body() != null) {
                    val updatedGoal = response.body()!!
                    successMessage.value = "Goal actualizada con éxito"
                    Log.i("DietaryGoalViewModel", "Dietary goal updated: $updatedGoal")
                } else {
                    Log.e("UpdateDietaryGoal", "Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateDietaryGoal", "Error: ${e.message}")
            }
        }
    }
    fun updateDietaryRestriction(dietaryRestriction: UserDietaryRestriction) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.preferenceApi.updateDietaryRestriction(dietaryRestriction)
                if (response.isSuccessful && response.body() != null) {
                    val updatedRestriction = response.body()!!
                    successMessage.value = "Restriction actualizada con éxito"
                    Log.i("DietaryRestrictionViewModel", "Dietary restriction updated: $updatedRestriction")
                } else {
                    Log.e("UpdateDietaryRestriction", "Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateDietaryRestriction", "Error: ${e.message}")
            }
        }
    }

    fun createDietaryGoals(requestG: UserDietaryGoal) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val goalResponse = RetrofitInstance.preferenceApi.createDietaryGoal(requestG)
                if (goalResponse.isSuccessful) {
                    goalResponse.body()?.let { createdGoal ->
                        Log.d("DietaryGoalViewModel", "Objetivo creado con éxito: $createdGoal")
                    }
                } else {
                    val errorBody = goalResponse.errorBody()?.string()
                    Log.e("DietaryGoalViewModel", "Error al crear objetivo: $errorBody")
                    errorMessage.value = "Error al crear objetivo: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("DietaryGoalViewModel", "Error de red dietary: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("DietaryGoalViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createDietaryRestrictions(requestR: UserDietaryRestriction) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val restrictionResponse = RetrofitInstance.preferenceApi.createDietaryRestriction(requestR)
                if (restrictionResponse.isSuccessful) {
                    Log.d("DietaryRestrictionViewModel", "Restricción creada con éxito: $requestR")

                } else {
                    val errorBody = restrictionResponse.errorBody()?.string()
                    Log.e("DietaryRestrictionViewModel", "Error al crear restricciones: $errorBody")
                    errorMessage.value = "Error al crear restricciones: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("DietaryRestrictionViewModel", "Error de red restriction: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("DietaryRestrictionViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

}
