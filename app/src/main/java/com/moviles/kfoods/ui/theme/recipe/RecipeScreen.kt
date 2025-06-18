package com.moviles.kfoods.ui.theme.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.kfoods.R
import com.moviles.kfoods.common.Constants.IMAGES_BASE_URL
import com.moviles.kfoods.models.Recipe
import com.moviles.kfoods.models.RecipeIngredient
import com.moviles.kfoods.ui.theme.home.RemoteImage
import com.moviles.kfoods.viewmodel.RecipeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    navController: NavController? = null,
    recipeViewModel: RecipeViewModel = viewModel(),
    userId: Int? = null
) {
    // State to manage the prescription to be deleted
    val recipeToDelete = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(userId) {
        if (userId == null || userId == -1) {
            recipeViewModel.getRecipes()
        } else {
            recipeViewModel.getRecipesByUser(userId)
        }
    }

    val recipes by recipeViewModel.recipes.collectAsState(emptyList())
    val isLoading by recipeViewModel.isLoading.collectAsState(false)
    val searchQuery = remember { mutableStateOf("") }
    val selectedMealType = remember { mutableStateOf("Todos") }
    val selectedPrepTime = remember { mutableStateOf(0) }
    val menuExpanded = remember { mutableStateOf(false) }
    val selectedIngredient = remember { mutableStateOf("") }

    val filteredRecipes = filterRecipes(
        recipes = recipes,
        searchQuery = searchQuery.value,
        selectedMealType = selectedMealType.value,
        selectedPrepTime = selectedPrepTime.value,
        selectedIngredient = selectedIngredient.value
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo de la app",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recetas",
                            color = Color(0xFFFF5722),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (userId != null && userId != -1) {
                navController?.let {
                    FloatingActionButton(
                        onClick = { it.navigate("recipe_form") },
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Receta")
                    }
                }
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            SearchAndFilterBar(
                searchQuery = searchQuery,
                menuExpanded = menuExpanded,
                selectedPrepTime = selectedPrepTime,
                selectedIngredient = selectedIngredient,
                selectedMealType = selectedMealType
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val deleteRecipe: (Int) -> Unit = { recipeId ->
                    // We save the recipe to delete to show dialog
                    recipeToDelete.value = recipeId
                }

                navController?.let {
                    RecipeList(
                        recipes = filteredRecipes,
                        navController = it,
                        onDeleteClick = deleteRecipe,
                        userId = userId
                    )
                } ?: RecipeList(
                    recipes = filteredRecipes,
                    navController = rememberNavController(),
                    onDeleteClick = deleteRecipe,
                    userId = userId
                )
            }
        }

        // Confirmation dialog to delete recipe
        recipeToDelete.value?.let { idToDelete ->
            AlertDialog(
                onDismissRequest = { recipeToDelete.value = null },
                title = { Text(text = "Confirmar eliminación") },
                text = { Text("¿Estás seguro que quieres eliminar esta receta?") },
                confirmButton = {
                    TextButton(onClick = {
                        recipeViewModel.deleteRecipe(idToDelete)
                        // Reload prescriptions after deleting
                        if (userId == null || userId == -1) {
                            recipeViewModel.getRecipes()
                        } else {
                            recipeViewModel.getRecipesByUser(userId)
                        }
                        recipeToDelete.value = null
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { recipeToDelete.value = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}



// Function to display search bar and filters
@Composable
fun SearchAndFilterBar(searchQuery: MutableState<String>, menuExpanded: MutableState<Boolean>, selectedPrepTime: MutableState<Int>,
                       selectedIngredient: MutableState<String>, selectedMealType: MutableState<String>) {
    val primaryColor = Color(0xFFFF5722)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = {
                    Text(text = "Busca tu comida favorita...", color = Color.Gray)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = primaryColor
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterMenu(
                menuExpanded = menuExpanded,
                selectedPrepTime = selectedPrepTime,
                selectedIngredient = selectedIngredient
            )
        }
        MealTypeFilter(
            selectedMealType = selectedMealType,
            selectedPrepTime = selectedPrepTime,
            selectedIngredient = selectedIngredient
        )
    }
}

// Food type filter
@Composable
fun MealTypeFilter(
    selectedMealType: MutableState<String>,
    selectedPrepTime: MutableState<Int>,
    selectedIngredient: MutableState<String>
) {
    val mealTypes = listOf(
        "Todos" to Icons.Default.AllInclusive,
        "Desayuno" to Icons.Default.BreakfastDining,
        "Almuerzo" to Icons.Default.LunchDining,
        "Cena" to Icons.Default.DinnerDining
    )
    val primaryColor = Color(0xFFFF5722)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mealTypes.forEach { (mealType, icon) ->
            Button(
                onClick = {
                    selectedMealType.value = mealType
                    if (mealType == "Todos") {
                        selectedPrepTime.value = 0
                        selectedIngredient.value = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMealType.value == mealType) primaryColor else Color(0xFFFFCCBC)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = mealType,
                    tint = Color.White
                )
            }
        }
    }
}


// Filter dropdown menu
@Composable
fun FilterMenu(menuExpanded: MutableState<Boolean>, selectedPrepTime: MutableState<Int>, selectedIngredient: MutableState<String>) {
    val primaryColor = Color(0xFFFF5722)

    Box {
        IconButton(onClick = { menuExpanded.value = !menuExpanded.value }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Abrir filtros",
                tint = primaryColor
            )
        }
        DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tiempo de preparación: <= 15 min") },
                onClick = {
                    selectedPrepTime.value = 15
                    menuExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = { Text("Tiempo de preparación: <= 30 min") },
                onClick = {
                    selectedPrepTime.value = 30
                    menuExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = { Text("Tiempo de preparación: <= 60 min") },
                onClick = {
                    selectedPrepTime.value = 60
                    menuExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = {
                    Column {
                        Text("Filtrar por ingrediente")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = selectedIngredient.value,
                            onValueChange = { selectedIngredient.value = it },
                            placeholder = { Text("Escribe un ingrediente") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                        )
                    }
                },
                onClick = { menuExpanded.value = false }
            )
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    navController: NavController,
    onDeleteClick: (Int) -> Unit,
    userId: Int?
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = {
                    navController.navigate("recipe_details/${recipe.id}")
                },
                onEditClick = {
                    navController.navigate("edit_recipe/${recipe.id}")
                },
                onDeleteClick = {
                    onDeleteClick(recipe.id)
                },
                userId = userId
            )

        }
    }
}



// Card to display a recipe
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    userId: Int?
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteImage(
                IMAGES_BASE_URL + recipe.image_url,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Categoría: ${recipe.category}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .background(Color.Red, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Prep: ${recipe.preparation_time} min",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }
            }
            if (userId != null && userId != -1) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF1976D2)
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }

        }
    }
}


// Recipe filter
fun filterRecipes(recipes: List<Recipe>,searchQuery: String,selectedMealType: String,selectedPrepTime: Int,selectedIngredient: String): List<Recipe> {
    return recipes.filter { recipe ->
        val ingredients = recipe.recipe_ingredients?.map { it.ingredient.name } ?: emptyList()
        (selectedMealType == "Todos" || recipe.category == selectedMealType) &&
                (searchQuery.isBlank() || recipe.name.contains(searchQuery, ignoreCase = true)) &&
                (selectedPrepTime == 0 || recipe.preparation_time <= selectedPrepTime) &&
                (selectedIngredient.isBlank() || ingredients.any { it.contains(selectedIngredient, ignoreCase = true) })
    }
}

@Composable
fun AppNavigation(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    NavHost(navController = navController, startDestination = "recipes") {
        composable("recipes") {
            RecipeScreen(navController = navController, recipeViewModel = recipeViewModel)
        }
        composable(
            "recipe_details/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            RecipeDetailsScreen(recipeId,recipeViewModel,navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(recipeId: Int?, recipeViewModel: RecipeViewModel, navController: NavController) {
    if (recipeId == null) {
        Text("Error: No se pudo cargar la receta.")
        return
    }

    val recipe by recipeViewModel.recipeDetail.collectAsState()
    LaunchedEffect(recipeId) {
        recipeViewModel.getRecipesById(recipeId)
    }

    if (recipe == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de la app",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Image and title
            RecipeHeader(recipe!!)

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredients
            SectionHeader(title = "Ingredientes")
            IngredientList(recipe!!.recipe_ingredients ?: emptyList())

            Spacer(modifier = Modifier.height(16.dp))

            // Preparation
            SectionHeader(title = "Preparación")
            PreparationSteps(recipe!!.instructions.split("\n"))
        }
    }
}



@Composable
fun RecipeHeader(recipe: Recipe) {
    Box {
        RemoteImage(
            IMAGES_BASE_URL + recipe.image_url,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Text(
            text = recipe.name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun IngredientList(ingredients: List<RecipeIngredient>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (ingredients.isEmpty()) {
                Text(
                    text = "No hay ingredientes disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else {
                ingredients.forEach { ingredient ->
                    Text(
                        text = "- ${ingredient.quantity} ${ingredient.ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun PreparationSteps(steps: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (steps.isEmpty()) {
                Text(
                    text = "No hay instrucciones disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else {
                steps.forEachIndexed { index, step ->
                    Text(
                        text = "${index + 1}. $step",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}





