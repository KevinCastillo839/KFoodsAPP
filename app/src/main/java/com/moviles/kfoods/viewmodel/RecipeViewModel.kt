package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.kfoods.models.Recipe
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _recipe = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipe

    private val _recipeDetail = MutableStateFlow<Recipe?>(null)
    val recipeDetail: StateFlow<Recipe?> = _recipeDetail

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getRecipesForUser(usarId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.recipeApi.getRecipesForUser(usarId)
                if (response.isSuccessful && response.body() != null) {
                    _recipe.value = response.body()!!
                    Log.i("RecipeViewModel", "Fetched Recipe: ${response.body()}")
                } else {
                    Log.e("RecipeViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching Recipe: ${e.message}", e)
            }
        }
    }

    fun getRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.recipeApi.getRecipes()
                if (response.isSuccessful && response.body() != null) {
                    _recipe.value = response.body()!!
                    Log.i("RecipeViewModel", "Fetched Recipe: ${response.body()}")
                } else {
                    Log.e("RecipeViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching Recipe: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getRecipesById(recipeId: Int?) {
        if (recipeId == null) {
            _errorMessage.value = "Invalid recipe ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.recipeApi.getRecipesById(recipeId)
                if (response.isSuccessful && response.body() != null) {
                    _recipeDetail.value = response.body()!!
                    Log.i("RecipeViewModel", "Fetched recipe: ${response.body()}")
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                    Log.e("RecipeViewModel", "Fetch failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("RecipeViewModel", "Error fetching recipe: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}