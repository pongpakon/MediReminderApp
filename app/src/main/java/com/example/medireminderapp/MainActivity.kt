package com.example.medireminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medireminderapp.ui.theme.MediReminderAppTheme
import androidx.compose.foundation.layout.Box
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediReminderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// ในไฟล์ MainActivity.kt

// ...โค้ดส่วนบน...

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("registration") }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onAddMedicineClick = { navController.navigate("add_medicine") },
                onCalendarClick = { navController.navigate("calendar") } // <-- เพิ่มบรรทัดนี้
            )
        }
        composable("add_medicine") {
            AddMedicineScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("registration") {
            RegistrationScreen(
                onBack = { navController.popBackStack() }
            )
        }
        // เพิ่มเส้นทางสำหรับหน้าปฏิทิน
        composable("calendar") {
            // หน้าจอสำหรับปฏิทิน (ตอนนี้ใช้ Box เปล่าๆ ไปก่อน)
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}