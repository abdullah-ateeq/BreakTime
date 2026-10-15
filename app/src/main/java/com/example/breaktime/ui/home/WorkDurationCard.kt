package com.example.breaktime.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breaktime.domain.BreakTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkDurationCard(
    hoursInput: String,
    minutesInput: String,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onPresetSelected: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoursLong = hoursInput.toLongOrNull() ?: 0L
    val minutesLong = minutesInput.toLongOrNull() ?: 0L
    val totalWorkMinutes = hoursLong * 60L + minutesLong
    val hasNormalization = minutesLong > 59L

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Work duration, $hoursLong hours $minutesLong minutes."
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORK DURATION",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                if (hasNormalization) {
                    Text(
                        text = "Normalized: ${BreakTimeFormatter.formatDuration(totalWorkMinutes)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = hoursInput,
                    onValueChange = onHoursChange,
                    label = { Text("Hours") },
                    placeholder = { Text("8") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Work hours" }
                )

                OutlinedTextField(
                    value = minutesInput,
                    onValueChange = onMinutesChange,
                    label = { Text("Minutes") },
                    placeholder = { Text("0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Work minutes" }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "QUICK PRESETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val presets = listOf(
                        Triple("4 HOURS", 4L, 0L),
                        Triple("6 HOURS", 6L, 0L),
                        Triple("8 HOURS", 8L, 0L),
                        Triple("8.5 HOURS", 8L, 30L),
                        Triple("10 HOURS", 10L, 0L),
                        Triple("12 HOURS", 12L, 0L)
                    )

                    presets.forEach { (label, h, m) ->
                        val isSelected = (hoursInput == h.toString() && minutesInput == m.toString())
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPresetSelected(h, m) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}
