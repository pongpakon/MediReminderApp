package com.example.medireminderapp

import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// Data class for a single medicine item
data class Medicine(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val intakeTime: String = "",
    val isDanger: Boolean = false,
    val isTaken: Boolean = false,
    val warning: String = "", // <-- เพิ่ม field สำหรับข้อความเตือน
    val imageUri: String = "" // <-- เพิ่ม field สำหรับ URI รูปภาพ
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddMedicineClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    var medicines by remember { mutableStateOf(emptyList<Medicine>()) }
    val db = Firebase.firestore
    val greenColor = Color(0xFF4CAF50)

    // ฟังก์ชันสำหรับลบรายการยา
    fun deleteMedicine(medicineId: String) {
        db.collection("medicines").document(medicineId)
            .delete()
            .addOnSuccessListener { Log.d("DashboardScreen", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("DashboardScreen", "Error deleting document", e) }
    }

    // ฟังก์ชันสำหรับอัปเดตสถานะการทานยาใน Firestore
    fun updateMedicineStatus(medicineId: String, newStatus: Boolean) {
        db.collection("medicines").document(medicineId)
            .update("isTaken", newStatus)
            .addOnSuccessListener { Log.d("DashboardScreen", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("DashboardScreen", "Error updating document", e) }
    }

    LaunchedEffect(Unit) {
        val medicinesCollection = db.collection("medicines")
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
                        onClick = onAddMedicineClick,
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
                                // ส่งข้อมูลที่จำเป็นทั้งหมดไปให้ MedicineCard
                                MedicineCard(
                                    medicine = medicine,
                                    onToggle = { newStatus -> updateMedicineStatus(medicine.id, newStatus) },
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
                onClick = onCalendarClick,
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
fun MedicineCard(medicine: Medicine, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) { // แก้ไขให้รับทั้ง onDelete และ onToggle
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ชื่อยา: ${medicine.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (medicine.isDanger) Color.Red else Color.Black
                )
                Text(text = "ปริมาณ: ${medicine.dosage}")
                Text(text = "เวลา: ${medicine.intakeTime}")
                Text(
                    text = if (medicine.isTaken) "ทานแล้ว" else "ยังไม่ได้ทานยา",
                    color = if (medicine.isTaken) Color.Gray else Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // เพิ่ม Row เพื่อจัดเรียงปุ่มสลับและปุ่มลบ
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = medicine.isTaken,
                    onCheckedChange = {
                        // เพิ่ม Log เพื่อช่วยในการตรวจสอบว่าปุ่มถูกกดหรือไม่
                        Log.d("MedicineCard", "Toggled for medicine ${medicine.id}, new status: $it")
                        onToggle(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Red,
                    )
                )
                // เพิ่มปุ่มลบกลับมา
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
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(onAddMedicineClick = {}, onCalendarClick = {})
}