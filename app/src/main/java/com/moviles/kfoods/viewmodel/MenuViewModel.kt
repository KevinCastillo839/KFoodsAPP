package com.moviles.kfoods.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.WeeklyMenuResponse
import com.moviles.kfoods.network.RetrofitInstance.menuApi
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MenuViewModelFactory(private val userId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




class MenuViewModel(private val userId: Int) : ViewModel() {

    private val _weeklyMenus = MutableLiveData<List<WeeklyMenuResponse>>()
    val weeklyMenus: LiveData<List<WeeklyMenuResponse>> = _weeklyMenus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    private val _generateMenuResult = MutableLiveData<Pair<Boolean, String>?>()
    val generateMenuResult: LiveData<Pair<Boolean, String>?> = _generateMenuResult
    init {
        fetchWeeklyMenu()
    }

    fun fetchWeeklyMenu() {
        Log.d("DEBUG", "fetchWeeklyMenu() called")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<WeeklyMenuResponse> = menuApi.getWeeklyMenu(1)

                Log.d("WeeklyMenu", response.toString())

                if (response.isSuccessful && response.body() != null) {
                    _weeklyMenus.value = listOf(response.body()!!)
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al obtener el menú semanal más reciente"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateWeeklyMenu() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = menuApi.generateWeeklyMenu(userId)
                if (response.isSuccessful) {
                    _generateMenuResult.value = Pair(true, response.body()?.message ?: "Menú generado exitosamente.")
                    fetchWeeklyMenu()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    val errorMessage = try {
                        JSONObject(errorBody).getString("message")
                    } catch (e: Exception) {
                        errorBody
                    }
                    _generateMenuResult.value = Pair(false, "Error ${response.code()}: $errorMessage")
                }
            } catch (e: Exception) {
                _generateMenuResult.value = Pair(false, "Excepción: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearGenerateMenuResult() {
        _generateMenuResult.value = null
    }

}

