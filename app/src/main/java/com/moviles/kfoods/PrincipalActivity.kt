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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.moviles.kfoods.factory.AuthViewModelFactory
import com.moviles.kfoods.viewmodel.AuthViewModel
import com.moviles.kfoods.viewmodel.MenuViewModel
import com.moviles.kfoods.viewmodel.MenuViewModelFactory
import kotlin.getValue

import com.google.accompanist.pager.*


import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.moviles.kfoods.common.Constants.IMAGES_BASE_URL
import com.moviles.kfoods.models.dto.OverpassElement
import com.moviles.kfoods.viewmodel.SupermarketViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class PrincipalActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application) // use the factory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userId = intent.getIntExtra("id", 1) // Por defecto 1 en lugar de -1
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
    val context = LocalContext.current // Obtén el Context aquí

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem) { index ->
                selectedItem = index
                // Change the screen when an item is selected

                when (index) {
                    0 -> navController.navigate("user/$userId") // Navigate to the User screen
                    1 -> {
                        // Redirigir a RecipeActivity
                        val intent = Intent(context, RecipeActivity::class.java).apply {
                            putExtra("userId", userId)
                        }
                        context.startActivity(intent)
                    }
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

                    // Crear el factory aquí con el userId
                    val factory = MenuViewModelFactory(userId)

                    // Obtener el ViewModel con la factory personalizada
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


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(menuViewModel: MenuViewModel, userId: Int) {
    val menuList by menuViewModel.weeklyMenus.observeAsState(emptyList())
    val isLoading by menuViewModel.isLoading.observeAsState(false)
    val errorMessage by menuViewModel.errorMessage.observeAsState()

    val weeklyPagerState = rememberPagerState()

    // Paleta de colores similar a KFoods
    val primaryColor = Color(0xFFFF5722) // Naranja vibrante
    val secondaryColor = Color(0xFFFFE0B2) // Naranja claro pastel
    val backgroundColor = Color.White // Fondo blanco

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp) // Menor padding superior
    ) {
        // Logo en la esquina superior izquierda, más arriba y más grande
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(60.dp) // Aumentado el tamaño
                .align(Alignment.Start)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))


        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            if (menuList.isEmpty()) {
                Text(
                    "No hay menús disponibles.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    modifier = Modifier.padding(16.dp)
                )
                return@Column
            }

            HorizontalPager(
                count = menuList.size,
                state = weeklyPagerState,
                modifier = Modifier.weight(1f)
            ) { weekPage ->

                val weeklyMenuResponse = menuList[weekPage]
                val dailyMenus = weeklyMenuResponse.weekly_menus
                val dailyPagerState = rememberPagerState()

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White) // Aquí blanco en vez de secondaryColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Menú creado el: ${weeklyMenuResponse.created_at}",
                            style = MaterialTheme.typography.titleMedium.copy(color = primaryColor),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (dailyMenus.isNotEmpty()) {
                            HorizontalPager(
                                count = dailyMenus.size,
                                state = dailyPagerState,
                                modifier = Modifier.weight(1f)
                            ) { dayPage ->
                                val dailyMenu = dailyMenus[dayPage]

                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "Día: ${dailyMenu.day_of_week}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = dailyMenu.menu.name,
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color.DarkGray),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = dailyMenu.menu.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    Text(
                                        text = "Recetas",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryColor
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(dailyMenu.menu.recipes) { recipe ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(20.dp)
                                                        .fillMaxWidth()
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.align(Alignment.CenterStart)
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
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF3E3E3E)
                                                                ),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = "Categoría: ${recipe.category}",
                                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                                            )
                                                        }
                                                    }

                                                    // Recuadro naranja abajo a la derecha con el tiempo de preparación
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.BottomEnd)
                                                            .background(
                                                                color = primaryColor,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = "Prep: ${recipe.preparation_time} min",
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                "No hay menú diario disponible para este menú semanal.",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
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
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionGranted: @Composable () -> Unit) {
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status.isGranted) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        onPermissionGranted()
    } else {
        Text("Permiso de ubicación necesario para mostrar supermercados cercanos")
    }
}




@Composable
fun MapScreen(viewModel: SupermarketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    RequestLocationPermission {
        MapContent(viewModel)
    }
}

@Composable

fun MapContent(viewModel: SupermarketViewModel) {
    val context = LocalContext.current
    val supermarkets by viewModel.supermarkets.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNearbySupermarkets(context)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            supermarkets.isEmpty() -> {
                Text(
                    text = "Cargando supermercados...",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                val currentLocationPair = currentLocation?.let { it.latitude to it.longitude }
                OSMapView(supermarkets, currentLocationPair)

            }
        }
    }
}

@Composable
fun OSMapView(
    supermarkets: List<OverpassElement>,
    currentLocation: Pair<Double, Double>?
) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setBuiltInZoomControls(true)
                setMultiTouchControls(true)

                val mapController = controller
                mapController.setZoom(15.0)

                // Punto para centrar mapa
                val startPoint = when {
                    currentLocation != null -> GeoPoint(currentLocation.first, currentLocation.second)
                    supermarkets.isNotEmpty() -> GeoPoint(supermarkets.first().lat, supermarkets.first().lon)
                    else -> GeoPoint(9.935, -84.091) // fallback
                }
                mapController.setCenter(startPoint)

                // Marcadores supermercados
                supermarkets.forEach { market ->
                    val marker = Marker(this)
                    marker.position = GeoPoint(market.lat, market.lon)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = market.tags?.get("name") ?: "Supermercado"
                    overlays.add(marker)
                }

                // Marcador ubicación actual
                currentLocation?.let { (lat, lon) ->
                    val myMarker = Marker(this)
                    myMarker.position = GeoPoint(lat, lon)
                    myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    myMarker.title = "Mi ubicación"
                    // Puedes cambiar el icono con uno personalizado, por ejemplo:
                    // myMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_my_location)?.toBitmapDescriptor()
                    overlays.add(myMarker)
                }
            }
        }
    )
}

@Composable
fun KFoodsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}
@Composable
fun RemoteImage(imageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,


    )
}

@Composable
fun FallbackImage(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFFF5722).copy(alpha = 0.2f)) // Naranja claro
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Img", color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
    }
}


data class NavigationItem(val label: String, val icon: ImageVector)