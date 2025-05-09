import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.ApiResponse
import com.moviles.kfoods.models.LoginRequest
import com.moviles.kfoods.models.LoginResponse
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.ResetPassword
import com.moviles.kfoods.models.ResetPasswordRequest
import com.moviles.kfoods.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/allergy")
    suspend fun getAllergies(): Response<List<Allergy>>

    @POST("api/auth/register")
    suspend fun register(@Body  user: User): Response<ApiResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ResetPassword): Response<ApiResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>

    // === CRUD para alergias ===

    // Crear nueva alergia
    @POST("api/allergy")
    suspend fun createAllergy(@Body allergy: Allergy): Response<Allergy>

    // Actualizar una alergia existente
    @PUT("api/allergy/{id}")
    suspend fun updateAllergy(@Path("id") id: Int, @Body allergy: Allergy): Response<Allergy>

    // Eliminar una alergia
    @DELETE("api/allergy/{id}")
    suspend fun deleteAllergy(@Path("id") id: Int): Response<Unit>


    // Obtener todas las preferencias
    @GET("api/preference")
    suspend fun getPreferences(): Response<List<Preference>>

    // Obtener una preferencia por ID
    @GET("api/preference/{id}")
    suspend fun getPreferenceById(@Path("id") id: Int): Response<Preference>

    // Crear una nueva preferencia
    @POST("api/preference")
    suspend fun createPreference(@Body request: Preference): Response<Preference>

    // Actualizar una preferencia existente
    @PUT("api/preference/{id}")
    suspend fun updatePreference(@Path("id") id: Int, @Body request: Preference): Response<Preference>

    // Eliminar una preferencia por ID
    @DELETE("api/preference/{id}")
    suspend fun deletePreference(@Path("id") id: Int): Response<ApiResponse>

}
