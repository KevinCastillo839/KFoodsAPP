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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.UserAllergy
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.models.dto.CreatePreferenceRequestDto
import com.moviles.kfoods.ui.theme.KFoodsTheme
import com.moviles.kfoods.viewmodel.AllergyViewModel
import com.moviles.kfoods.viewmodel.UserAllergyViewModel
import com.moviles.kfoods.viewmodels.PreferenceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PreferenceActivity : ComponentActivity() {
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve userId and flag from the Intent
        val userId = intent.getIntExtra("id", -1)
        val isNewUser = intent.getBooleanExtra("IS_NEW_USER", false)

        allergyViewModel.getAllergies()
        preferenceViewModel.getAllDietaryRestriction()
        preferenceViewModel.getAllDietaryGoal()
        preferenceViewModel.fetchUserPreferences(userId)

        setContent {
            KFoodsTheme {
                PreferencesScreen(userId = userId,isNewUser = isNewUser)
            }
        }
    }
}

@Composable
fun PreferencesScreen(userId: Int, isNewUser: Boolean, viewModelA: AllergyViewModel = viewModel(),
    viewModelP: PreferenceViewModel = viewModel(), viewModelUA: UserAllergyViewModel = viewModel()) {
    val dietaryRestriction by viewModelP.dietaryRestriction.collectAsState(initial = emptyList())
    val userAllergies by viewModelUA.userAllergy.collectAsState(initial = emptyList())
    val dietaryGoal by viewModelP.dietaryGoal.collectAsState(initial = emptyList())
    val allergies by viewModelA.allergies.collectAsState(initial = emptyList())
    val preferenceId by viewModelP.preferenceId.collectAsState()
    val selectedDietaryRestriction = remember { mutableStateListOf<DietaryRestriction>() }
    val selectedAllergies = remember { mutableStateListOf<Allergy>() }
    var isSavingPreferences by remember { mutableStateOf(false) }
    var preferenceIdState by remember { mutableStateOf<Int?>(null) }
    var dietGoal by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showCreatePreferenceDialog by remember { mutableStateOf(false) }
    var hasCreatedPreference by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    if (!isNewUser) {
        Log.d("PreferencesScreen", "Editar preferencias para el usuario: $userId")
        val userPreference by viewModelP.userPreference.collectAsState()
        LaunchedEffect(userPreference,userAllergies) {
            userPreference?.let {
                preferenceIdState = it.preferenceId
                selectedDietaryRestriction.clear()
                selectedDietaryRestriction.addAll(
                    it.restrictions?.mapNotNull { restrictionName ->
                        dietaryRestriction.find { restriction -> restriction.name == restrictionName.name }
                    } ?: emptyList()
                )
                dietGoal = dietaryGoal.find { it.id == userPreference!!.goal?.id }?.goal ?: ""
            }
            selectedAllergies.clear()
            userAllergies.forEach { userAllergy ->
                userAllergy.allergy?.let { allergy ->
                    // Find the corresponding object in the allergy list
                    allergies.find { it.id == allergy.id }?.let { matchedAllergy ->
                        selectedAllergies.add(matchedAllergy)
                    }
                }
            }
        }
    }
    // Mostrar cuadro de diálogo al iniciar la pantalla
    LaunchedEffect(preferenceIdState, isNewUser) {
        if (!isNewUser && preferenceIdState == 0 && !hasCreatedPreference) {
            showCreatePreferenceDialog = true
        }
    }
    LaunchedEffect(Unit) {
        viewModelA.getAllergies()
        viewModelP.getAllDietaryRestriction()
        viewModelP.getAllDietaryGoal()
        viewModelP.fetchUserPreferences(userId)
        viewModelUA.fetchUserAllergy(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PreferencesHeader(context)
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .shadow(elevation = 8.dp,
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
        ) {
            Column(modifier = Modifier
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
                Text(text = "Marque sus preferencias alimenticias",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                DietaryRestrictionSelector(dietaryRestriction, selectedDietaryRestriction)
                Spacer(modifier = Modifier.height(8.dp))

                DietGoalSelector(dietaryGoal, dietGoal, onDietGoalSelected = { dietGoal = it })
                Spacer(modifier = Modifier.height(8.dp))

                AllergySelector(allergies, selectedAllergies,viewModelA)
                Spacer(modifier = Modifier.height(16.dp))

                AddAllergyButton(userId, context)
                Spacer(modifier = Modifier.height(16.dp))

                SavePreferencesButton(
                    userId = userId,
                    isNewUser=isNewUser,
                    viewModelP = viewModelP,
                    dietGoal=dietGoal,
                    dietaryGoal=dietaryGoal,
                    onSaveClicked = { isSavingPreferences = true },
                    context=context
                )

                if (isNewUser || preferenceIdState != null) {
                    HandlePreferencesEffects(
                        preferenceIdState = preferenceIdState ?: 0, // Use a safe default value if null
                        isNewUser = isNewUser,
                        preferenceId = preferenceId,
                        isSavingPreferences = isSavingPreferences,
                        dietaryGoal = dietaryGoal,
                        dietGoal = dietGoal,
                        selectedDietaryRestriction = selectedDietaryRestriction,
                        selectedAllergies = selectedAllergies,
                        userId = userId,
                        viewModelP = viewModelP,
                        viewModelUA = viewModelUA,
                        context = context
                    )
                } else {
                    Log.e("PreferencesScreen", "Error: preferenceIdState es nulo y no es un nuevo usuario.")
                }
            }
            // Dialog that appears if the user failed to create the preference in the registry
            if (showCreatePreferenceDialog) {
                AlertDialog(
                    onDismissRequest = { if (!isProcessing) showCreatePreferenceDialog = false },
                    title = { Text("Crear Preferencia") },
                    text = { Text("No tiene preferencias creadas. ¿Desea crear una nueva preferencia?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                isProcessing = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val preference = CreatePreferenceRequestDto(user_id = userId)
                                        viewModelP.createPreferences(preference)
                                        hasCreatedPreference = true // Check that a preference has already been created

                                        // Keep the dialogue open for 5 seconds
                                        delay(5000)
                                        viewModelP.fetchUserPreferences(userId)
                                        viewModelUA.fetchUserAllergy(userId)
                                        Toast.makeText(context, "Preferencia creada exitosamente", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error al crear preferencias: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        // Close the dialog after 5 seconds or if an error occurs
                                        showCreatePreferenceDialog = false
                                        isProcessing = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            enabled = !isProcessing // Disable button during processing
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                if (!isProcessing) {
                                    showCreatePreferenceDialog = false
                                    val intent = Intent(context, PrincipalActivity::class.java).apply {
                                        putExtra("id", userId) // Pass userId of existing user
                                        putExtra("IS_NEW_USER", false) // Indicate that you are not a new user
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            enabled = !isProcessing // Disable button during processing
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DietaryRestrictionSelector(dietaryRestriction: List<DietaryRestriction>,
    selectedDietaryRestriction: SnapshotStateList<DietaryRestriction>) {
    dietaryRestriction.forEach { restriction ->
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
        ) {
            val isChecked = selectedDietaryRestriction.contains(restriction)
            Checkbox(checked = isChecked,
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
fun DietGoalSelector(dietaryGoal: List<DietaryGoal>,dietGoal: String,onDietGoalSelected: (String) -> Unit) {
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
fun AllergySelector(allergies: List<Allergy>,selectedAllergies: SnapshotStateList<Allergy>,viewModelA: AllergyViewModel) {
    var isAllergiesExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(isAllergiesExpanded) {
        if (isAllergiesExpanded) {
            viewModelA.getAllergies()
        }
    }

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
        DropdownMenu(expanded = isAllergiesExpanded,
            onDismissRequest = { isAllergiesExpanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {allergies.forEach { allergy ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = selectedAllergies.any { it.id == allergy.id },
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    if (selectedAllergies.none { it.id == allergy.id }) {
                                        selectedAllergies.add(allergy)
                                    }
                                } else {
                                    selectedAllergies.removeAll { it.id == allergy.id }
                                }
                            }
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
fun SavePreferencesButton(userId: Int,isNewUser: Boolean,viewModelP: PreferenceViewModel,
    dietGoal: String,
    dietaryGoal: List<DietaryGoal>,
    onSaveClicked: () -> Unit,
    context: Context
) {
    val isDietGoalValid = dietaryGoal.any { it.goal == dietGoal }

    Button(
        onClick = {
            if (!isDietGoalValid) {
                Toast.makeText(context, "Seleccione un objetivo de dieta válido.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (isNewUser) {
                val preference = CreatePreferenceRequestDto(user_id = userId)
                viewModelP.createPreferences(preference)
                Log.d("PreferencesScreen", "Crear nuevas preferencias para el usuario: $userId")
            }
            onSaveClicked()
        },
        colors = ButtonDefaults.buttonColors(containerColor = if (isDietGoalValid) Color(0xFFFF5722) else Color.Gray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = isDietGoalValid // Disable the button if no valid target is selected
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
fun HandlePreferencesEffects(preferenceIdState: Int, isNewUser: Boolean, preferenceId: Int?,
    isSavingPreferences: Boolean, dietaryGoal: List<DietaryGoal>, dietGoal: String, selectedDietaryRestriction: MutableList<DietaryRestriction>,
    selectedAllergies: MutableList<Allergy>, userId: Int, viewModelP: PreferenceViewModel,
    viewModelUA: UserAllergyViewModel, context: Context) {
    LaunchedEffect(preferenceId, isSavingPreferences) {
        if (isSavingPreferences) {
            var attempts = 0
            // If you are not a new user, use the preferenceIdState directly.
            val preferenceIdFinal = if (!isNewUser) preferenceIdState else preferenceId

            while (preferenceIdFinal == null && attempts < 100) {
                delay(50) // Wait for preferenceId to have a value
                attempts++
            }

            if (preferenceIdFinal == null) {
                Log.e("PreferencesScreen", "Error: No se pudo crear preferenceId después de 100 intentos")
                Toast.makeText(context, "Error al crear o editar las preferencias", Toast.LENGTH_SHORT).show()
                return@LaunchedEffect
            }
            Log.d("PreferencesScreen", "Id Creado o Existente: $preferenceIdFinal")
            Toast.makeText(context, "Preferencias procesadas con éxito", Toast.LENGTH_SHORT).show()

            if (isNewUser) {
                createDietaryGoal(preferenceIdFinal, dietGoal, dietaryGoal, viewModelP, context)
                createDietaryRestrictions(preferenceIdFinal, selectedDietaryRestriction, viewModelP)
                createUserAllergies(userId, selectedAllergies, viewModelUA)
            } else {
                updateDietaryGoal(preferenceIdFinal, dietGoal, dietaryGoal, viewModelP, context)
                updateDietaryRestrictions(preferenceIdFinal, selectedDietaryRestriction, viewModelP)
                updateUserAllergies(userId, selectedAllergies, viewModelUA)
            }
            navigateToGenerateMenuActivity(userId,isNewUser, context)
        }
    }
}

private fun navigateToGenerateMenuActivity( userId: Int,isNewUser: Boolean,context: Context) {
    if (isNewUser) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    } else {
        val intent = Intent(context, PrincipalActivity::class.java).apply {
            putExtra("id", userId) // Pass userId of existing user
            putExtra("IS_NEW_USER", false) // Indicate that you are not a new user
        }
        context.startActivity(intent)
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

private fun createDietaryGoal( preferenceId: Int, dietGoal: String, dietaryGoal: List<DietaryGoal>,
                               viewModelP: PreferenceViewModel, context: Context) {
    val selectedGoal = dietaryGoal.find { it.goal == dietGoal }
    if (selectedGoal == null) {
        Toast.makeText(context, "Seleccione un objetivo de dieta válido.", Toast.LENGTH_SHORT).show()
        return
    }
    val userDietaryGoal = UserDietaryGoal(
        id = 0,
        user_preference_id = preferenceId,
        goal_id = selectedGoal.id
    )
    viewModelP.createDietaryGoals(userDietaryGoal)
}
private fun createDietaryRestrictions(preferenceId: Int, selectedDietaryRestriction: List<DietaryRestriction>,
    viewModelP: PreferenceViewModel) {
    val restrictionIds = selectedDietaryRestriction.mapNotNull { it.id }
    val userDietaryRestriction = UserDietaryRestriction(
        id = 0,
        user_preference_id = preferenceId,
        restriction_ids = restrictionIds
    )
    viewModelP.createDietaryRestrictions(userDietaryRestriction)
}
private fun createUserAllergies(userId: Int, selectedAllergies: List<Allergy>, viewModelUA: UserAllergyViewModel) {
    val allergyIds = selectedAllergies.mapNotNull { it.id }
    val userAllergy = UserAllergy(
        id = 0,
        user_id = userId,
        allergy_ids = allergyIds,
        created_at = null,
        updated_at = null
    )
    viewModelUA.createUserAllergy(userAllergy)
}
private fun updateDietaryGoal( preferenceId: Int, dietGoal: String, dietaryGoal: List<DietaryGoal>,
    viewModelP: PreferenceViewModel,  context: Context) {
    val selectedGoal = dietaryGoal.find { it.goal == dietGoal }
    if (selectedGoal == null) {
        Toast.makeText(context, "Seleccione un objetivo de dieta válido.", Toast.LENGTH_SHORT).show()
        return
    }
    val userDietaryGoal = UserDietaryGoal(
        id = 0,
        user_preference_id = preferenceId,
        goal_id = selectedGoal.id
    )
    viewModelP.updateDietaryGoal(userDietaryGoal)
}
private fun updateDietaryRestrictions(preferenceId: Int, selectedDietaryRestriction: List<DietaryRestriction>,
    viewModelP: PreferenceViewModel) {
    val restrictionIds = selectedDietaryRestriction.mapNotNull { it.id }
    val userDietaryRestriction = UserDietaryRestriction(
        id = 0,
        user_preference_id = preferenceId,
        restriction_ids = restrictionIds
    )
    viewModelP.updateDietaryRestriction(userDietaryRestriction)
}
private fun updateUserAllergies(userId: Int, selectedAllergies: List<Allergy>, viewModelUA: UserAllergyViewModel) {
    val allergyIds = selectedAllergies.mapNotNull { it.id }
    val userAllergy = UserAllergy(
        id = 0,
        user_id = userId,
        allergy_ids = allergyIds,
        created_at = null,
        updated_at = null
    )
    viewModelUA.updateUserAllergy(userId, userAllergy)
}
