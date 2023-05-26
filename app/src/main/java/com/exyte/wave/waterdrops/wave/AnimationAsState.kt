package com.exyte.wave.waterdrops.wave

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import kotlin.random.Random

@Composable
fun createAnimationsAsState(
    pointsQuantity: Int,
): MutableList<State<Float>> {
    val animations = remember {
        mutableListOf<State<Float>>()
    }
    val random = remember { Random(System.currentTimeMillis()) }
    val infiniteAnimation = rememberInfiniteTransition()

    if (animations.size == 0) {
        repeat(pointsQuantity / 2) {
            val durationMillis = random.nextInt(2000, 6000)
            animations += infiniteAnimation.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis),
                    repeatMode = RepeatMode.Reverse,
                )
            )
        }
    } else {
        repeat(pointsQuantity / 2) {
            val durationMillis = random.nextInt(2000, 6000)
            animations[it] = infiniteAnimation.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis),
                    repeatMode = RepeatMode.Reverse,
                )
            )
        }
    }
    return animations
}