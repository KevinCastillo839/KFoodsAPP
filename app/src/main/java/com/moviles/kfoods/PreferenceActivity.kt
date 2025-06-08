package com.moviles.kfoods

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.viewmodel.AllergyViewModel
import com.moviles.kfoods.viewmodel.UserAllergyViewModel
import com.moviles.kfoods.viewmodels.PreferenceViewModel

class PreferenceActivity : ComponentActivity() {
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allergyViewModel.getAllergies()
        preferenceViewModel.getAllDietaryRestriction()
        preferenceViewModel.getAllDietaryGoal()

        val userId = intent.getIntExtra("USER_ID", -1)
        setContent {
            KFoodsTheme {
                PreferencesScreen(userId = userId)
            }
        }
    }
}

@Composable
fun PreferencesScreen(
    userId: Int,
    viewModelA: AllergyViewModel = viewModel(),
    viewModelP: PreferenceViewModel = viewModel(),
    viewModelUA: UserAllergyViewModel = viewModel()
) {
    val dietaryRestriction by viewModelP.dietaryRestriction.collectAsState(initial = emptyList())
    val dietaryGoal by viewModelP.dietaryGoal.collectAsState(initial = emptyList())
    val allergies by viewModelA.allergies.collectAsState(initial = emptyList())
    val preferenceId by viewModelP.preferenceId.collectAsState()
    var isSavingPreferences by remember { mutableStateOf(false) }

    var dietGoal by remember { mutableStateOf("") }
    val selectedDietaryRestriction = remember { mutableStateListOf<DietaryRestriction>() }
    val selectedAllergies = remember { mutableStateListOf<Allergy>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModelA.getAllergies()
        viewModelP.getAllDietaryRestriction()
        viewModelP.getAllDietaryGoal()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PreferencesHeader(context)
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
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

                DietaryRestrictionSelector(dietaryRestriction, selectedDietaryRestriction)
                Spacer(modifier = Modifier.height(8.dp))

                DietGoalSelector(dietaryGoal, dietGoal, onDietGoalSelected = { dietGoal = it })
                Spacer(modifier = Modifier.height(8.dp))

                AllergySelector(allergies, selectedAllergies)
                Spacer(modifier = Modifier.height(16.dp))

                AddAllergyButton(userId, context)
                Spacer(modifier = Modifier.height(16.dp))

                SavePreferencesButton(
                    userId = userId,
                    viewModelP = viewModelP,
                    onSaveClicked = { isSavingPreferences = true }
                )

                HandlePreferencesEffects(
                    preferenceId = preferenceId,
                    isSavingPreferences = isSavingPreferences,
                    dietaryGoal = dietaryGoal,
                    dietGoal=dietGoal,
                    selectedDietaryRestriction = selectedDietaryRestriction,
                    selectedAllergies = selectedAllergies,
                    userId = userId,
                    viewModelP = viewModelP,
                    viewModelUA = viewModelUA,
                    context = context
                )
            }
        }
    }
}

@Composable
fun DietaryRestrictionSelector(
    dietaryRestriction: List<DietaryRestriction>,
    selectedDietaryRestriction: SnapshotStateList<DietaryRestriction>
) {
    dietaryRestriction.forEach { restriction ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
        ) {
            val isChecked = selectedDietaryRestriction.contains(restriction)
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isSelected ->
                    if (isSelected) {
                        selectedDietaryRestriction.add(restriction)
                    } else {
                        selectedDietaryRestriction.remove(restriction)
                    }
                }
            )
            Text(text = restriction.name, modifier = Modifier.padding(start = 2.dp))
        }
    }
}

@Composable
fun DietGoalSelector(dietaryGoal: List<DietaryGoal>,dietGoal: String,onDietGoalSelected: (String) -> Unit
) {
    var isDietGoalsExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dietGoal,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Objetivo de dieta") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir menú",
                    modifier = Modifier.clickable { isDietGoalsExpanded = true }
                )
            }
        )
        DropdownMenu(
            expanded = isDietGoalsExpanded,
            onDismissRequest = { isDietGoalsExpanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            dietaryGoal.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(text = goal.goal) },
                    onClick = {
                        onDietGoalSelected(goal.goal)
                        isDietGoalsExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AllergySelector(allergies: List<Allergy>,selectedAllergies: SnapshotStateList<Allergy>) {
    var isAllergiesExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedAllergies.joinToString(", ") { it.name },
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Alergias Seleccionadas") },
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
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
}

@Composable
fun AddAllergyButton(userId: Int, context: Context) {
    Button(
        onClick = {
            val intent = Intent(context, AllergyActivity::class.java)
            intent.putExtra("USER_ID", userId)
            context.startActivity(intent)
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
}

@Composable
fun SavePreferencesButton(userId: Int, viewModelP: PreferenceViewModel, onSaveClicked: () -> Unit) {
    Button(
        onClick = {
            val preference = Preference(
                id = 0,
                user_id = userId,
                created_at = null,
                updated_at = null
            )
            viewModelP.createPreferences(preference)
            onSaveClicked() // Actualizar estado de guardado
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

@Composable
fun HandlePreferencesEffects(
    preferenceId: Int?,
    isSavingPreferences: Boolean,
    dietaryGoal: List<DietaryGoal>,
    dietGoal: String,
    selectedDietaryRestriction: MutableList<DietaryRestriction>,
    selectedAllergies: MutableList<Allergy>,
    userId: Int,
    viewModelP: PreferenceViewModel,
    viewModelUA: UserAllergyViewModel,
    context: Context
) {
    LaunchedEffect(preferenceId, isSavingPreferences) {
        if (isSavingPreferences) {
            if (preferenceId != null) {
                handleDietaryGoal(preferenceId, dietaryGoal, dietGoal, viewModelP, context)
                handleDietaryRestrictions(preferenceId, selectedDietaryRestriction, viewModelP)
                handleUserAllergies(selectedAllergies, userId, viewModelUA)
                navigateToGenerateMenuActivity(context)
            } else {
                Toast.makeText(context, "Error al crear preferencia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Funciones auxiliares
private fun handleDietaryGoal(preferenceId: Int, dietaryGoal: List<DietaryGoal>, dietGoal: String, viewModelP: PreferenceViewModel ,context: Context) {
    val selectedGoal = dietaryGoal.find { it.goal == dietGoal }
    if (selectedGoal == null) {
        Toast.makeText(context, "Seleccione un objetivo de dieta.", Toast.LENGTH_SHORT).show()
        return
    }
    val userDietaryGoal = UserDietaryGoal(
        id = 0,
        user_preference_id = preferenceId,
        goal_id = selectedGoal.id
    )
    Log.d("PreferencesScreen", "Objetivo dietético seleccionado: $userDietaryGoal")
    viewModelP.createDietaryGoals(userDietaryGoal)
}

private fun handleDietaryRestrictions(preferenceId: Int, selectedDietaryRestriction: MutableList<DietaryRestriction>, viewModelP: PreferenceViewModel) {
    val restrictionIds = selectedDietaryRestriction.mapNotNull { it.id }
    val userDietaryRestriction = UserDietaryRestriction(
        id = 0,
        user_preference_id = preferenceId,
        restriction_ids = restrictionIds
    )
    Log.d("PreferencesScreen", "Restricciones dietéticas creadas: $userDietaryRestriction")
    viewModelP.createDietaryRestrictions(userDietaryRestriction)
}

private fun handleUserAllergies(selectedAllergies: MutableList<Allergy>, userId: Int, viewModelUA: UserAllergyViewModel) {
    val allergyIds = selectedAllergies.mapNotNull { it.id }
    val userAllergy = UserAllergy(
        id = 0,
        user_id = userId,
        allergy_ids = allergyIds,
        created_at = null,
        updated_at = null
    )
    Log.d("PreferencesScreen", "Alergias creadas para el usuario: $userAllergy")
    viewModelUA.createUserAllergy(userAllergy)
}

private fun navigateToGenerateMenuActivity(context: Context) {
    val intent = Intent(context, GenerateMenuActivity::class.java)
    context.startActivity(intent)
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
