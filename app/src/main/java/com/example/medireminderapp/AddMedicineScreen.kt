package com.example.medireminderapp

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(navController: NavController) {
    val context = LocalContext.current
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var medicineName by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพิ่มยาใหม่") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = if (selectedImageUri != null) {
                    rememberAsyncImagePainter(model = selectedImageUri)
                } else {
                    rememberAsyncImagePainter(model = R.drawable.ic_launcher_background)
                },
                contentDescription = "Medicine Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("ชื่อยา") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stockQuantity,
                onValueChange = { stockQuantity = it },
                label = { Text("จำนวนยาในคลัง") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("ขนาดยา") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "วันหมดอายุ: ${selectedDate?.let { dateFormat.format(Date(it)) } ?: "ยังไม่ระบุ"}",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                }
            }

            if (showDatePicker) {
                MyDatePicker(
                    onDateSelected = { date ->
                        selectedDate = date
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val medicineData = hashMapOf(
                        "name" to medicineName,
                        "stock" to stockQuantity.toIntOrNull(),
                        "dose" to dose,
                        "expiryDate" to selectedDate,
                        "imageUrl" to selectedImageUri,
                        "userId" to auth.currentUser?.uid,
                        "creationDate" to Date()
                    )

                    firestore.collection("medicines")
                        .add(medicineData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "เพิ่มยาสำเร็จ", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "เพิ่มยาไม่สำเร็จ: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("เพิ่มยา")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    openDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}