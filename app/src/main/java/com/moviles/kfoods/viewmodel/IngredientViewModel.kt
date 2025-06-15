package com.moviles.kfoods.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.dto.CreateIngredientRequestDto
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertIngredient(
        name: String,
        description: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val now = java.time.LocalDateTime.now().toString()
                val newIngredient = CreateIngredientRequestDto(
                    name = name,
                    description = description,
                    created_at = now,
                    updated_at = null
                )

                val response = api.createIngredient(newIngredient)
                if (response.isSuccessful) {
                    response.body()?.let {
                        ingredientList.add(it)
                        onResult(true)
                    } ?: onResult(false)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

}
