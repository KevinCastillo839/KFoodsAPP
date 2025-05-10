package com.moviles.kfoods

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.viewmodel.AllergyViewModel
import com.moviles.kfoods.viewmodel.UserAllergyViewModel
import com.moviles.kfoods.viewmodels.PreferenceViewModel

class PreferenceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("USER_ID", -1)
        setContent {
            KFoodsTheme {
                PreferencesScreen(userId = userId)
            }
        }
    }
}

@Composable
fun PreferencesScreen(userId: Int,viewModelA: AllergyViewModel = viewModel(),viewModelP: PreferenceViewModel = viewModel(),viewModelUA: UserAllergyViewModel = viewModel()) {
    val preferences = listOf("Vegetariano", "Sin gluten", "Vegano", "Alta en proteínas", "Baja en calorías")
    val selectedPreferences = remember { mutableStateListOf<String>() }
    val allergies by viewModelA.allergies.collectAsState(initial = emptyList())
    var dietGoal by remember { mutableStateOf("") }
    val dietGoals = listOf("Pérdida de peso", "Mantenimiento", "Ganancia muscular")
    var isDietGoalsExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isAllergiesExpanded by remember { mutableStateOf(false) }
    val selectedAllergies = remember { mutableStateListOf<Allergy>() }

    LaunchedEffect(Unit) {
        viewModelA.getAllergies()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Top: image with logo
        PreferencesHeader(context)
        // Form
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
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
        )  {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()), // Scroll modifier
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.height(4.dp))

                // Preferences Options
                preferences.forEach { preference ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                    ) {
                        Checkbox(
                            checked = selectedPreferences.contains(preference),
                            onCheckedChange = {
                                if (it) selectedPreferences.add(preference)
                                else selectedPreferences.remove(preference)
                            }
                        )
                        Text(text = preference, modifier = Modifier.padding(start = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Diet Goal Selector
                Text(text = "Seleccione su objetivo de dieta", fontSize = 16.sp, color = Color.Gray)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dietGoal,
                        onValueChange = {},
                        readOnly = true, // Avoid direct editing
                        label = { Text(text = "Objetivo de dieta") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Abrir menú",
                                modifier = Modifier.clickable {
                                    isDietGoalsExpanded = true
                                } // Activate the menu
                            )

                        }
                    )

                    DropdownMenu(
                        expanded = isDietGoalsExpanded,
                        onDismissRequest = {
                            isDietGoalsExpanded = false
                        }, // Closes the menu if clicked outside
                        modifier = Modifier.fillMaxWidth() // Ensures the menu fills the entire available width
                    ) {
                        dietGoals.forEach { goal ->
                            DropdownMenuItem(
                                text = { Text(text = goal) },
                                onClick = {
                                    dietGoal = goal // Update the selected value
                                    isDietGoalsExpanded = false // Close the menu
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Allergy selection
                Text(text = "Agregue sus alergias", fontSize = 16.sp, color = Color.Gray)

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedAllergies.joinToString(", ") { it.name },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Alergias Seleccionadas") },
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight(),
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
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedAllergies.contains(allergy),
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = allergy.name)
                                    }
                                },
                                onClick = {
                                    if (selectedAllergies.contains(allergy)) {
                                        selectedAllergies.remove(allergy)
                                    } else {
                                        selectedAllergies.add(allergy)
                                    }
                                }
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                // Button to add new allergy
                Button(
                    onClick = {
                        // Acción al presionar el botón: lógica para agregar nueva alergia
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar alergia",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Agregar nueva Alergia", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Send button
                Button(
                    onClick = {
                        val preference = Preference(
                            id = 0,
                            user_id = userId, // Use the received user ID
                            is_vegetarian = selectedPreferences.contains("Vegetariano"),
                            is_gluten_free = selectedPreferences.contains("Sin gluten"),
                            is_vegan = selectedPreferences.contains("Vegano"),
                            dietary_goals = dietGoal,
                            created_at = null,
                            updated_at = null
                        )
                        viewModelP.createPreference(preference)
                        val allergyIds = selectedAllergies.mapNotNull { it.id }

                        val userAllergy = UserAllergy(
                            id = 0,
                            user_id = userId, // Use the received user ID
                            allergy_ids = allergyIds,
                            created_at = null,
                            updated_at = null
                        )
                        viewModelUA.createUserAllergy(userAllergy)

                        // Redirect to another Activity after successful registration
                        val intent = Intent(context, GenerateMenuActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Enviar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

}

@Composable
fun PreferencesHeader(context: Context) {

    Box(modifier = Modifier.height(120.dp)) {
        Image(
            painter = painterResource(id = R.drawable.image_main),
            contentDescription = "Imagen de fondo",
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo circular",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .clickable { context.startActivity(Intent(context, MainActivity::class.java)) }
        )
    }
}
