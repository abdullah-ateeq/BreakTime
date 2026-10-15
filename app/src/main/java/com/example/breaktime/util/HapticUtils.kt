package com.example.breaktime.util

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HapticUtils {
    fun performClick(haptic: HapticFeedback?, enabled: Boolean) {
        if (enabled && haptic != null) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun performSuccess(haptic: HapticFeedback?, enabled: Boolean) {
        if (enabled && haptic != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}
