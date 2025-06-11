package com.moviles.kfoods.ui.theme.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalPagerApi::class)
@Composable

fun HomeScreen(menuViewModel: MenuViewModel, userId: Int) {
    val menuList by menuViewModel.weeklyMenus.observeAsState(emptyList())
    val isLoading by menuViewModel.isLoading.observeAsState(false)
    val errorMessage by menuViewModel.errorMessage.observeAsState()

    val weeklyPagerState = rememberPagerState()

    val primaryColor = Color(0xFFFF5722) // Naranja vibrante
    val secondaryColor = Color(0xFFFFE0B2) // Naranja claro pastel
    val backgroundColor = Color.White // Fondo blanco

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFCCBC), // Naranja pastel suave
                        Color(0xFFFFF3E0)  // Fondo más claro todavía
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
    {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(60.dp)
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

                // Obtener primer y último día para mostrar rango
                val firstDay = dailyMenus.firstOrNull()?.day_of_week ?: ""
                val lastDay = dailyMenus.lastOrNull()?.day_of_week ?: ""

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFE8D8) // Naranja pastel súper suave, más claro que FFCCBC


                    )

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CreatedDateLabel(
                                date = weeklyMenuResponse.created_at,
                                primaryColor = Color(0xFFB71C1C) // rojo oscuro
                            )
                        }

                        // Imagen de fondo detrás de fecha y rango de días
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.image_principal),
                                contentDescription = "Fondo días",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color(0x80000000)) // negro semi-transparente
                            )

                            Text(
                                text = if (firstDay == lastDay) "Día: $firstDay" else "Semana: $firstDay - $lastDay",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                    // No mostramos el día aquí para evitar repetición

                                    Text(
                                        text = dailyMenu.menu.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF3E2723),
                                            fontWeight = FontWeight.Bold,
                                            shadow = Shadow(
                                                color = Color(0x33000000),
                                                offset = Offset(1f, 1f),
                                                blurRadius = 2f
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(bottom = 4.dp)
                                            .background(
                                                color = Color(0xFFFFCCBC),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )

                                    Text(
                                        text = dailyMenu.menu.description.ifEmpty { "Tu menú balanceado para cada día" },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF6D4C41),
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 16.sp
                                        ),
                                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                    )




                                    Text(
                                        text = "Recetas",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryColor
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
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
            .background(Color(0xFFFF8A50).copy(alpha = 0.3f)) // Naranja más claro y visible
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Img", color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatedDateLabel(
    date: String,
    primaryColor: Color
) {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))

    val formattedDate = try {
        val parsedDate = LocalDateTime.parse(date, inputFormatter)
        outputFormatter.format(parsedDate)
    } catch (e: Exception) {
        date // fallback si hay error
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = primaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.calendar_month_24px),
            contentDescription = "Ícono de calendario",
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(primaryColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Menú creado el:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = primaryColor,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

