// src/main/java/com/example/medireminderapp/MainActivity.kt

package com.example.medireminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medireminderapp.ui.theme.MediReminderAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MediReminderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination = if (auth.currentUser != null) {
                        "dashboard_screen"
                    } else {
                        "login_screen"
                    }
                    AppNavigation(startDestination)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("register_screen") {
            RegisterScreen(navController = navController)
        }
        composable("dashboard_screen") {
            DashboardScreen(navController = navController)
        }
        composable("choose_method_screen") {
            ChooseMethodScreen(navController = navController)
        }
        composable("add_medicine_screen") {
            AddMedicineScreen(navController = navController)
        }
        composable("add_by_photo_screen") {
            AddByPhotoScreen(navController = navController)
        }
        composable("edit_medicine_screen/{medicineId}") { backStackEntry ->
            EditMedicineScreen(
                navController = navController,
                medicineId = backStackEntry.arguments?.getString("medicineId")
            )
        }
    }
}