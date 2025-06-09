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

    private val _preferenceId = MutableStateFlow<Int?>(null)
    val preferenceId: StateFlow<Int?> get() = _preferenceId

    fun getAllPreferences() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.getPreferences()
                if (response.isSuccessful) {
                    preferences.value = response.body()
                    Log.d("PreferenceViewModel", "Preferencias obtenidas: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PreferenceViewModel", "Error al obtener preferencias: $errorBody")
                    errorMessage.value = "Error al obtener preferencias: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("PreferenceViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("PreferenceViewModel", "Error inesperado: ${e.message}")
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
                    Log.i("AllergyViewModel", "Fetched allergies: ${response.body()}")
                } else {
                    Log.e("AllergyViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AllergyViewModelError", "Error fetching allergies: ${e.message}", e)
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
                    Log.i("AllergyViewModel", "Fetched allergies: ${response.body()}")
                } else {
                    Log.e("AllergyViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AllergyViewModelError", "Error fetching allergies: ${e.message}", e)
            }
        }
    }

    fun getPreferenceById(id: Int) {
        if (id <= 0) {
            errorMessage.value = "El ID proporcionado no es válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.getPreferenceById(id)
                if (response.isSuccessful) {
                    selectedPreference.value = response.body()
                    Log.d("PreferenceViewModel", "Preferencia obtenida: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PreferenceViewModel", "Error al obtener preferencia: $errorBody")
                    errorMessage.value = "Error al obtener preferencia: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("PreferenceViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("PreferenceViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createPreferences(request: CreatePreferenceRequestDto) {
        viewModelScope.launch {
            _preferenceId.value = createPreference(request)
        }
    }


    suspend fun createPreference(requestP: CreatePreferenceRequestDto): Int? {
        return try {
            val response = RetrofitInstance.preferenceApi.createPreference(requestP)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.id // Retorna el ID generado
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




    fun createDietaryGoals(requestG: UserDietaryGoal) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val goalResponse = RetrofitInstance.preferenceApi.createDietaryGoal(requestG)
                if (goalResponse.isSuccessful) {
                    goalResponse.body()?.let { createdGoal ->
                        // Maneja la respuesta exitosa aquí, si es necesario, como actualizar un LiveData
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

    fun updatePreference(id: Int, request: Preference) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.updatePreference(id, request)
                if (response.isSuccessful) {
                    successMessage.value = "Preferencia actualizada con éxito"
                    Log.d("PreferenceViewModel", "Preferencia actualizada: ${response.body()}")
                    getAllPreferences() // Refrescar la lista de preferencias
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PreferenceViewModel", "Error al actualizar preferencia: $errorBody")
                    errorMessage.value = "Error al actualizar preferencia: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("PreferenceViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("PreferenceViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deletePreference(id: Int) {
        if (id <= 0) {
            errorMessage.value = "El ID proporcionado no es válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.preferenceApi.deletePreference(id)
                if (response.isSuccessful) {
                    successMessage.value = "Preferencia eliminada con éxito"
                    Log.d("PreferenceViewModel", "Preferencia eliminada: $id")
                    getAllPreferences() // Refrescar la lista de preferencias
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PreferenceViewModel", "Error al eliminar preferencia: $errorBody")
                    errorMessage.value = "Error al eliminar preferencia: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("PreferenceViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("PreferenceViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}
