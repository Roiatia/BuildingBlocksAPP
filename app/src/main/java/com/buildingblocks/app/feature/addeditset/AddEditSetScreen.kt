package com.buildingblocks.app.feature.addeditset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buildingblocks.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSetScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onLimitReached: () -> Unit = {},
    vm: AddEditSetViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsStateWithLifecycle()
    val locations by vm.storageLocations.collectAsStateWithLifecycle()

    LaunchedEffect(form.savedSuccessfully) {
        if (form.savedSuccessfully) onSaved()
    }

    LaunchedEffect(form.limitReached) {
        if (form.limitReached) onLimitReached()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vm.isEditMode) "Edit Set" else "Add Set") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = vm::save, enabled = !form.isSaving) {
                        if (form.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        else Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Required fields
            SectionLabel("Required")

            OutlinedTextField(
                value = form.name,
                onValueChange = vm::onName,
                label = { Text("Set Name *") },
                isError = form.nameError != null,
                supportingText = form.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            StatusDropdown(selected = form.status, onSelect = vm::onStatus)

            // Optional fields
            SectionLabel("Optional")

            OutlinedTextField(
                value = form.legoSetNumber,
                onValueChange = vm::onSetNumber,
                label = { Text("Set Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = form.theme,
                onValueChange = vm::onTheme,
                label = { Text("Theme") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = form.year,
                    onValueChange = vm::onYear,
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = form.pieceCount,
                    onValueChange = vm::onPieceCount,
                    label = { Text("Piece Count") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = form.imageUrl,
                onValueChange = vm::onImageUrl,
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            PriorityDropdown(selected = form.priority, onSelect = vm::onPriority)

            DifficultyDropdown(selected = form.difficulty, onSelect = vm::onDifficulty)

            if (locations.isNotEmpty()) {
                StorageDropdown(
                    locations = locations,
                    selected = form.storageLocationId,
                    onSelect = vm::onStorageLocation
                )
            }

            OutlinedTextField(
                value = form.notes,
                onValueChange = vm::onNotes,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(selected: SetStatus, onSelect: (SetStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.displayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Status *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SetStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.displayName()) },
                    onClick = { onSelect(status); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityDropdown(selected: Priority, onSelect: (Priority) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.displayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Priority") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Priority.entries.forEach { p ->
                DropdownMenuItem(text = { Text(p.displayName()) }, onClick = { onSelect(p); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultyDropdown(selected: Difficulty?, onSelect: (Difficulty?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.displayName() ?: "Not set",
            onValueChange = {},
            readOnly = true,
            label = { Text("Difficulty") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Not set") }, onClick = { onSelect(null); expanded = false })
            Difficulty.entries.forEach { d ->
                DropdownMenuItem(text = { Text(d.displayName()) }, onClick = { onSelect(d); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StorageDropdown(
    locations: List<com.buildingblocks.app.domain.model.StorageLocation>,
    selected: java.util.UUID?,
    onSelect: (java.util.UUID?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = locations.find { it.id == selected }?.name ?: "None"
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Storage Location") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("None") }, onClick = { onSelect(null); expanded = false })
            locations.forEach { loc ->
                DropdownMenuItem(
                    text = { Text(loc.name) },
                    onClick = { onSelect(loc.id); expanded = false }
                )
            }
        }
    }
}
