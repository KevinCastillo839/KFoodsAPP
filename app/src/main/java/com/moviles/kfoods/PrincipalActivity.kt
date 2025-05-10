package com.moviles.kfoods

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moviles.kfoods.factory.AuthViewModelFactory
import com.moviles.kfoods.viewmodel.AuthViewModel
import kotlin.getValue

class PrincipalActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application) // use the factory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userId = intent.getIntExtra("id", -1) //Receive the userId as a parameter
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
                    1 -> navController.navigate("recipe")  // Navigate to the recipe screen
                    2 -> navController.navigate("home")  // Navigate to the home screen
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
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen() // Pantalla Principal
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


@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pantalla Principal",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun UserScreen(authViewModel: AuthViewModel, userId: Int) {
    val context = LocalContext.current
    LaunchedEffect(userId) {
        authViewModel.getUserById(userId)
    }

    val isLoading by rememberUpdatedState(authViewModel.isLoading.value)
    val userResult by authViewModel.userResult.observeAsState(initial = null)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.image_main),
            contentDescription = "Fondo de pantalla",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 250.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    clip = true
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFE0B2),
                            Color(0xFFFFF3E0),
                            Color(0xFFFFFBF5)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de usuario
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Foto de usuario",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = userResult?.full_name ?: "Nombre de Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )

            // Correo del usuario
            Text(
                text = userResult?.email ?: "Correo no disponible",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones existentes
            Button(
                onClick = { /* TODO: Acción editar preferencias */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Editar Preferencias", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Acción mis recetas */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Mis Recetas", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Acción borrar cuenta */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Borrar Cuenta", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // logout button
            Button(
                onClick = {
                    authViewModel.logout()  // 1.  logout
                    val intent = Intent(context, MainActivity::class.java)  // 2. go to the  MainActivity
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Cerrar Sesión", color = Color.White, fontSize = 16.sp)
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
fun MapScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pantalla de Mapa",
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