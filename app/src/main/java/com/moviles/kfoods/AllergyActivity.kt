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
import com.moviles.kfoods.models.Allergy

class AllergyActivity : ComponentActivity() {

    private val allergyViewModel: AllergyViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allergyViewModel.getAllergies()

        setContent {
            KFoodsTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Lista de Alergias") },
                            actions = {
                                // Puedes agregar iconos o acciones aquí si lo deseas
                            }
                        )
                    }
                ) { padding ->
                    AllergyScreen(
                        modifier = Modifier.padding(padding),
                        allergyViewModel = allergyViewModel
                    )
                }
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
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editingAllergyId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = {
                                name = allergy.name
                                description = allergy.description ?: ""
                                isEditing = true
                                editingAllergyId = allergy.id
                            }) {
                                Text("Editar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { allergyViewModel.deleteAllergy(allergy.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isEditing) "Editar Alergia" else "Agregar Alergia",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val allergy = Allergy(
                    id = editingAllergyId ?: 0,
                    name = name,
                    description = description
                )
                if (isEditing) {
                    allergyViewModel.updateAllergy(allergy)
                } else {
                    allergyViewModel.addAllergy(allergy)
                }

                // Limpiar formulario
                name = ""
                description = ""
                isEditing = false
                editingAllergyId = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Actualizar Alergia" else "Agregar Alergia")
        }
    }
}


