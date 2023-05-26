package com.exyte.wave.animating

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember

enum class WaterLevelState {
    StartReady,
    Animating,
}

@Composable
fun waveProgressAsState(
    timerState: WaterLevelState,
    timerDurationInMillis: Long
): State<Float> {
    val animatable = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(timerState) {
        when (timerState) {
            WaterLevelState.StartReady -> {
                animatable.animateTo(
                    targetValue = 0.0f,
                    animationSpec = spring(stiffness = 100f)
                )
            }

            WaterLevelState.Animating -> {
                animatable.animateTo(
                    targetValue = 0.7f,
                    animationSpec = tween(
                        durationMillis = timerDurationInMillis.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    return produceState(initialValue = animatable.value, key1 = animatable.value) {
        this.value = animatable.value
    }
}