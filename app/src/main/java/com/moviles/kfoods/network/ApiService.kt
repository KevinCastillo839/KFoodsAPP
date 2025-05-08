import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.LoginRequest
import com.moviles.kfoods.models.dto.LoginResponse
import com.moviles.kfoods.models.dto.ResetPassword
import com.moviles.kfoods.models.dto.ResetPasswordRequest
import com.moviles.kfoods.models.User
import com.moviles.kfoods.models.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/allergy")
    suspend fun getAllergies(): Response<List<Allergy>>

    @POST("api/auth/register")
    suspend fun register(@Body  user: User): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ResetPassword): Response<ApiResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>



}
