package com.moviles.kfoods.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.LoginRequest
import com.moviles.kfoods.models.dto.LoginResponse
import com.moviles.kfoods.models.dto.ResetPassword
import com.moviles.kfoods.models.dto.ResetPasswordRequest
import com.moviles.kfoods.models.User
import com.moviles.kfoods.network.RetrofitInstance
import com.moviles.kfoods.util.SharedPreferencesManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewModelScope


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext

    val registrationResult = MutableLiveData<Boolean>()
    var isLoading = mutableStateOf(false)
    var successMessage = mutableStateOf("")
    val userResult = MutableLiveData<User?>()
    val loginResult = MutableLiveData<LoginResponse?>()
    val errorMessage = MutableLiveData<String>()
    private val sharedPreferencesManager = SharedPreferencesManager(context)
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitInstance.api.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d("AuthViewModel", "Mensaje: ${loginResponse.message}")
                        Log.d("AuthViewModel", "Token: ${loginResponse.token}")

                        loginResult.value = loginResponse

                        // Aquí guardamos el token
                        sharedPreferencesManager.saveToken(loginResponse.token)

                        // Y después llamamos a getAllergies()
                        getAllergies()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthViewModel", "Error en login: Código: ${response.code()}, Error: $errorBody")
                    errorMessage.value = "Correo o contraseña incorrectos. Intenta nuevamente."
                }
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
            }
        }
    }

    private fun getAllergies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllergies()
                if (response.isSuccessful) {
                    val allergies = response.body()
                    Log.d("AuthViewModel", "Alergias recibidas: $allergies")
                } else {
                    Log.e("AuthViewModel", "Error al obtener alergias: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error inesperado al obtener alergias: ${e.message}")
            }
        }
    }
    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            try {
                val createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
                val cleanedEmail = email.trim()
                val user = User(
                    email = cleanedEmail,
                    password = password,
                    full_name = fullName,
                    created_at = createdAt
                )

                val response = RetrofitInstance.api.register(user)

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        // Logeamos el ID y el Token
                        Log.d("AuthViewModel", "RegistroExitoso -> userId: ${registerResponse.userId}, token: ${registerResponse.token}")

                        // Guardar el token recibido
                        sharedPreferencesManager.saveToken(registerResponse.token)

                        // Después de guardar el token, obtenemos alergias
                        getAllergies()

                        registrationResult.value = true
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthViewModel", "Error en el registro: Código: ${response.code()}, Error: $errorBody")
                    errorMessage.value = errorBody ?: "Error desconocido"
                    registrationResult.value = false
                }
            } catch (e: HttpException) {
                errorMessage.value = "Error en la conexión: ${e.message()}"
                registrationResult.value = false
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                registrationResult.value = false
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                registrationResult.value = false
            }
        }
    }



    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading.value = true
            successMessage.value = ""
            errorMessage.value = ""

            try {
                val request = ResetPassword(email)
                val response: Response<ApiResponse> = RetrofitInstance.api.forgotPassword(request)

                if (response.isSuccessful && response.body() != null) {
                    successMessage.value = response.body()!!.message
                } else {
                    errorMessage.value = response.errorBody()?.string() ?: "Error desconocido al enviar correo"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de red: ${e.localizedMessage ?: "desconocido"}"
            } finally {
                isLoading.value = false
            }
        }
    }
    fun resetPassword(
        email: String,
        token: String,
        newPassword: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Mostrar la nueva contraseña en el Logcat
                Log.d("ResetPassword", "Nueva contraseña: $newPassword")

                val request = ResetPasswordRequest(email, token, newPassword)
                val response = RetrofitInstance.api.resetPassword(request)

                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.message)
                } else {
                    onError("Error al restablecer la contraseña: ${response.errorBody()?.string() ?: "Error desconocido"}")
                }
            } catch (e: Exception) {
                onError("Excepción: ${e.message}")
            }
        }
    }

    fun getUserById(userId: Int) {
        Log.d("AuthViewModel", "Iniciando la obtención del usuario con ID: $userId") // Log inicial

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getUserById(userId)
                Log.d("AuthViewModel", "Respuesta recibida: $response") // Log de la respuesta

                if (response.isSuccessful) {
                    val user = response.body()
                    Log.d("AuthViewModel", "Usuario obtenido exitosamente: $user") // Log de éxito

                    userResult.value = user // Establecer el resultado
                    errorMessage.value = "" // Limpiar cualquier error
                } else {
                    Log.e("AuthViewModel", "Error en la respuesta: Código ${response.code()}") // Log de error de respuesta
                    errorMessage.value = "Error al obtener el usuario"
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción al obtener el usuario: ${e.message}", e) // Log de excepción
                errorMessage.value = "Error inesperado: ${e.message}"
            }
        }
    }

}


private fun getSimplifiedMessage(responseBody: String?): String {
    return responseBody?.let {
        // Aquí puedes agregar lógica adicional si el cuerpo es JSON o tiene formato complejo
        try {
            val regex = "\"message\"\\s*:\\s*\"(.*?)\"".toRegex()  // Regex para extraer el mensaje
            val matchResult = regex.find(it)
            matchResult?.groups?.get(1)?.value ?: "Mensaje no encontrado"
        } catch (e: Exception) {
            "Error al procesar el mensaje"
        }
    } ?: "Respuesta vacía"
}






