package com.moviles.kfoods.ui.theme.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.moviles.kfoods.viewmodel.SupermarketViewModel


@Composable
fun GoogleMapScreen(viewModel: SupermarketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    RequestLocationPermission {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.loadNearbySupermarkets(context)
        }

        MapWithGoogle(viewModel)
    }
}



@Composable
fun MapWithGoogle(viewModel: SupermarketViewModel) {
    val context = LocalContext.current
    val supermarkets by viewModel.supermarkets.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val selectedMarket = remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadNearbySupermarkets(context)
    }

    val location = currentLocation

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f) // Ocupa la mitad superior de la pantalla
                .fillMaxSize()
        ) {
            when {
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                supermarkets.isEmpty() -> {
                    Text(
                        text = "Cargando supermercados...",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val defaultLatLng = location?.let {
                        LatLng(it.latitude, it.longitude)
                    } ?: LatLng(supermarkets[0].lat, supermarkets[0].lon)

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition(
                            selectedMarket.value ?: defaultLatLng,
                            15f,
                            0f,
                            0f
                        )
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        supermarkets.forEach { market ->
                            val latLng = LatLng(market.lat, market.lon)
                            Marker(
                                state = MarkerState(position = latLng),
                                title = market.tags?.get("name") ?: "Supermercado",
                                onClick = {
                                    selectedMarket.value = latLng
                                    false
                                }
                            )
                        }

                        location?.let {
                            Marker(
                                state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                                title = "Mi ubicación"
                            )
                        }

                        if (location != null && selectedMarket.value != null) {
                            Polyline(
                                points = listOf(
                                    LatLng(location.latitude, location.longitude),
                                    selectedMarket.value!!
                                ),
                                color = Color.Blue,
                                width = 8f
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .height(150.dp) // tamaño reducido
                .background(Color(0xFFFFCC80)) // naranja más oscuro y suave
                .padding(vertical = 8.dp)
        ) {
            items(supermarkets) { market ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            // Al hacer click, actualizar selectedMarket para centrar mapa
                            selectedMarket.value = LatLng(market.lat, market.lon)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Icono supermercado",
                        tint = Color(0xFF1E1E1E),
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = market.tags?.get("name") ?: "Supermercado",
                        modifier = Modifier.padding(start = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF1E1E1E)
                    )
                }
            }
        }

    }
}
