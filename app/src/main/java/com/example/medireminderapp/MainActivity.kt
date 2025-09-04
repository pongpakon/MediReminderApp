package com.example.medireminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medireminderapp.ui.theme.MediReminderAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        setContent {
            MediReminderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(auth)
                }
            }
        }
    }
}

@Composable
fun MyApp(auth: FirebaseAuth) {
    val navController = rememberNavController()
    val startDestination = if (auth.currentUser != null) "dashboard_screen" else "login_screen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login_screen") {
            LoginScreen(
                navController = navController,
                auth = auth
            )
        }
        composable("registration_screen") {
            RegistrationScreen(navController = navController)
        }
        composable("add_medicine_screen") {
            AddMedicineScreen(navController = navController)
        }
        composable("dashboard_screen") {
            DashboardScreen(navController = navController)
        }
    }
}