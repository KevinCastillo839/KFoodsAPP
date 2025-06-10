package com.moviles.kfoods.ui.theme.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.moviles.kfoods.R
import com.moviles.kfoods.common.Constants.IMAGES_BASE_URL
import com.moviles.kfoods.viewmodel.MenuViewModel

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
