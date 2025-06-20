package com.moviles.kfoods.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.ShoppingListDto
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel : ViewModel() {
    private val _shoppingList = MutableStateFlow<ShoppingListDto?>(null)
    val shoppingList: StateFlow<ShoppingListDto?> = _shoppingList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getWeeklyShoppingList(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.shoppingListApi.getShoppingListByUserId(userId)
                if (response.isSuccessful) {
                    val shoppingListDto = response.body()
                    if (shoppingListDto != null && shoppingListDto.success) {
                        _shoppingList.value = shoppingListDto
                    } else {
                        _errorMessage.value = "Error al cargar la lista de compras"
                    }
                } else {
                    _errorMessage.value = "Error HTTP ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}