package com.exyte.wave.waterdrops.wave

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle


@Stable
data class WaterDropText(
    val modifier: Modifier = Modifier,
    val align: Alignment,
    val textStyle: TextStyle,
    val waveParams: WaveParams
)

@Stable
data class WaveParams(
    val pointsQuantity: Int = 10,
    val maxWaveHeight: Float = 20f,
    val bufferY: Float = 60f,
    val bufferX: Float = 50f,
)