package com.exyte.wave

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import com.exyte.wave.waterdrops.ElementParams
import kotlin.math.pow

fun isAboveElement(waterLevel: Int, bufferY: Float, position: Offset) =
    waterLevel < position.y - bufferY

fun atElementLevel(
    waterLevel: Int,
    buffer: Float,
    elementParams: ElementParams,
) =
    (waterLevel >= (elementParams.position.y - buffer)) && (waterLevel < (elementParams.position.y + elementParams.size.height * 0.33))

fun isWaterFalls(
    waterLevel: Int,
    elementParams: ElementParams,
) =
    waterLevel >= (elementParams.position.y + elementParams.size.height * 0.33) && waterLevel <= (elementParams.position.y + elementParams.size.height)

@Stable
data class PointF(
    var x: Float,
    var y: Float
)

fun MutableList<PointF>.copy(): MutableList<PointF> = map {
    it.copy()
}.toMutableList()

@Stable
class Parabola(
    point1: PointF,
    point2: PointF,
    point3: PointF
) {
    private val a: Float
    private val b: Float
    private val c: Float

    init {
        val denom = (point1.x - point2.x) * (point1.x - point3.x) * (point2.x - point3.x)
        a =
            (point3.x * (point2.y - point1.y) + point2.x * (point1.y - point3.y) + point1.x * (point3.y - point2.y)) / denom
        b =
            (point3.x.pow(2) * (point1.y - point2.y) + point2.x.pow(2) * (point3.y - point1.y) + point1.x.pow(
                2
            ) * (point2.y - point3.y)) / denom
        c =
            (point2.x * point3.x * (point2.x - point3.x) * point1.y + point3.x * point1.x * (point3.x - point1.x) * point2.y + point1.x * point2.x * (point1.x - point2.x) * point3.y) / denom
    }

    fun calculate(x: Float): Float {
        return a * x.pow(2) + (b * x) + c
    }

}

fun lerpF(start: Float, stop: Float, fraction: Float): Float =
    (1 - fraction) * start + fraction * stop

fun parabolaInterpolation(fraction: Float): Float {
    return ((-40) * (fraction - 0.5).pow(2) + 11).toFloat()
}

fun Int.toBoolean(): Boolean = this != 0

@Stable
fun TextUnit.toPx(density: Density): Float = with(density) { this@toPx.roundToPx().toFloat() }