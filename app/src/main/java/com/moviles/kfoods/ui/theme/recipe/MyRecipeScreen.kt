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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.moviles.kfoods.models.dto.CreateRecipeIngredientDto
import com.moviles.kfoods.models.dto.CreateRecipeRequestDto
import com.moviles.kfoods.models.dto.IngredientDto
import com.moviles.kfoods.viewmodel.IngredientViewModel
import com.moviles.kfoods.viewmodel.RecipeViewModel
import com.moviles.kfoods.viewmodel.UnitMeasurementViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    userId: Int,
    navController: NavController,
    recipeViewModel: RecipeViewModel = viewModel(),
    ingredientViewModel: IngredientViewModel = viewModel(),
    recipeId: Int? = null
) {
    val recipeState by recipeViewModel.recipeDetail.collectAsState()

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            recipeViewModel.getRecipesById(recipeId)
        }
    }
    val unitViewModel: UnitMeasurementViewModel = viewModel()
    val unitMeasurements by unitViewModel.unitMeasurements.collectAsState()

    LaunchedEffect(Unit) {
        unitViewModel.fetchUnitMeasurements()
    }

    val ingredientList = ingredientViewModel.ingredientList
    val scrollState = rememberScrollState()

    val recipeName = remember { mutableStateOf("") }
    val instructions = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val prepTimeText = remember { mutableStateOf("") }

    LaunchedEffect(recipeState) {
        recipeState?.let { recipe ->
            recipeName.value = recipe.name
            instructions.value = recipe.instructions
            category.value = recipe.category
            prepTimeText.value = recipe.preparation_time?.toString() ?: ""
        }
    }


    val ingredientDropdownExpanded = remember { mutableStateOf(false) }
    val categoryDropdownExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val existingImageUrl = recipeState?.image_url

    data class IngredientSelection(
        val ingredient: IngredientDto,
        var quantity: Double = 0.0,
        var unitId: Int? = null
    )

    val selectedIngredients = remember { mutableStateListOf<IngredientSelection>() }

    LaunchedEffect(recipeState) {
        selectedIngredients.clear()
        recipeState?.recipe_ingredients?.forEach { ri ->
            val ingredient = ingredientList.find { it.id == ri.ingredient_id }
            if (ingredient != null) {
                selectedIngredients.add(
                    IngredientSelection(
                        ingredient = ingredient,
                        quantity = ri.quantity,
                        unitId = ri.unit_measurement_id
                    )
                )
            }
        }
    }



    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val categories = listOf("Desayuno", "Almuerzo", "Cena")

    LaunchedEffect(Unit) {
        ingredientViewModel.fetchIngredients()
    }

    val nowIsoString = remember {
        DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFE0B2),
                            Color(0xFFFFF3E0),
                            Color(0xFFFFFBF5)
                        )
                    )
                )
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp) // Adjusted padding for consistency
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (recipeState == null) "Crear Receta" else "Editar Receta",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp), // Similar rounded corners to login fields
                        clip = true
                    )
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Consistent spacing
            ) {
                Button(
                    onClick = {
                        navController.navigate("create_ingredient")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Fixed height for buttons
                ) {
                    Text("Agregar Ingrediente", color = Color.White)
                }

                OutlinedTextField(
                    value = recipeName.value,
                    onValueChange = { recipeName.value = it },
                    label = { Text("Nombre de la receta") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions.value,
                    onValueChange = { instructions.value = it },
                    label = { Text("Instrucciones") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Fixed height for instructions
                    maxLines = 5
                )

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
                        shape = RoundedCornerShape(12.dp),
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

                OutlinedTextField(
                    value = prepTimeText.value,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            prepTimeText.value = newValue
                        }
                    },
                    label = { Text("Tiempo de preparación (minutos)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Fixed height for buttons
                ) {
                    Text("Seleccionar imagen", color = Color.White)
                }

                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Fixed size for image preview
                            .align(Alignment.CenterHorizontally)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                    )
                } else if (existingImageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(existingImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Fixed size for image preview
                            .align(Alignment.CenterHorizontally)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    "Ingredientes de la receta",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

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
                        shape = RoundedCornerShape(12.dp),
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

                selectedIngredients.forEachIndexed { index, selection ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            selection.ingredient.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1E1E1E)
                        )

                        var quantityText by remember { mutableStateOf(selection.quantity.toString()) }

                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { newText ->
                                quantityText = newText
                                val parsed = newText.toDoubleOrNull()
                                if (parsed != null) {
                                    selectedIngredients[index] = selection.copy(quantity = parsed)
                                }
                            },
                            label = { Text("Cantidad") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )



                        var unitDropdownExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = unitDropdownExpanded,
                            onExpandedChange = { unitDropdownExpanded = !unitDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = unitMeasurements.firstOrNull { it.id == selection.unitId }?.name ?: "",
                                onValueChange = {},
                                label = { Text("Unidad de medida") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = unitDropdownExpanded,
                                onDismissRequest = { unitDropdownExpanded = false }
                            ) {
                                unitMeasurements.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit.name) },
                                        onClick = {
                                            selectedIngredients[index] = selection.copy(unitId = unit.id)
                                            unitDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
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

                        val file = imageUri?.let { uri ->
                            getFileFromUri(context, uri)
                        }

                        val ingredientsList = selectedIngredients.map { sel ->
                            CreateRecipeIngredientDto(
                                id = 0,
                                recipe_id = recipeState?.id ?: 0,
                                ingredient_id = sel.ingredient.id,
                                quantity = sel.quantity, // Ya es Double ✅
                                unit_measurement_id = sel.unitId,
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
                            created_at = nowIso,
                            updated_at = nowIso,
                            Recipe_IngredientsJson = ingredientsJsonString,
                            user_id = userId
                        )

                        val currentRecipe = recipeState

                        if (currentRecipe == null) {
                            Log.i("RecipeForm", "Creando receta: $request")
                            recipeViewModel.createRecipe(request, file)
                        } else {
                            Log.i("RecipeForm", "Editando receta ID ${currentRecipe.id}: $request")
                            recipeViewModel.updateRecipe(currentRecipe.id, request, file)
                        }

                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp) // Fixed height for buttons
                ) {
                    Text(if (recipeState == null) "Crear Receta" else "Guardar Cambios", color = Color.White)
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
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


