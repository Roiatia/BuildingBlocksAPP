package com.buildingblocks.app.feature.setdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.ui.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailsScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    vm: SetDetailsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showStatusSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (state is SetDetailsUiState.Success) {
                        val set = (state as SetDetailsUiState.Success).set
                        IconButton(onClick = { onEdit(set.id.toString()) }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (state is SetDetailsUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = { showStatusSheet = true },
                    icon = { Icon(Icons.Default.SwapHoriz, null) },
                    text = { Text("Change Status") }
                )
            }
        }
    ) { padding ->
        when (val s = state) {
            is SetDetailsUiState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is SetDetailsUiState.NotFound ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Set not found")
                }

            is SetDetailsUiState.Success -> {
                val set = s.set
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (set.imageUrl != null) {
                            SubcomposeAsyncImage(
                                model = set.imageUrl,
                                contentDescription = set.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Inventory2, null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        }
                        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)) {
                            StatusChip(status = set.status)
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(set.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            set.legoSetNumber?.let {
                                Text("Set #$it", style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                set.theme?.let { DetailRow("Theme", it) }
                                set.year?.let { DetailRow("Year", it.toString()) }
                                set.pieceCount?.let { DetailRow("Pieces", "$it pcs") }
                                DetailRow("Priority", set.priority.displayName())
                                set.difficulty?.let { DetailRow("Difficulty", it.displayName()) }
                                set.purchasedAt?.let { DetailRow("Purchased", it.toString()) }
                                DetailRow("Added", set.addedAt.toString().take(10))
                                if (set.status.isBacklog()) {
                                    DetailRow("Waiting", "${set.daysWaiting()} days")
                                }
                                s.storageLocation?.let { DetailRow("Storage", it.name) }
                            }
                        }

                        set.notes?.let { notes ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Notes", style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(4.dp))
                                    Text(notes, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        Spacer(Modifier.height(88.dp))
                    }
                }

                if (showStatusSheet) {
                    ModalBottomSheet(onDismissRequest = { showStatusSheet = false }) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text("Change Status", style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp))
                            SetStatus.entries.forEach { status ->
                                TextButton(
                                    onClick = { vm.updateStatus(status); showStatusSheet = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatusChip(status = status)
                                        if (set.status == status) {
                                            Icon(Icons.Default.Check, null,
                                                tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete set?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; vm.deleteSet(onDeleted) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
