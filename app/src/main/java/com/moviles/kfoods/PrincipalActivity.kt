package com.moviles.kfoods

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage

import com.moviles.kfoods.factory.AuthViewModelFactory
import com.moviles.kfoods.viewmodel.AuthViewModel
import com.moviles.kfoods.viewmodel.MenuViewModel
import com.moviles.kfoods.viewmodel.MenuViewModelFactory
import kotlin.getValue
import com.moviles.kfoods.ui.theme.home.HomeScreen
import com.moviles.kfoods.ui.theme.map.MapScreen
import com.moviles.kfoods.ui.theme.user.UserScreen



class PrincipalActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application) // use the factory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userId = intent.getIntExtra("id", -1) // Por defecto 1 en lugar de -1
//Receive the userId as a parameter
            KFoodsTheme {
                PrincipalScreen(userId = userId)
                // Pass the userId to PrincipalScreen
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(userId: Int, authViewModel: AuthViewModel = viewModel()) {
    var selectedItem by remember { mutableStateOf(2) } // 0: Profile, 1: Book, 2: Home, 3: Cart, 4: Map

    val navController = rememberNavController() // // Create NavController


    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem) { index ->
                selectedItem = index
                // Change the screen when an item is selected

                when (index) {
                    0 -> navController.navigate("user/$userId") // Navigate to the User screen
                    1 -> navController.navigate("recipe")
                    2 ->navController.navigate("home/$userId")// Navigate to the home screen
                    3 -> navController.navigate("cart")  // Navigate to the cart screen
                    4 -> navController.navigate("map")  // Navigate to the map screen
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
            // use nahost to navigate in the screens
            NavHost(navController = navController, startDestination = "home/$userId") {
                composable(
                    route = "home/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->

                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1


                    val factory = MenuViewModelFactory(userId)

                    val menuViewModel: MenuViewModel = viewModel(factory = factory)

                    HomeScreen(menuViewModel = menuViewModel, userId = userId)
                }



                composable(
                    route = "user/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })  // <-declarate argument type
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: -1

                    UserScreen(authViewModel = authViewModel, userId = userId)
                }

                composable("recipe") {
                    RecipeScreen()
                }
                composable("cart") {
                    CartScreen()
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
        containerColor = Color(0xFFFF5722), // Fondo naranja
        tonalElevation = 8.dp,
        modifier = Modifier.background(
            color = Color(0xFFFF5722), // Fondo naranja
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
                        tint = if (selectedIndex == index) Color.White else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun RecipeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pantalla de Recetas",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}@Composable
fun CartScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pantalla de Carrito de Compras",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun KFoodsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}


data class NavigationItem(val label: String, val icon: ImageVector)