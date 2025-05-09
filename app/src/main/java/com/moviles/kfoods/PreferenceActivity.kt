package com.moviles.kfoods

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.kfoods.ui.theme.KFoodsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.viewmodel.AllergyViewModel
import com.moviles.kfoods.viewmodels.PreferenceViewModel

class PreferenceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener ID desde el Intent
        val userId = intent.getIntExtra("USER_ID", -1) // Default -1 si no se encuentra
        setContent {
            KFoodsTheme {
                PreferencesScreen(userId = userId)
            }
        }
    }
}

@Composable
fun PreferencesScreen(userId: Int,viewModelA: AllergyViewModel = viewModel(),viewModelP: PreferenceViewModel = viewModel()) {
    val preferences = listOf("Vegetariano", "Sin gluten", "Vegano", "Alta en proteínas", "Baja en calorías")
    val selectedPreferences = remember { mutableStateListOf<String>() }
    val allergies by viewModelA.allergies.collectAsState(initial = emptyList())
    var dietGoal by remember { mutableStateOf("") }
    val dietGoals = listOf("Pérdida de peso", "Mantenimiento", "Ganancia muscular")
    var isDietGoalsExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedAllergy by remember { mutableStateOf<Allergy?>(null) }
    var isAllergiesExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModelA.getAllAllergies()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Parte superior: imagen con logo
        Column(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image_main),
                    contentDescription = "Imagen de fondo",
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa todo el ancho
                        .height(200.dp), // Ajusta la altura para que sea menor
                    contentScale = ContentScale.Crop // Mantiene una escala adecuada
                )
                // Logo centrado encima de la imagen
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo circular",
                    modifier = Modifier
                        .size(80.dp) // Ajusta el tamaño del logo si es necesario
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .clickable {
                            // Acción al tocar el logo
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }
        }
        // Formulario
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 160.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    clip = true
                )
                .background(brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFE0B2),
                            Color(0xFFFFF3E0),
                            Color(0xFFFFFBF5)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        )  {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.weight(1f) // Permite que esta sección ocupe espacio restante
            ) {
                Text(
                    text = "Preferencias",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Marque sus preferencias alimenticias",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Opciones de preferencias
                preferences.forEach { preference ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedPreferences.contains(preference),
                            onCheckedChange = {
                                if (it) selectedPreferences.add(preference)
                                else selectedPreferences.remove(preference)
                            }
                        )
                        Text(text = preference, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de objetivo de dieta
                Text(text = "Seleccione su objetivo de dieta", fontSize = 16.sp, color = Color.Gray)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dietGoal,
                        onValueChange = {},
                        readOnly = true, // Evita la edición directa
                        label = { Text(text = "Objetivo de dieta") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown, // Usa directamente el vector predeterminado
                                contentDescription = "Abrir menú",
                                modifier = Modifier.clickable {
                                    isDietGoalsExpanded = true
                                } // Activa el menú
                            )

                        }
                    )

                    DropdownMenu(
                        expanded = isDietGoalsExpanded,
                        onDismissRequest = {
                            isDietGoalsExpanded = false
                        }, // Cierra el menú si se hace clic fuera
                        modifier = Modifier.fillMaxWidth() // Asegura que el menú ocupe todo el ancho disponible
                    ) {
                        dietGoals.forEach { goal ->
                            DropdownMenuItem(
                                text = { Text(text = goal) },
                                onClick = {
                                    dietGoal = goal // Actualiza el valor seleccionado
                                    isDietGoalsExpanded = false // Cierra el menú
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Selección de alergias
                Text(text = "Agregue sus alergias", fontSize = 16.sp, color = Color.Gray)

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()) {
                        // Selector de Alergias
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedAllergy?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Alergia") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Abrir menú",
                                        modifier = Modifier.clickable { isAllergiesExpanded = true }
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = isAllergiesExpanded,
                                onDismissRequest = { isAllergiesExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                allergies.forEach { allergy ->
                                    DropdownMenuItem(
                                        text = { Text(text = allergy.name) },
                                        onClick = {
                                            selectedAllergy = allergy
                                            isAllergiesExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                }

            }

            // Botón para agregar nueva alergia
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        // Acción al presionar el botón: lógica para agregar nueva alergia
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)), // Color del botón
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier // Espaciado entre el botón y otros elementos
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add, // Ícono predeterminado tipo "+"
                            contentDescription = "Agregar alergia",
                            tint = Color.White, // Color del ícono
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Agregar nueva Alergia", color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botón para enviar
            Button(
                onClick = {
                    val preference = Preference(
                        id = 0, // Si es autogenerado en el servidor, puedes enviar un valor vacío o 0
                        user_id = userId, // Usa el ID del usuario recibido
                        is_vegetarian = selectedPreferences.contains("Vegetariano"),
                        is_gluten_free = selectedPreferences.contains("Sin gluten"),
                        is_vegan = selectedPreferences.contains("Vegano"),
                        dietary_goals = dietGoal,
                        created_at = null, // Deja vacío; usualmente el servidor lo genera
                        updated_at = null
                    )
                    viewModelP.createPreference(preference)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Enviar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

        }

    }
}