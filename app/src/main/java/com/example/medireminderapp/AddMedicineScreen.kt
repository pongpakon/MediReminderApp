package com.example.medireminderapp

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onBack: () -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var intakeTime by remember { mutableStateOf("") }
    var isDanger by remember { mutableStateOf(false) }
    var warningText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // สถานะสำหรับรูปภาพ
    val context = LocalContext.current
    val db = Firebase.firestore
    val greenColor = Color(0xFF4CAF50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพิ่มยาใหม่", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = greenColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = greenColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // ส่วนสำหรับรูปภาพ
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pill_icon),
                                contentDescription = "Placeholder",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                    Button(onClick = {
                        // TODO: Implement image picker logic
                        Toast.makeText(context, "ฟังก์ชันเพิ่มรูปภาพ", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("เพิ่มรูปภาพ")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input fields
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        label = { Text("ชื่อยา") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("ปริมาณยา (Dosage)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = intakeTime,
                        onValueChange = { intakeTime = it },
                        label = { Text("เวลา (เช่น 18:00)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkbox สำหรับยาอันตราย
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDanger,
                            onCheckedChange = { isDanger = it }
                        )
                        Text(
                            text = "ยาอันตราย (มีไฮไลต์สีแดง)",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // กล่องข้อความคำเตือนจะแสดงเมื่อ isDanger เป็นจริง
                    if (isDanger) {
                        OutlinedTextField(
                            value = warningText,
                            onValueChange = { warningText = it },
                            label = { Text("คำเตือน") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val medicine = Medicine(
                                name = medicineName,
                                dosage = dosage,
                                intakeTime = intakeTime,
                                isDanger = isDanger,
                                warning = warningText,
                                imageUri = imageUri.toString()
                            )
                            db.collection("medicines")
                                .add(medicine)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        "AddMedicineScreen",
                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                    )
                                    Toast.makeText(context, "บันทึกยาเรียบร้อย", Toast.LENGTH_SHORT)
                                        .show()
                                    onBack()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("AddMedicineScreen", "Error adding document", e)
                                    Toast.makeText(
                                        context,
                                        "บันทึกข้อมูลไม่สำเร็จ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "บันทึกยา")
                    }
                }
            }
        }
    }
}
