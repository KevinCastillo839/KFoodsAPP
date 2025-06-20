package com.moviles.kfoods.ui.theme.shoppingList

import ShoppingListViewModel
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.kfoods.R
import com.moviles.kfoods.models.dto.ShoppingListDto
import com.moviles.kfoods.models.dto.SimpleShoppingListItemDto


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    userId: Int,
    viewModel: ShoppingListViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.getWeeklyShoppingList(userId)
    }

    Box(
        modifier = modifier
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo circular",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                shoppingList == null || shoppingList!!.data.isNullOrEmpty() -> {
                    Text(
                        text = "No hay elementos en la lista de compras",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    ShoppingListContent(shoppingList = shoppingList!!)
                }
            }
        }
    }
}

@Composable
fun ShoppingListContent(shoppingList: ShoppingListDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Lista de Compras Semanal (${shoppingList.totalItems} elementos)",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF1E1E1E)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(shoppingList.data) { item ->
                ShoppingListItemCard(item = item)
            }
        }
    }
}

@Composable
fun ShoppingListItemCard(item: SimpleShoppingListItemDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.ingredient ?: "Ingrediente desconocido",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1E1E1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.totalQuantity ?: ""} ${item.unit ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )

        }
    }
}