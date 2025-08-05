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

// สร้าง Composable function สำหรับจัดการ Navigation ทั้งหมดในแอป
@Composable
fun AppNavigation() {
    // rememberNavController ใช้เพื่อสร้าง NavController ที่จะควบคุมการเปลี่ยนหน้า
    val navController = rememberNavController()

    // NavHost จะเป็นตัวกำหนดเส้นทาง (Destinations) ของแต่ละหน้า
    NavHost(navController = navController, startDestination = "login") {
        // กำหนดเส้นทางสำหรับหน้า Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // เมื่อล็อกอินสำเร็จ จะเปลี่ยนไปที่หน้า Dashboard
                    navController.navigate("dashboard") {
                        // popUpTo ลบหน้า login ออกจาก stack เพื่อไม่ให้ย้อนกลับมาได้
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        // กำหนดเส้นทางสำหรับหน้า Dashboard
        composable("dashboard") {
            // เรียกใช้ DashboardScreen และส่งฟังก์ชันสำหรับนำทางไปหน้า AddMedicineScreen
            DashboardScreen(
                onAddMedicineClick = { navController.navigate("add_medicine") }
            )
        }
        // กำหนดเส้นทางสำหรับหน้าเพิ่มยา
        composable("add_medicine") {
            AddMedicineScreen(
                // ส่งฟังก์ชัน onBack ไปยัง AddMedicineScreen เพื่อให้สามารถกดปุ่มย้อนกลับได้
                onBack = { navController.popBackStack() }
            )
        }
    }
}
