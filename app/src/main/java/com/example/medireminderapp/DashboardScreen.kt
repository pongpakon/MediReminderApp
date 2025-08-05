package com.example.medireminderapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// Data class for a single medicine item
data class Medicine(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val intakeTime: String = "",
    val isDanger: Boolean = false
)

// DashboardScreen now accepts onAddMedicineClick function for navigation.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onAddMedicineClick: () -> Unit) {
    // State to hold the list of medicines
    var medicines by remember { mutableStateOf(emptyList<Medicine>()) }
    val db = Firebase.firestore

    // Fetch data from Firestore in real-time
    LaunchedEffect(Unit) {
        val medicinesCollection = db.collection("medicines")
        medicinesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("DashboardScreen", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val medicineList = mutableListOf<Medicine>()
                for (doc in snapshot.documents) {
                    val medicine = doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                    if (medicine != null) {
                        medicineList.add(medicine)
                    }
                }
                medicines = medicineList
            } else {
                Log.d("DashboardScreen", "Current data: null")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "หน้าหลัก") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Check if the medicine list is not empty
            if (medicines.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medicines) { medicine ->
                        MedicineCard(medicine = medicine)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "ยังไม่มีข้อมูลยา โปรดเพิ่มยาใหม่")
                }
            }

            // Button to navigate to AddMedicineScreen
            Button(
                onClick = onAddMedicineClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "เพิ่มยาใหม่")
            }
        }
    }
}

@Composable
fun MedicineCard(medicine: Medicine) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "ชื่อยา: ${medicine.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "ปริมาณ: ${medicine.dosage}")
            Text(text = "เวลา: ${medicine.intakeTime}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(onAddMedicineClick = {})
}

