// src/main/java/com/example/medireminderapp/DashboardScreen.kt

package com.example.medireminderapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlinx.coroutines.launch

data class Medicine(
    val id: String = "",
    val name: String = "",
    val dose: String = "",
    val stockQuantity: String = "",
    val imageUrl: String? = null,
    val creationDate: Date? = null,
    val expiryDate: Long? = null,
    val userId: String? = null,
    val times: List<String> = listOf()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val medicines = remember { mutableStateListOf<Medicine>() }
    val coroutineScope = rememberCoroutineScope()
    val greenColor = Color(0xFF4CAF50)

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("medicines")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    medicines.clear()
                    for (doc in snapshot.documents) {
                        val medicine = doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                        if (medicine != null) {
                            medicines.add(medicine)
                        }
                    }
                }
            }
    }

    val deleteMedicine = { medicineId: String ->
        firestore.collection("medicines").document(medicineId).delete()
    }
    val onEatMedicine = { medicineId: String ->
        // Logic for "eating" the medicine
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.ic_pill_icon), contentDescription = "Dashboard", modifier = Modifier.size(24.dp), tint = greenColor)
                    }
                    FloatingActionButton(
                        onClick = { navController.navigate("choose_method_screen") },
                        containerColor = greenColor,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Medicine", tint = Color.White)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.ic_person_icon), contentDescription = "Profile", modifier = Modifier.size(24.dp), tint = Color.Gray)
                    }
                }
            }
        },
        containerColor = greenColor
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "หน้าหลัก", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    if (medicines.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "ยังไม่มีข้อมูลยา โปรดเพิ่มยาใหม่")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = medicines, key = { it.id }) { medicine ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        when (dismissValue) {
                                            DismissValue.DismissedToEnd -> {
                                                coroutineScope.launch {
                                                    onEatMedicine(medicine.id)
                                                }
                                                true
                                            }
                                            DismissValue.DismissedToStart -> {
                                                coroutineScope.launch {
                                                    deleteMedicine(medicine.id)
                                                }
                                                true
                                            }
                                            else -> false
                                        }
                                    }
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val color = when (dismissState.targetValue) {
                                            DismissValue.DismissedToEnd -> Color.Green.copy(alpha = 0.5f)
                                            DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.5f)
                                            else -> Color.Transparent
                                        }
                                        val icon = when (dismissState.targetValue) {
                                            DismissValue.DismissedToEnd -> Icons.Default.Done
                                            DismissValue.DismissedToStart -> Icons.Default.Delete
                                            else -> null
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(color).padding(horizontal = 16.dp),
                                            contentAlignment = when (dismissState.targetValue) {
                                                DismissValue.DismissedToEnd -> Alignment.CenterStart
                                                DismissValue.DismissedToStart -> Alignment.CenterEnd
                                                else -> Alignment.Center
                                            }
                                        ) {
                                            if (icon != null) {
                                                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MedicineCard(
                                        medicine = medicine,
                                        onDelete = { deleteMedicine(medicine.id) },
                                        onEdit = {
                                            navController.navigate("edit_medicine_screen/${medicine.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomEnd).padding(innerPadding).padding(end = 16.dp, bottom = 16.dp)
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar", tint = greenColor)
            }
        }
        )
    }

    @Composable
    fun MedicineCard(medicine: Medicine, onDelete: () -> Unit, onEdit: () -> Unit) {
        Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (medicine.imageUrl != null && medicine.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = medicine.imageUrl),
                        contentDescription = "Medicine image",
                        modifier = Modifier.size(60.dp).padding(end = 16.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pill_icon),
                        contentDescription = "Medicine image placeholder",
                        modifier = Modifier.size(60.dp).padding(end = 16.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ชื่อยา: ${medicine.name}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                    Text(text = "ปริมาณ: ${medicine.dose}")
                    Text(text = "จำนวนคงเหลือ: ${medicine.stockQuantity ?: "ไม่ระบุ"}")
                    Text(text = "เวลา: ${medicine.times.joinToString(", ")}")
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
        }
    }
}