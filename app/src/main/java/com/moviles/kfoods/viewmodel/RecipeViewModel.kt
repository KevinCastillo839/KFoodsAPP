package com.moviles.kfoods.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.moviles.kfoods.models.Recipe
import com.moviles.kfoods.models.dto.CreateRecipeRequestDto
import com.moviles.kfoods.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    fun getRecipesForUser(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.recipeApi.getRecipesForUser(userId)
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
    fun getRecipesByUser(userId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.recipeApi.getMyRecipes(userId)
                if (response.isSuccessful && response.body() != null) {
                    _recipe.value = response.body()!!
                    Log.i("RecipeViewModel", "Fetched Recipes for user $userId: ${response.body()}")
                } else {
                    Log.e("RecipeViewModel", "Fetch failed for user $userId: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching Recipes for user $userId: ${e.message}", e)
            } finally {
                _isLoading.value = false
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
    fun createRecipe(request: CreateRecipeRequestDto, imageFile: File?) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                fun String.toPart() = toRequestBody("text/plain".toMediaTypeOrNull())

                val namePart = request.name.toPart()
                val instructionsPart = request.instructions.toPart()
                val categoryPart = request.category.toPart()
                val prepTimePart = request.preparation_time.toString().toPart()
                val createdAtPart = request.created_at.toPart()
                val updatedAtPart = request.updated_at?.toPart()
                val userIdPart = request.user_id.toString().toPart()

                val gson = Gson()
                // Aquí asumo que Recipe_IngredientsJson es un String JSON ya serializado,
                // si es lista, serializarlo primero con gson.toJson(request.Recipe_IngredientsJson)
                val ingredientsJsonString = request.Recipe_IngredientsJson
                val ingredientsPart = ingredientsJsonString.toRequestBody("application/json".toMediaType())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                val response = RetrofitInstance.recipeApi.createMyRecipe(
                    name = namePart,
                    instructions = instructionsPart,
                    category = categoryPart,
                    preparationTime = prepTimePart,
                    createdAt = createdAtPart,
                    updatedAt = updatedAtPart,
                    userId = userIdPart,
                    recipeIngredientsJson = ingredientsPart,
                    image = imagePart  // Aquí pasas el archivo multipart
                )

                if (response.isSuccessful && response.body() != null) {
                    val createdRecipe = response.body()!!
                    Log.i("RecipeViewModel", "Recipe created successfully: $createdRecipe")
                    _errorMessage.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error creating recipe: ${response.code()} ${response.message()}" +
                            (if (errorBody != null) "\nDetails: $errorBody" else "")
                    Log.e("RecipeViewModel", "Error creating recipe: ${response.code()} ${response.message()}\n$errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception creating recipe: ${e.message}"
                Log.e("RecipeViewModel", "Exception creating recipe", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateRecipe(
        recipeId: Int,
        request: CreateRecipeRequestDto,
        imageFile: File?
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                fun String.toPart() = toRequestBody("text/plain".toMediaTypeOrNull())

                val namePart = request.name.toPart()
                val instructionsPart = request.instructions.toPart()
                val categoryPart = request.category.toPart()
                val prepTimePart = request.preparation_time.toString().toPart()
                val createdAtPart = request.created_at.toPart()
                val updatedAtPart = request.updated_at?.toPart()
                val userIdPart = request.user_id.toString().toPart()

                val ingredientsJsonString = request.Recipe_IngredientsJson
                val ingredientsPart = ingredientsJsonString.toRequestBody("application/json".toMediaType())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                val response = RetrofitInstance.recipeApi.updateRecipe(
                    id = recipeId,
                    name = namePart,
                    instructions = instructionsPart,
                    category = categoryPart,
                    preparationTime = prepTimePart,
                    updatedAt = updatedAtPart!!, // recuerda que puede ser nullable, ajusta si es necesario
                    recipeIngredientsJson = ingredientsPart,
                    image = imagePart
                )


                if (response.isSuccessful && response.body() != null) {
                    val updatedRecipe = response.body()!!
                    Log.i("RecipeViewModel", "Recipe updated successfully: $updatedRecipe")
                    _errorMessage.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error updating recipe: ${response.code()} ${response.message()}" +
                            (if (errorBody != null) "\nDetails: $errorBody" else "")
                    Log.e("RecipeViewModel", "Error updating recipe: ${response.code()} ${response.message()}\n$errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception updating recipe: ${e.message}"
                Log.e("RecipeViewModel", "Exception updating recipe", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun deleteRecipe(recipeId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.recipeApi.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    Log.i("RecipeViewModel", "Recipe deleted successfully: $recipeId")
                    // Actualiza la lista local si quieres, por ejemplo, recarga recetas
                    // o elimina manualmente la receta de _recipe.value
                    _errorMessage.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error deleting recipe: ${response.code()} ${response.message()}" +
                            (if (errorBody != null) "\nDetails: $errorBody" else "")
                    Log.e("RecipeViewModel", "Error deleting recipe: ${response.code()} ${response.message()}\n$errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception deleting recipe: ${e.message}"
                Log.e("RecipeViewModel", "Exception deleting recipe", e)
            } finally {
                _isLoading.value = false
            }
        }
    }



}