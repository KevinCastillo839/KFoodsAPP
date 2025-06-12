package com.moviles.kfoods.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.IngredientDto
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.launch

class IngredientViewModel : ViewModel() {
    private val api = RetrofitInstance.ingredientApi

    val ingredientList = mutableStateListOf<IngredientDto>()

    fun fetchIngredients() {
        viewModelScope.launch {
            val response = api.getAllIngredients()
            if (response.isSuccessful) {
                response.body()?.let {
                    ingredientList.clear()
                    ingredientList.addAll(it)
                }
            }
        }
    }
}
