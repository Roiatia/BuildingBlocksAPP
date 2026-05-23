package com.buildingblocks.app.feature.backlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.domain.model.SortOrder
import com.buildingblocks.app.ui.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogScreen(
    onSetClick: (String) -> Unit,
    vm: BacklogViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    val backlogStatuses = SetStatus.entries.filter { it.isBacklog() }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Build Backlog") },
                    actions = {
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Default.Sort, "Sort")
                            }
                            DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                                listOf(
                                    SortOrder.DATE_ADDED_ASC,
                                    SortOrder.DATE_ADDED_DESC,
                                    SortOrder.PIECE_COUNT_DESC,
                                    SortOrder.PIECE_COUNT_ASC,
                                    SortOrder.PRIORITY_HIGH,
                                    SortOrder.DIFFICULTY_HIGH
                                ).forEach { order ->
                                    DropdownMenuItem(
                                        text = { Text(order.displayName()) },
                                        onClick = { vm.onSortOrder(order); showSortMenu = false }
                                    )
                                }
                            }
                        }
                    }
                )
                // Pick a set for me button
                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("✦ Pick a set for me", color = MaterialTheme.colorScheme.primary)
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.filterStatus == null,
                            onClick = { vm.onFilterStatus(null) },
                            label = { Text("All") }
                        )
                    }
                    items(backlogStatuses) { status ->
                        FilterChip(
                            selected = state.filterStatus == status,
                            onClick = { vm.onFilterStatus(if (state.filterStatus == status) null else status) },
                            label = { Text(status.displayName()) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.sets.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text("Backlog is clear!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sets with Sealed, Unbuilt, In Progress, Missing Parts, or Waiting status appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.sets, key = { it.id.toString() }) { set ->
                    BacklogCard(set = set, onClick = { onSetClick(set.id.toString()) })
                }
            }
        }
    }
}

@Composable
private fun BacklogCard(set: LegoSet, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box {
            // Image
            if (set.imageUrl != null) {
                SubcomposeAsyncImage(
                    model = set.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Inventory2, null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                }
            }

            // Waiting badge (top-right)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Timer, null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(3.dp))
                    Text("${set.daysWaiting()}d",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            }

            // Set number badge (top-left)
            set.legoSetNumber?.let { num ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(num,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        // Info
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(set.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
                StatusChip(status = set.status)
            }
            val meta = listOfNotNull(set.theme, set.pieceCount?.let { "$it pcs" })
            if (meta.isNotEmpty()) {
                Text(meta.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
