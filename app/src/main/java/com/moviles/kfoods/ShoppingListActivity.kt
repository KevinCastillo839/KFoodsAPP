package com.moviles.kfoods

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.kfoods.ui.theme.KFoodsTheme
import com.moviles.kfoods.models.dto.ShoppingListDto
import com.moviles.kfoods.models.dto.SimpleShoppingListItemDto
import com.moviles.kfoods.viewmodel.ShoppingListViewModel

class ShoppingListActivity : ComponentActivity() {

    private val shoppingListViewModel: ShoppingListViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener userId del Intent (ajusta según cómo pases el userId)
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            shoppingListViewModel.getWeeklyShoppingList(userId)
        }

        setContent {
            KFoodsTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lista de Compras Semanal") },
                            actions = {
                                // Puedes agregar acciones aquí si es necesario
                            }
                        )
                    }
                ) { padding ->
                    ShoppingListScreen(
                        modifier = Modifier.padding(padding),
                        shoppingListViewModel = shoppingListViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    shoppingListViewModel: ShoppingListViewModel
) {
    val shoppingList by shoppingListViewModel.shoppingList.collectAsState()
    val isLoading by shoppingListViewModel.isLoading
    val errorMessage by shoppingListViewModel.errorMessage.observeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            shoppingList != null -> {
                ShoppingListContent(shoppingList = shoppingList!!)
            }
            else -> {
                Text(
                    text = "No hay datos disponibles",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ShoppingListContent(shoppingList: ShoppingListDto) {
    if (shoppingList.data.isEmpty()) {
        Text(
            text = "No se encontraron elementos en la lista de compras",
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        Column {
            Text(
                text = "Total de elementos: ${shoppingList.totalItems}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(shoppingList.data) { item ->
                    ShoppingListItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun ShoppingListItemCard(item: SimpleShoppingListItemDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ingrediente: ${item.IngredientName}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cantidad: ${item.TotalQuantity} ${item.Unit}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}