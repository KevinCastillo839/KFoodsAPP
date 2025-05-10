package com.moviles.kfoods

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.sp
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.ui.theme.KFoodsTheme
import com.moviles.kfoods.viewmodel.AllergyViewModel
import androidx.compose.ui.text.font.FontWeight


class AllergyActivity : ComponentActivity() {
    private val allergyViewModel: AllergyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allergyViewModel.getAllergies()
        setContent {
            KFoodsTheme {
                AllergyStyledScreen(allergyViewModel)
            }
        }
    }
}

@Composable
fun AllergyStyledScreen(
    viewModel: AllergyViewModel
) {
    val allergies by viewModel.allergies.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Header with image and logo

        Box(modifier = Modifier.height(120.dp)) {
            Image(
                painter = painterResource(id = R.drawable.image_main),
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .clickable { (context as? ComponentActivity)?.finish() }
            )
        }

        // Body with gradient and rounded corners

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Alergias",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Administra tus alergias aquí",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))

            // List of allergies

            allergies.forEach { allergy ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${allergy.name}", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Descripción: ${allergy.description ?: "Sin descripción"}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(onClick = {
                                isEditing = true
                                editingId = allergy.id
                                name = allergy.name
                                description = allergy.description ?: ""
                            }) {
                                Text("Editar")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.deleteAllergy(allergy.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isEditing) "Editar Alergia" else "Agregar Alergia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val allergy = Allergy(
                        id = editingId ?: 0,
                        name = name,
                        description = description
                    )
                    if (isEditing) viewModel.updateAllergy(allergy)
                    else viewModel.addAllergy(allergy)
                    name = ""
                    description = ""
                    isEditing = false
                    editingId = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = if (isEditing) "Actualizar" else "Agregar",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
