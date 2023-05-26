package com.exyte.wave.waterdrops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.exyte.wave.Parabola
import com.exyte.wave.PointF

@Composable
fun createPlottedPointsAsState(
    waterLevel: Float,
    containerSize: IntSize,
    levelState: LevelState,
    position: Offset,
    buffer: Float,
    elementSize: IntSize,
    parabola: Parabola,
    pointsQuantity: Int
): List<PointF> {

    val pointSpacing = remember(containerSize, pointsQuantity) {
        derivedStateOf {
            if (containerSize.width == 0)
                0f
            else
                containerSize.width.toFloat() / pointsQuantity
        }
    }

    val spacing = pointSpacing.value.toInt()
    if (spacing == 0) {
        return mutableListOf()
    }

    val points = remember(spacing, containerSize) {
        derivedStateOf {
            (-spacing..containerSize.width + spacing step spacing).map { x ->
                PointF(x.toFloat(), waterLevel)
            }
        }
    }

    val plottedPoints = remember(waterLevel, levelState) {
        when (levelState) {
            is LevelState.FlowsAround -> {
                val point1 = PointF(
                    position.x,
                    position.y - buffer / 5
                )
                val point2 = point1.copy(x = position.x + elementSize.width)
                val point3 = PointF(
                    position.x + elementSize.width / 2,
                    position.y - buffer
                )
                val p = Parabola(point1, point2, point3)
                points.value.forEach {
                    val pointAtParabola = p.calculate(it.x)
                    if (pointAtParabola > waterLevel) {
                        it.y = waterLevel
                    } else {
                        it.y = pointAtParabola
                    }
                }
            }

            is LevelState.WaveIsComing -> {
                val centerPointValue =
                    parabola.calculate(position.x + elementSize.width / 2)
                points.value.forEach {
                    val pr = parabola.calculate(it.x)
                    if (centerPointValue > waterLevel) {
                        if (pr < waterLevel) {
                            it.y = waterLevel
                        } else {
                            it.y = pr
                        }
                    } else {
                        if (pr > waterLevel) {
                            it.y = waterLevel
                        } else {
                            it.y = pr
                        }
                    }
                }
            }

            LevelState.PlainMoving -> {
                points.value.map {
                    it.y = waterLevel
                }
            }
        }
        points
    }
    return plottedPoints.value
}