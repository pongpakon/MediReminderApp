package com.example.medireminderapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(onBack: () -> Unit) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var intakeTime by remember { mutableStateOf("") }
    var isDangerMedicine by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = Firebase.firestore

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "เพิ่มยาใหม่") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ย้อนกลับ")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("ชื่อยา") },
                modifier = Modifier.fillMaxWidth()
                // ไม่ต้องตั้งค่า KeyboardOptions เพื่อให้รองรับทุกภาษา
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("ปริมาณยา (Dosage)") },
                modifier = Modifier.fillMaxWidth(),
                // กำหนด KeyboardOptions สำหรับตัวเลขและจุดทศนิยม
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = intakeTime,
                onValueChange = { intakeTime = it },
                label = { Text("เวลารับประทาน (เช่น 18:00 น.)") },
                modifier = Modifier.fillMaxWidth(),
                // กำหนด KeyboardOptions สำหรับตัวเลขและจุด
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDangerMedicine,
                    onCheckedChange = { isDangerMedicine = it }
                )
                Text(
                    text = "ยาอันตราย (มีการไฮไลต์สีแดง)",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (medicineName.isNotBlank() && dosage.isNotBlank() && intakeTime.isNotBlank()) {
                        coroutineScope.launch {
                            val medicine = hashMapOf(
                                "name" to medicineName,
                                "dosage" to dosage,
                                "intakeTime" to intakeTime,
                                "isDanger" to isDangerMedicine
                            )
                            db.collection("medicines")
                                .add(medicine)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(context, "บันทึกยาสำเร็จ!", Toast.LENGTH_SHORT).show()
                                    onBack() // Navigate back on success
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "เกิดข้อผิดพลาดในการบันทึก: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "โปรดกรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "บันทึกยา")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    // TODO: Open camera for OCR scanning
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "สแกนฉลากยาด้วย OCR")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    AddMedicineScreen(onBack = {})
}
