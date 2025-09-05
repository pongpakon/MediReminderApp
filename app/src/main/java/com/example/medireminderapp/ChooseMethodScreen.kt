// src/main/java/com/example/medireminderapp/ChooseMethodScreen.kt

package com.example.medireminderapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChooseMethodScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "เลือกวิธีเพิ่มข้อมูลยา",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate("add_by_photo_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Scan Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("ถ่ายรูปฉลากยา")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("add_medicine_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_manual_input),
                    contentDescription = "Manual Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("เพิ่มข้อมูลเอง")
            }
        }
    }
}