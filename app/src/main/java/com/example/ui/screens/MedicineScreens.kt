package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineEntity
import com.example.ui.AppViewModel
import com.example.ui.theme.HealthOutline
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SoftBlueSecondary
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicineType by remember { mutableStateOf("Tablet") }
    var frequency by remember { mutableStateOf("Daily") }
    val reminderTimes = remember { mutableStateListOf("08:00") }
    var instructions by remember { mutableStateOf("") }
    var foodRelation by remember { mutableStateOf("AFTER_FOOD") }
    var deadlineMinutes by remember { mutableStateOf(45) }
    var currentStock by remember { mutableStateOf("30") }
    var lowStockThreshold by remember { mutableStateOf("7") }
    var unit by remember { mutableStateOf("tablets") }
    var remindersEnabled by remember { mutableStateOf(true) }

    var newTimeInput by remember { mutableStateOf("") }
    var showAddTimeDialog by remember { mutableStateOf(false) }

    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Injection", "Drops", "Inhaler", "Other")
    val foodOptions = listOf("BEFORE_FOOD" to "Before Food", "AFTER_FOOD" to "After Food", "WITH_FOOD" to "With Food", "NO_RESTRICTION" to "No Restriction")
    val deadlineOptions = listOf(15, 30, 45, 60, 90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Basic Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Medicine Name *") },
                        placeholder = { Text("e.g., Metformin, Lisinopril") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("medicine_name_input")
                    )

                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage *") },
                        placeholder = { Text("e.g., 500 mg, 1 tablet, 10 ml") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("medicine_dosage_input")
                    )

                    Text("Medicine Form", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(medicineTypes) { type ->
                            FilterChip(
                                selected = medicineType == type,
                                onClick = { medicineType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Schedule & Reminder Times
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Schedule & Reminder Times", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Text("Reminder Times (24h format HH:mm)", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        reminderTimes.forEach { timeStr ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text(timeStr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (reminderTimes.size > 1) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove",
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { reminderTimes.remove(timeStr) }
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { showAddTimeDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Time", tint = NavyPrimary)
                        }
                    }

                    Text("Relation to Food", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(foodOptions) { (key, label) ->
                            FilterChip(
                                selected = foodRelation == key,
                                onClick = { foodRelation = key },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftBlueSecondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Special Instructions") },
                        placeholder = { Text("e.g. Take with a glass of water, do not chew") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Escalation & Stock
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Safety Deadline & Stock", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Text("Safety Deadline (Confirmation Window)", style = MaterialTheme.typography.labelMedium)
                    Text("If dose is unconfirmed after this time, trusted contacts are notified.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(deadlineOptions) { mins ->
                            FilterChip(
                                selected = deadlineMinutes == mins,
                                onClick = { deadlineMinutes = mins },
                                label = { Text("$mins mins") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = currentStock,
                            onValueChange = { currentStock = it },
                            label = { Text("Current Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lowStockThreshold,
                            onValueChange = { lowStockThreshold = it },
                            label = { Text("Low Alert At") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Active Reminders", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = { remindersEnabled = it }
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        val timesJson = JSONArray(reminderTimes.toList()).toString()
                        val newMed = MedicineEntity(
                            name = name.trim(),
                            dosage = dosage.trim(),
                            medicineType = medicineType,
                            frequency = frequency,
                            reminderTimesJson = timesJson,
                            instructions = instructions.trim(),
                            foodRelation = foodRelation,
                            deadlineMinutes = deadlineMinutes,
                            remindersEnabled = remindersEnabled,
                            currentStock = currentStock.toIntOrNull() ?: 30,
                            lowStockThreshold = lowStockThreshold.toIntOrNull() ?: 5,
                            unit = unit
                        )
                        viewModel.addMedicine(newMed) {
                            onSaved()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_medicine_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                enabled = name.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Save Medicine Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddTimeDialog) {
        AlertDialog(
            onDismissRequest = { showAddTimeDialog = false },
            title = { Text("Add Reminder Time") },
            text = {
                OutlinedTextField(
                    value = newTimeInput,
                    onValueChange = { newTimeInput = it },
                    label = { Text("Time (e.g. 14:30 or 20:00)") },
                    placeholder = { Text("HH:mm") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTimeInput.isNotBlank()) {
                            reminderTimes.add(newTimeInput.trim())
                            newTimeInput = ""
                            showAddTimeDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTimeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineScreen(
    medicineId: Long,
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val allMedicines by viewModel.allMedicines.collectAsState()
    val med = allMedicines.find { it.id == medicineId }

    if (med == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Medicine not found")
        }
        return
    }

    var name by remember(med) { mutableStateOf(med.name) }
    var dosage by remember(med) { mutableStateOf(med.dosage) }
    var medicineType by remember(med) { mutableStateOf(med.medicineType) }
    var instructions by remember(med) { mutableStateOf(med.instructions) }
    var deadlineMinutes by remember(med) { mutableStateOf(med.deadlineMinutes) }
    var currentStock by remember(med) { mutableStateOf(med.currentStock.toString()) }
    var lowStockThreshold by remember(med) { mutableStateOf(med.lowStockThreshold.toString()) }
    var remindersEnabled by remember(med) { mutableStateOf(med.remindersEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit ${med.name}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = currentStock,
                    onValueChange = { currentStock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lowStockThreshold,
                    onValueChange = { lowStockThreshold = it },
                    label = { Text("Low Alert At") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reminders Active", fontWeight = FontWeight.SemiBold)
                Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateMedicine(
                        med.copy(
                            name = name.trim(),
                            dosage = dosage.trim(),
                            instructions = instructions.trim(),
                            currentStock = currentStock.toIntOrNull() ?: med.currentStock,
                            lowStockThreshold = lowStockThreshold.toIntOrNull() ?: med.lowStockThreshold,
                            remindersEnabled = remindersEnabled
                        )
                    ) {
                        onSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailsScreen(
    medicineId: Long,
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onRefill: (Long) -> Unit
) {
    val allMedicines by viewModel.allMedicines.collectAsState()
    val med = allMedicines.find { it.id == medicineId }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (med == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Medicine not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(med.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(med.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = med.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${med.dosage} • ${med.medicineType}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow("Food Relation", med.foodRelation.replace("_", " "))
                    DetailRow("Safety Window", "${med.deadlineMinutes} minutes to confirm")
                    DetailRow("Instructions", med.instructions.ifBlank { "None provided" })
                    DetailRow("Current Inventory", "${med.currentStock} ${med.unit}")
                    DetailRow("Low Stock Alert", "Triggers when below ${med.lowStockThreshold} ${med.unit}")
                    DetailRow("Status", if (med.remindersEnabled) "Reminders Active ✓" else "Paused")
                }
            }

            Button(
                onClick = { viewModel.refillMedicineStock(med.id, 30) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBlueSecondary)
            ) {
                Text("+ Refill +30 Units", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete ${med.name}?") },
            text = { Text("This will remove this medicine and cancel its scheduled reminders.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedicine(med.id) {
                            showDeleteConfirm = false
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
