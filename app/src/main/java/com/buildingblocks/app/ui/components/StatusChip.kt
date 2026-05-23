package com.buildingblocks.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.ui.theme.*

@Composable
fun StatusChip(status: SetStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        SetStatus.SEALED -> StatusSealed
        SetStatus.UNBUILT -> StatusUnbuilt
        SetStatus.IN_PROGRESS -> StatusInProgress
        SetStatus.BUILT -> StatusBuilt
        SetStatus.DISASSEMBLED -> StatusDisassembled
        SetStatus.MISSING_PARTS -> StatusMissingParts
        SetStatus.WAITING_FOR_DISPLAY_SPACE -> StatusWaitingDisplay
        SetStatus.SOLD -> StatusSold
    }
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.displayName(),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
