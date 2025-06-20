package com.moviles.kfoods

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moviles.kfoods.factory.AuthViewModelFactory
import com.moviles.kfoods.ui.theme.home.HomeScreen
import com.moviles.kfoods.ui.theme.ingredient.CreateIngredientForm
import com.moviles.kfoods.ui.theme.map.MapScreen
import com.moviles.kfoods.ui.theme.recipe.RecipeDetailsScreen
import com.moviles.kfoods.ui.theme.recipe.RecipeForm
import com.moviles.kfoods.ui.theme.recipe.RecipeScreen
import com.moviles.kfoods.ui.theme.shoppingList.ShoppingListScreen
import com.moviles.kfoods.ui.theme.user.UserScreen
import com.moviles.kfoods.viewmodel.AuthViewModel
import com.moviles.kfoods.viewmodel.MenuViewModel
import com.moviles.kfoods.viewmodel.MenuViewModelFactory
import com.moviles.kfoods.viewmodel.RecipeViewModel

class PrincipalActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userId = intent.getIntExtra("id", -1) // Obtener userId del Intent
            KFoodsTheme {
                PrincipalScreen(userId = userId, authViewModel = authViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(userId: Int, authViewModel: AuthViewModel = viewModel()) {
    var selectedItem by remember { mutableStateOf(2) } // 0: Perfil, 1: Recetas, 2: Inicio, 3: Carrito, 4: Mapa

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem) { index ->
                selectedItem = index
                when (index) {
                    0 -> navController.navigate("user/$userId")
                    1 -> navController.navigate("recipe")
                    2 -> navController.navigate("home/$userId")
                    3 -> navController.navigate("shoppinglist/by-user/$userId")// Pasar userId al carrito
                    4 -> navController.navigate("map")
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            NavHost(navController = navController, startDestination = "home/$userId") {
                composable(
                    route = "home/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                    val factory = MenuViewModelFactory(userId)
                    val menuViewModel: MenuViewModel = viewModel(factory = factory)
                    HomeScreen(
                        menuViewModel = menuViewModel,
                        userId = userId,
                        onRecipeClick = { recipeId ->
                            navController.navigate("recipe_details/$recipeId")
                        }
                    )
                }

                composable(
                    route = "user/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                    val recipeViewModel: RecipeViewModel = viewModel()
                    UserScreen(
                        authViewModel = authViewModel,
                        recipeViewModel = recipeViewModel,
                        userId = userId,
                        navController = navController
                    )
                }

                composable("recipe_form") {
                    val recipeViewModel: RecipeViewModel = viewModel()
                    RecipeForm(
                        recipeViewModel = recipeViewModel,
                        navController = navController,
                        userId = userId
                    )
                }

                composable(
                    route = "edit_recipe/{recipeId}",
                    arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull()
                    RecipeForm(
                        userId = userId,
                        navController = navController,
                        recipeId = recipeId
                    )
                }

                composable("create_ingredient") {
                    CreateIngredientForm(navController = navController, viewModel = viewModel())
                }

                composable(
                    route = "user_recipes/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                    RecipeScreen(
                        navController = navController,
                        userId = userId
                    )
                }

                composable("recipe") {
                    val recipeViewModel: RecipeViewModel = viewModel()
                    RecipeScreen(navController = navController, recipeViewModel = recipeViewModel)
                }

                composable(
                    route = "recipe_details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getInt("id") ?: -1
                    val recipeViewModel: RecipeViewModel = viewModel()
                    RecipeDetailsScreen(
                        recipeId = recipeId,
                        recipeViewModel = recipeViewModel,
                        navController = navController
                    )
                }

                composable(
                    route = "shoppinglist/by-user/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                    ShoppingListScreen(
                        navController = navController,
                        userId = userId
                    )
                }

                composable("map") {
                    MapScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFFFF5722),
        tonalElevation = 8.dp,
        modifier = Modifier.background(
            color = Color(0xFFFF5722),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        )
    ) {
        val items = listOf(
            NavigationItem("Perfil", Icons.Filled.Person),
            NavigationItem("Libro", Icons.Filled.Book),
            NavigationItem("Inicio", Icons.Filled.Home),
            NavigationItem("Carrito", Icons.Filled.ShoppingCart),
            NavigationItem("Mapa", Icons.Filled.Map)
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selectedIndex == index) Color.Black else Color.DarkGray
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CartScreen(userId: Int, navController: androidx.navigation.NavController) {
    ShoppingListScreen(
        navController = navController,
        userId = userId
    )
}

@Composable
fun KFoodsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

data class NavigationItem(val label: String, val icon: ImageVector)