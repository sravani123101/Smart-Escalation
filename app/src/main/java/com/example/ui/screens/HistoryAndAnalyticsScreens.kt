package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import com.example.ui.AppViewModel
import com.example.ui.components.AdherenceDonutChart
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SoftBlueSecondary
import com.example.ui.theme.StatusConfirmed
import com.example.ui.theme.StatusEscalated
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSkipped
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineHistoryScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allReminders by viewModel.allReminders.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, CONFIRMED, PHOTO, SKIPPED, UNCONFIRMED
    var photoToPreview by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf(
        "ALL" to "All Doses",
        "CONFIRMED" to "Confirmed",
        "PHOTO" to "Photo Verified",
        "SKIPPED" to "Skipped",
        "UNCONFIRMED" to "Unconfirmed"
    )

    val filteredList = allReminders.filter { item ->
        val matchesSearch = item.medicineName.contains(searchQuery, ignoreCase = true) ||
                item.scheduledTimeString.contains(searchQuery, ignoreCase = true) ||
                item.scheduledDateString.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "CONFIRMED" -> item.isConfirmed
            "PHOTO" -> item.status == ReminderLogEntity.STATUS_CONFIRMED_WITH_PHOTO
            "SKIPPED" -> item.isSkipped
            "UNCONFIRMED" -> item.isMissedOrUnconfirmed
            else -> true
        }

        matchesSearch && matchesFilter
    }.sortedByDescending { it.scheduledTimeMillis }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportAdherenceReport(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV", tint = NavyPrimary)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by medicine or date...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No records matching current filters", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HistoryItemCard(
                            log = item,
                            onPhotoClick = { photoToPreview = item.photoPath }
                        )
                    }
                }
            }
        }
    }

    if (photoToPreview != null) {
        val path = photoToPreview!!
        AlertDialog(
            onDismissRequest = { photoToPreview = null },
            title = { Text("Verified Packaging Photo") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Verified Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = "Stored locally in private app sandbox.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { photoToPreview = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    log: ReminderLogEntity,
    onPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = log.medicineName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${log.scheduledDateString} at ${log.scheduledTimeString} • ${log.dosage}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = log.status)
            }

            if (!log.photoPath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPhotoClick() },
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = NavyPrimary)
                        Text("View Attached Photo", fontSize = 11.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (!log.skipReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Skip Reason: ${log.skipReason}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (log.isEscalated) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Escalated to: ${log.escalatedToContactName ?: "Emergency Contact"}",
                    fontSize = 11.sp,
                    color = StatusEscalated,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val allReminders by viewModel.allReminders.collectAsState()
    val allMedicines by viewModel.allMedicines.collectAsState()

    var timeframe by remember { mutableStateOf("ALL") } // TODAY, 7DAYS, 30DAYS, ALL

    val filtered = when (timeframe) {
        "TODAY" -> {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            allReminders.filter { it.scheduledDateString == todayStr }
        }
        "7DAYS" -> {
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
            allReminders.filter { it.scheduledTimeMillis >= sevenDaysAgo }
        }
        "30DAYS" -> {
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            allReminders.filter { it.scheduledTimeMillis >= thirtyDaysAgo }
        }
        else -> allReminders
    }

    val total = filtered.size
    val confirmed = filtered.count { it.status == ReminderLogEntity.STATUS_CONFIRMED }
    val confirmedWithPhoto = filtered.count { it.status == ReminderLogEntity.STATUS_CONFIRMED_WITH_PHOTO }
    val totalConfirmed = confirmed + confirmedWithPhoto
    val skipped = filtered.count { it.isSkipped }
    val unconfirmed = filtered.count { it.isMissedOrUnconfirmed }
    val adherencePercent = if (total > 0) (totalConfirmed.toFloat() / total.toFloat()) * 100f else 100f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adherence Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeframe Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("TODAY" to "Today", "7DAYS" to "7 Days", "30DAYS" to "30 Days", "ALL" to "All Time").forEach { (key, label) ->
                        FilterChip(
                            selected = timeframe == key,
                            onClick = { timeframe = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Big Adherence Score Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Overall Adherence Rate", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        AdherenceDonutChart(percentage = adherencePercent, size = 120.dp, strokeWidth = 14.dp)
                        Text(
                            text = "$totalConfirmed of $total scheduled doses confirmed on schedule",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Breakdown Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Standard Confirmed",
                        value = confirmed.toString(),
                        iconColor = StatusConfirmed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Photo Verified",
                        value = confirmedWithPhoto.toString(),
                        iconColor = SoftBlueSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Skipped Doses",
                        value = skipped.toString(),
                        iconColor = StatusSkipped,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Unconfirmed (Escalated)",
                        value = unconfirmed.toString(),
                        iconColor = StatusEscalated,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Medicine-wise adherence breakdown
            item {
                Text("Medicine Adherence Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(allMedicines) { med ->
                val medLogs = filtered.filter { it.medicineId == med.id }
                val medTotal = medLogs.size
                val medConfirmed = medLogs.count { it.isConfirmed }
                val medAdh = if (medTotal > 0) medConfirmed.toFloat() / medTotal.toFloat() else 1f

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(med.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${(medAdh * 100).toInt()}% ($medConfirmed/$medTotal)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { medAdh.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (medAdh >= 0.8f) StatusConfirmed else if (medAdh >= 0.5f) StatusPending else StatusEscalated
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val medicines by viewModel.allMedicines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Stock & Refills", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Inventory decrements only when doses are confirmed. Refill reminders trigger automatically when units fall below the safety threshold.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(medicines, key = { it.id }) { med ->
                val isLow = med.currentStock <= med.lowStockThreshold

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLow) Color(0xFFFFFBEB) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = if (isLow) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Inventory,
                                            contentDescription = null,
                                            tint = if (isLow) Color(0xFFD97706) else NavyPrimary
                                        )
                                    }
                                }

                                Column {
                                    Text(med.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${med.dosage} • ${med.medicineType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            if (isLow) {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Low Stock",
                                        color = StatusEscalated,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current: ${med.currentStock} ${med.unit} (Alert at ${med.lowStockThreshold})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.refillMedicineStock(med.id, 10) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+10", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { viewModel.refillMedicineStock(med.id, 30) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+30 Refill", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
