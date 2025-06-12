package com.moviles.kfoods.ui.theme.recipe

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.DragAndDropPermissionsCompat.request
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.moviles.kfoods.models.Recipe
import com.moviles.kfoods.models.dto.CreateRecipeIngredientDto
import com.moviles.kfoods.models.dto.CreateRecipeRequestDto
import com.moviles.kfoods.models.dto.IngredientDto
import com.moviles.kfoods.models.dto.RecipeDto
import com.moviles.kfoods.viewmodel.IngredientViewModel
import com.moviles.kfoods.viewmodel.RecipeViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    userId: Int,
    navController: NavController,
    recipeViewModel: RecipeViewModel = viewModel(),
    ingredientViewModel: IngredientViewModel = viewModel(), // Parámetro para la receta a editar (null si crear)
     recipeId: Int? = null
    ) {
    // Obtener la receta del ViewModel como StateFlow y observarla
    val recipeState by recipeViewModel.recipeDetail.collectAsState()

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            recipeViewModel.getRecipesById(recipeId)
        }
    }
    // Lista de ingredientes disponibles
    val ingredientList = ingredientViewModel.ingredientList
    val scrollState = rememberScrollState()


    // Estados para el formulario, inicializados con datos existentes si existen
    val recipeName = remember { mutableStateOf(recipeState?.name ?: "") }
    val instructions = remember { mutableStateOf(recipeState?.instructions ?: "") }
    val category = remember { mutableStateOf(recipeState?.category ?: "") }
    val prepTimeText = remember { mutableStateOf(recipeState?.preparation_time?.toString() ?: "") }

    // Dropdown estado
    val ingredientDropdownExpanded = remember { mutableStateOf(false) }
    val categoryDropdownExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar imagen existente si hay (puedes ajustar según cómo guardas url o path)
    val existingImageUrl = recipeState?.image_url

    // Ingredientes seleccionados, inicializados con los actuales si existen
    data class IngredientSelection(
        val ingredient: IngredientDto,
        var quantity: String = "",
        var unit: String = ""
    )
    val selectedIngredients = remember(recipeState) {
        mutableStateListOf<IngredientSelection>().apply {
            clear()
            recipeState?.recipe_ingredients?.forEach { ri ->
                val ingredient = ingredientList.find { it.id == ri.ingredient_id }
                if (ingredient != null) {
                    val parts = ri.quantity.split(" ", limit = 2)
                    val qty = parts.getOrNull(0) ?: ""
                    val unit = parts.getOrNull(1) ?: ""
                    add(IngredientSelection(ingredient, qty, unit))
                }
            }
        }
    }
    // Open the image picker.
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val categories = listOf("Postre", "Entrada", "Plato fuerte", "Bebida")

    LaunchedEffect(Unit) {
        ingredientViewModel.fetchIngredients()
    }

    val nowIsoString = remember {
        DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(if (recipeState == null) "Crear Receta" else "Editar Receta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = recipeName.value,
            onValueChange = { recipeName.value = it },
            label = { Text("Nombre de la receta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = instructions.value,
            onValueChange = { instructions.value = it },
            label = { Text("Instrucciones") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded.value,
            onExpandedChange = { categoryDropdownExpanded.value = !categoryDropdownExpanded.value }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = category.value,
                onValueChange = {},
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded.value) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryDropdownExpanded.value,
                onDismissRequest = { categoryDropdownExpanded.value = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category.value = cat
                            categoryDropdownExpanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prepTimeText.value,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    prepTimeText.value = newValue
                }
            },
            label = { Text("Tiempo de preparación (minutos)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar imagen", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar imagen seleccionada o existente
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else if (existingImageUrl != null) {
            // Aquí podrías cargar la imagen existente usando Coil o similar, por ejemplo:
            Image(
                painter = rememberAsyncImagePainter(existingImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Agregar Ingredientes")

        ExposedDropdownMenuBox(
            expanded = ingredientDropdownExpanded.value,
            onExpandedChange = { ingredientDropdownExpanded.value = !ingredientDropdownExpanded.value }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = "",
                onValueChange = {},
                label = { Text("Seleccionar ingrediente") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ingredientDropdownExpanded.value) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = ingredientDropdownExpanded.value,
                onDismissRequest = { ingredientDropdownExpanded.value = false }
            ) {
                ingredientList.forEach { ingredient ->
                    DropdownMenuItem(
                        text = { Text(ingredient.name) },
                        onClick = {
                            if (selectedIngredients.none { it.ingredient.id == ingredient.id }) {
                                selectedIngredients.add(IngredientSelection(ingredient))
                            }
                            ingredientDropdownExpanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedIngredients.forEachIndexed { index, selection ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(selection.ingredient.name, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = selection.quantity,
                    onValueChange = { newQty ->
                        selectedIngredients[index] = selection.copy(quantity = newQty)
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selection.unit,
                    onValueChange = { newUnit ->
                        selectedIngredients[index] = selection.copy(unit = newUnit)
                    },
                    label = { Text("Unidad") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validaciones
                if (recipeName.value.isBlank()) {
                    Toast.makeText(context, "Ingrese el nombre de la receta", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (instructions.value.isBlank()) {
                    Toast.makeText(context, "Ingrese las instrucciones", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (category.value.isBlank()) {
                    Toast.makeText(context, "Seleccione una categoría", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (prepTimeText.value.isBlank()) {
                    Toast.makeText(context, "Ingrese el tiempo de preparación", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (selectedIngredients.isEmpty()) {
                    Toast.makeText(context, "Agregue al menos un ingrediente", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val now = LocalDateTime.now()
                val nowIso = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                // Obtener File a partir de imageUri
                val file = imageUri?.let { uri ->
                    getFileFromUri(context, uri)
                }

                // Crear lista de ingredientes DTO
                val ingredientsList = selectedIngredients.map { sel ->
                    CreateRecipeIngredientDto(
                        id = 0,
                        recipe_id = recipeState?.id ?: 0,
                        ingredient_id = sel.ingredient.id,
                        quantity = "${sel.quantity} ${sel.unit}".trim(),
                        created_at = nowIso,
                        updated_at = nowIso
                    )
                }

                val gson = Gson()
                val ingredientsJsonString = gson.toJson(ingredientsList)

                val request = CreateRecipeRequestDto(
                    name = recipeName.value.trim(),
                    instructions = instructions.value.trim(),
                    image_url = imageUri?.toString() ?: recipeState?.image_url,
                    category = category.value,
                    preparation_time = prepTimeText.value.toIntOrNull() ?: 0,
                    created_at =  nowIso,
                    updated_at = nowIso,
                    Recipe_IngredientsJson = ingredientsJsonString,
                    user_id = userId
                )

                val currentRecipe = recipeState  // guarda el valor actual

                if (currentRecipe == null) {
                    Log.i("RecipeForm", "Creando receta: $request")
                    recipeViewModel.createRecipe(request, file)
                } else {
                    Log.i("RecipeForm", "Editando receta ID ${currentRecipe.id}: $request")
                    recipeViewModel.updateRecipe(currentRecipe.id, request, file)
                }


                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (recipeState == null) "Crear Receta" else "Guardar Cambios")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}




@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    onImageSelected: (Uri?) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        onImageSelected(uri)
    }

    Column(modifier = modifier) {
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Seleccionar imagen", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        }
    }
}
fun getFileFromUri(context: Context, uri: Uri): File? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("event_image_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)

        return tempFile
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}


