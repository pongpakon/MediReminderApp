// src/main/java/com/example/medireminderapp/AddMedicineScreen.kt

package com.example.medireminderapp

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    navController: NavController,
    initialMedicineName: String = "",
    initialDose: String = ""
) {
    val context = LocalContext.current
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    var medicineName by remember { mutableStateOf(initialMedicineName) }
    var stockQuantity by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf(initialDose) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    val selectedTimes = remember { mutableStateListOf<String>() }

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = if (selectedImageUri != null) {
                            rememberAsyncImagePainter(model = selectedImageUri)
                        } else {
                            painterResource(id = R.drawable.ic_pill_icon)
                        },
                        contentDescription = "Medicine Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable { launcher.launch("image/*") }
                            .padding(16.dp)
                    )
                    Text("เพิ่มรูปภาพ", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "กำหนดเวลาทานยา", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Time"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("เพิ่มเวลา")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedTimes.forEach { time ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(time, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            IconButton(onClick = { selectedTimes.remove(time) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Time",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDate?.let { dateFormat.format(Date(it)) } ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("วันหมดอายุ") },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showDatePicker) {
                MyDatePicker(
                    onDateSelected = { dateInMillis ->
                        selectedDate = dateInMillis
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showTimePicker) {
                MyTimePicker(
                    onTimeSelected = { time ->
                        selectedTimes.add(time)
                        showTimePicker = false
                    },
                    onDismiss = { showTimePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (medicineName.isEmpty()) {
                        Toast.makeText(context, "กรุณากรอกชื่อยา", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedTimes.isEmpty()) {
                        Toast.makeText(context, "กรุณากำหนดเวลาทานยาอย่างน้อยหนึ่งเวลา", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userId = auth.currentUser?.uid ?: return@Button
                    if (selectedImageUri != null) {
                        val imageRef = storage.reference.child("medicine_images/${UUID.randomUUID()}.jpg")
                        imageRef.putFile(selectedImageUri!!)
                            .addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val medicineData = hashMapOf(
                                        "name" to medicineName,
                                        "stockQuantity" to stockQuantity,
                                        "dose" to dose,
                                        "expiryDate" to selectedDate,
                                        "times" to selectedTimes,
                                        "imageUrl" to uri.toString(),
                                        "userId" to userId,
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
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "อัปโหลดรูปภาพไม่สำเร็จ: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        val medicineData = hashMapOf(
                            "name" to medicineName,
                            "stockQuantity" to stockQuantity,
                            "dose" to dose,
                            "expiryDate" to selectedDate,
                            "times" to selectedTimes,
                            "userId" to userId,
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
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("บันทึก", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}