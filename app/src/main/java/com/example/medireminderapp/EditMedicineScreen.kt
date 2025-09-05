// src/main/java/com/example/medireminderapp/EditMedicineScreen.kt

package com.example.medireminderapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun EditMedicineScreen(navController: NavController, medicineId: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("หน้าแก้ไขข้อมูลยา (ID: $medicineId)")
    }
}