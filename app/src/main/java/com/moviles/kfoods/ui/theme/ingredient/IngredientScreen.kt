package com.moviles.kfoods.ui.theme.ingredient

import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.moviles.kfoods.viewmodel.IngredientViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateIngredientForm(
    navController: NavController,
    viewModel: IngredientViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFE0B2),
                            Color(0xFFFFF3E0),
                            Color(0xFFFFFBF5)
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Spacer(modifier = Modifier.weight(1f)) // Push back button to the left
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Logo
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

            // Ingredient Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Agregar Ingrediente",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1E1E1E)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.insertIngredient(name, description.ifBlank { null }) { success ->
                                if (success) {
                                    name = ""
                                    description = ""
                                    showSuccess = true
                                    showError = false
                                } else {
                                    showSuccess = false
                                    showError = true
                                }
                            }
                        } else {
                            Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }

                if (showSuccess) {
                    Text("¡Ingrediente agregado exitosamente!", color = Color(0xFF4CAF50))
                } else if (showError) {
                    Text("Error al agregar ingrediente. Por favor, inténtalo de nuevo.", color = Color.Red)
                }
            }
        }
    }
}