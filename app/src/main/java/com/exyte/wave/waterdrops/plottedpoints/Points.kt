package com.exyte.wave.waterdrops.plottedpoints

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.random.Random

@Composable
fun createInitialMultipliersAsState(pointsQuantity: Int): MutableList<Float> {
    val random = remember { Random(System.currentTimeMillis()) }
    return remember {
        mutableListOf<Float>().apply {
            repeat(pointsQuantity + 4) { this += random.nextFloat() }
        }
    }
}