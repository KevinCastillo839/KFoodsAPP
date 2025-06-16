import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.LoginRequest
import com.moviles.kfoods.models.dto.LoginResponse
import com.moviles.kfoods.models.dto.ResetPassword
import com.moviles.kfoods.models.dto.ResetPasswordRequest
import com.moviles.kfoods.models.User
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.models.dto.RegisterResponse
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

/*@GET("api/allergy")
    suspend fun getAllergies(): Response<List<Allergy>>
*/
    @POST("api/auth/register")
    suspend fun register(@Body  user: User): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ResetPassword): Response<ApiResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    // UserAllergy
    @POST("api/user_allergy")
    suspend fun createUserAllergy(@Body request: UserAllergy): Response<Preference>

    @PUT("api/user_allergy/{userId}")
    suspend fun updateUserAllergy(@Path("userId") id: Int, @Body userAllergy: UserAllergy): Response<UserAllergy>

    @GET("api/user_allergy/{userId}")
    suspend fun getAllergiesByUserId(@Path("userId") id: Int): Response<List<UserAllergy>>

    @GET("api/allergy")
    suspend fun getAllergies(): Response<List<Allergy>>

    @POST("api/allergy")
    suspend fun createAllergy(@Body allergy: Allergy): Response<Allergy>

    @PUT("api/allergy/{id}")
    suspend fun updateAllergy(@Path("id") id: Int, @Body allergy: Allergy): Response<Allergy>

    @DELETE("api/allergy/{id}")
    suspend fun deleteAllergy(@Path("id") id: Int): Response<ApiResponse>

}
