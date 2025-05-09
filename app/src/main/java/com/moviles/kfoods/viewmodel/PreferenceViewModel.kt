package com.moviles.kfoods.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.network.RetrofitInstance
import com.moviles.kfoods.models.Preference
import kotlinx.coroutines.launch
import java.io.IOException

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    val preferences = MutableLiveData<List<Preference>>()
    val selectedPreference = MutableLiveData<Preference?>()
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)

    fun getAllPreferences() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getPreferences()
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

    fun getPreferenceById(id: Int) {
        if (id <= 0) {
            errorMessage.value = "El ID proporcionado no es válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getPreferenceById(id)
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

    fun createPreference(request: Preference) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.createPreference(request)
                if (response.isSuccessful) {
                    successMessage.value = "Preferencia creada con éxito"
                    Log.d("PreferenceViewModel", "Preferencia creada: ${response.body()}")
                    getAllPreferences() // Refrescar la lista de preferencias
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PreferenceViewModel", "Error al crear preferencia: $errorBody")
                    errorMessage.value = "Error al crear preferencia: $errorBody"
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

    fun updatePreference(id: Int, request: Preference) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.updatePreference(id, request)
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
                val response = RetrofitInstance.api.deletePreference(id)
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
