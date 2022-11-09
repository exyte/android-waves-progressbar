package com.exyte.wave.waterdrops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberDropWaterDuration(
    elementSize: IntSize,
    containerSize: IntSize,
    duration: Long,
): Int {
    return remember(elementSize, containerSize) {
        if ((elementSize.height == 0) || containerSize.height == 0) {
            0
        } else {
            (((duration * elementSize.height * 0.66) / (containerSize.height))).toInt()
        }
    }
}