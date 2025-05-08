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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.kfoods.ui.theme.KFoodsTheme
import com.moviles.kfoods.viewmodel.AllergyViewModel
import androidx.compose.material3.TopAppBar

class AllergyActivity : ComponentActivity() {

    private val allergyViewModel: AllergyViewModel by viewModels()
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allergyViewModel.fetchAllergies() // Llama a la API al cargar la pantalla

        setContent {
            KFoodsTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lista de Alergias") }
                        )
                    },
                    content = { padding ->
                        AllergyScreen(
                            modifier = Modifier.padding(padding),
                            allergyViewModel = allergyViewModel
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AllergyScreen(
    modifier: Modifier = Modifier,
    allergyViewModel: AllergyViewModel
) {
    val allergies by allergyViewModel.allergies.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(allergies) { allergy ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Nombre: ${allergy.name}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Descripción: ${allergy.description ?: "Sin descripción"}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
