package com.moviles.kfoods

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moviles.kfoods.ui.theme.KFoodsTheme
import com.moviles.kfoods.ui.theme.shoppingList.ShoppingListScreen
import com.moviles.kfoods.viewmodel.ShoppingListViewModel

class ShoppingListActivity : ComponentActivity() {
    private val viewModel: ShoppingListViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getIntExtra("USER_ID", -1)

        setContent {
            KFoodsTheme {
                Scaffold { padding ->
                    if (userId != -1) {
                        ShoppingListScreen(
                            navController = androidx.navigation.compose.rememberNavController(),
                            userId = userId,
                            viewModel = viewModel,
                            modifier = Modifier.padding(padding)
                        )
                    } else {
                        Text("Error: ID de usuario no proporcionado")
                    }
                }
            }
        }
    }
}