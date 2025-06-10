package com.moviles.kfoods.ui.theme.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.moviles.kfoods.models.dto.OverpassElement
import com.moviles.kfoods.viewmodel.SupermarketViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.collections.forEach


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