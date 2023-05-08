package com.exyte.wave.waterdrops

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize
import com.exyte.wave.PointF
import com.exyte.wave.copy
import com.exyte.wave.lerpF
import com.exyte.wave.parabolaInterpolation
import com.exyte.wave.toBoolean
import com.exyte.wave.waterdrops.plottedpoints.createInitialMultipliersAsState
import com.exyte.wave.waterdrops.plottedpoints.createParabolaAsState
import com.exyte.wave.waterdrops.wave.WaveParams


@Composable
fun createPathsAsState(
    levelState: State<LevelState>,
    containerSize: IntSize,
    waterLevelProvider: () -> Float,
    dropWaterDuration: Int,
    animations: MutableList<State<Float>>,
    waveParams: WaveParams,
    elementParams: ElementParams,
): Paths {

    val parabola = createParabolaAsState(
        position = elementParams.position,
        elementSize = elementParams.size,
        waterLevel = waterLevelProvider(),
        buffer = waveParams.bufferY,
        dropWaterDuration = dropWaterDuration,
        levelState = levelState.value
    )

    val plottedPoints = createPlottedPointsAsState(
        waterLevel = waterLevelProvider(),
        containerSize = containerSize,
        levelState = levelState,
        position = elementParams.position,
        buffer = waveParams.bufferY,
        elementSize = elementParams.size,
        parabola = parabola,
        pointsQuantity = waveParams.pointsQuantity
    )

    val initialMultipliers =
        createInitialMultipliersAsState(pointsQuantity = waveParams.pointsQuantity)
    val waveMultiplier = animateFloatAsState(
        targetValue = if (levelState.value == LevelState.WaveIsComing) {
            1f
        } else {
            0f
        },
        animationSpec = keyframes {
            durationMillis = dropWaterDuration
            (0.7f).at((0.2f * dropWaterDuration).toInt())
            (0.8f).at((0.4f * dropWaterDuration).toInt())
        },
    )

    val paths by remember {
        mutableStateOf(Paths())
    }

    createPaths(
        animations,
        initialMultipliers,
        waveParams.maxWaveHeight,
        levelState,
        waveParams.bufferX,
        parabolaInterpolation(waveMultiplier.value),
        containerSize,
        plottedPoints,
        paths,
        elementParams
    )
    return paths
}

fun createPaths(
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    levelState: State<LevelState>,
    bufferX: Float,
    waveMultiplier: Float = 1f,
    containerSize: IntSize,
    points: MutableList<PointF>,
    paths: Paths,
    elementParams: ElementParams,
): Paths {

    for (i in 0..1) {
        var wavePoints = points.copy()
        val divider = i % 2
        wavePoints = addWaves(
            points = wavePoints,
            animations = animations,
            initialMultipliers = initialMultipliers,
            maxHeight = maxHeight,
            pointsInversion = divider.toBoolean(),
            levelState = levelState,
            position = elementParams.position,
            elementSize = elementParams.size,
            waveMultiplier = if (divider == 0) waveMultiplier / 2 else waveMultiplier,
            bufferX = bufferX,
        )
        paths.pathList[i].reset()
        paths.pathList[i] = createPath(containerSize, wavePoints, paths.pathList[i])
    }
    return paths
}

fun createPath(
    containerSize: IntSize,
    wavePoints: MutableList<PointF>,
    path: Path
): Path {
    path.moveTo(0f, containerSize.height.toFloat())
    wavePoints.forEach {
        path.lineTo(it.x, it.y)
    }
    path.lineTo(containerSize.width.toFloat(), containerSize.height.toFloat())
    return path
}

fun addWaves(
    points: MutableList<PointF>,
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    pointsInversion: Boolean,
    levelState: State<LevelState>,
    position: Offset,
    elementSize: IntSize,
    bufferX: Float,
    waveMultiplier: Float,
): MutableList<PointF> {
    val elementRangeX =
        (position.x - bufferX)..(position.x + elementSize.width + bufferX)
    points.forEachIndexed { index, pointF ->
        val newIndex = if (pointsInversion) {
            index % animations.size
        } else {
            (animations.size - index % animations.size) - 1
        }
        val initialMultipliersNewIndex = if (pointsInversion) {
            index
        } else {
            initialMultipliers.size - index - 1
        }
        var waveHeight = calculateWaveHeight(
            animations[newIndex].value,
            initialMultipliers[initialMultipliersNewIndex],
            maxHeight
        )

        if (levelState.value is LevelState.WaveIsComing) {
            if (pointF.x in elementRangeX) {
                waveHeight *= waveMultiplier
            }
        }

        pointF.y = pointF.y - waveHeight
    }
    return points
}

private fun calculateWaveHeight(
    currentSize: Float,
    initialMultipliers: Float,
    maxHeight: Float
): Float {
    var waveHeightPercent = initialMultipliers + currentSize
    if (waveHeightPercent > 1.0f) {
        val diff = waveHeightPercent - 1.0f
        waveHeightPercent = 1.0f - diff
    }

    return lerpF(maxHeight, 0f, waveHeightPercent)
}