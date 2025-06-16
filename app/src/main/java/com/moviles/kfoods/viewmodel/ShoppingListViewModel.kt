package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.ShoppingListDto
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _shoppingList = MutableStateFlow<ShoppingListDto?>(null)
    val shoppingList: StateFlow<ShoppingListDto?> = _shoppingList
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)

    fun getWeeklyShoppingList(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getWeeklyShoppingList(userId)
                if (response.isSuccessful && response.body() != null) {
                    _shoppingList.value = response.body()!!
                    Log.i("ShoppingListViewModel", "Fetched shopping list: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ShoppingListViewModel", "Fetch failed: ${response.code()}, error: $errorBody")
                    errorMessage.value = "Error al obtener la lista de compras: ${errorBody ?: "Código ${response.code()}"}"
                }
            } catch (e: IOException) {
                Log.e("ShoppingListViewModel", "Network error: ${e.message}", e)
                errorMessage.value = "Error de red: ${e.message}"
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Unexpected error: ${e.message}", e)
                errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}