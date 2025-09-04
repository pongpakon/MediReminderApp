// src/main/java/com/example/medireminderapp/DashboardScreen.kt
package com.example.medireminderapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

data class Medicine(
    val id: String = "",
    val name: String = "",
    val dose: String = "",
    val stock: Int? = null,
    val imageUrl: String? = null,
    val creationDate: Date? = null,
    val expiryDate: Long? = null,
    val userId: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    var medicines by remember { mutableStateOf(emptyList<Medicine>()) }
    val firestore = FirebaseFirestore.getInstance()
    val greenColor = Color(0xFF4CAF50)

    fun deleteMedicine(medicineId: String) {
        firestore.collection("medicines").document(medicineId)
            .delete()
            .addOnSuccessListener { Log.d("DashboardScreen", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("DashboardScreen", "Error deleting document", e) }
    }

    LaunchedEffect(Unit) {
        val medicinesCollection = firestore.collection("medicines")
        medicinesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("DashboardScreen", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val fetchedMedicines = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                }
                medicines = fetchedMedicines
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: Navigate to dashboard */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pill_icon),
                            contentDescription = "Dashboard",
                            modifier = Modifier.size(24.dp),
                            tint = greenColor
                        )
                    }

                    FloatingActionButton(
                        onClick = { navController.navigate("add_medicine_screen") },
                        containerColor = greenColor,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Medicine",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { /* TODO: Navigate to profile */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person_icon),
                            contentDescription = "Profile",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        },
        containerColor = greenColor
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "หน้าหลัก",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    if (medicines.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(medicines) { medicine ->
                                MedicineCard(
                                    medicine = medicine,
                                    onDelete = { deleteMedicine(medicine.id) }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "ยังไม่มีข้อมูลยา โปรดเพิ่มยาใหม่")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            FloatingActionButton(
                onClick = { /* TODO: Implement calendar navigation */ },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = greenColor
                )
            }
        }
    }
}

@Composable
fun MedicineCard(medicine: Medicine, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (medicine.imageUrl != null && medicine.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(medicine.imageUrl),
                    contentDescription = "Medicine image",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Medicine image placeholder",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ชื่อยา: ${medicine.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(text = "ปริมาณ: ${medicine.dose}")
                Text(text = "จำนวนคงเหลือ: ${medicine.stock ?: "ไม่ระบุ"}")
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }
        }
    }
}