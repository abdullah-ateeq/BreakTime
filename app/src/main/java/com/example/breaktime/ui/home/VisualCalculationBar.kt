package com.example.breaktime.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breaktime.domain.BreakTimeFormatter
import com.example.breaktime.model.BreakTimeResult
import com.example.breaktime.ui.theme.BreakBarDark
import com.example.breaktime.ui.theme.BreakBarLight
import com.example.breaktime.ui.theme.NetBarDark
import com.example.breaktime.ui.theme.NetBarLight
import com.example.breaktime.ui.theme.WorkBarDark
import com.example.breaktime.ui.theme.WorkBarLight

@Composable
fun VisualCalculationBar(
    result: BreakTimeResult,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val workColor = if (isDark) WorkBarDark else WorkBarLight
    val breakColor = if (isDark) BreakBarDark else BreakBarLight
    val netColor = if (isDark) NetBarDark else NetBarLight

    val totalWork = result.totalWorkMinutes.coerceAtLeast(1L).toFloat()
    val targetBreakFraction = (result.totalBreakMinutes.toFloat() / totalWork).coerceIn(0f, 1f)
    val targetNetFraction = (result.netMinutes.toFloat() / totalWork).coerceIn(0f, 1f)

    val animatedBreakFraction by animateFloatAsState(
        targetValue = targetBreakFraction,
        animationSpec = if (animationsEnabled) tween(durationMillis = 400) else tween(durationMillis = 0),
        label = "breakFraction"
    )
    val animatedNetFraction by animateFloatAsState(
        targetValue = targetNetFraction,
        animationSpec = if (animationsEnabled) tween(durationMillis = 400) else tween(durationMillis = 0),
        label = "netFraction"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "VISUAL PROPORTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Work Bar (Reference 100%)
            BarRow(
                label = "WORK",
                durationText = BreakTimeFormatter.formatCompactDuration(result.totalWorkMinutes),
                fraction = 1f,
                barColor = workColor
            )

            // Break Bar
            BarRow(
                label = "BREAKS",
                durationText = BreakTimeFormatter.formatCompactDuration(result.totalBreakMinutes),
                fraction = animatedBreakFraction,
                barColor = breakColor
            )

            // Net Bar
            BarRow(
                label = "NET",
                durationText = BreakTimeFormatter.formatCompactDuration(result.netMinutes),
                fraction = animatedNetFraction,
                barColor = netColor
            )
        }
    }
}

@Composable
private fun BarRow(
    label: String,
    durationText: String,
    fraction: Float,
    barColor: androidx.compose.ui.graphics.Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = durationText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceAtLeast(0.01f))
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}
