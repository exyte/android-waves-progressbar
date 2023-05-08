package com.exyte.wave.waterdrops.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.exyte.wave.ui.theme.Blue
import com.exyte.wave.ui.theme.Water
import com.exyte.wave.waterdrops.Paths

fun DrawScope.drawWaves(
    paths: Paths,
) {
    drawIntoCanvas {
        it.drawPath(paths.pathList[1], paint.apply {
            color = Blue
        })
        it.drawPath(paths.pathList[0], paint.apply {
            color = androidx.compose.ui.graphics.Color.Black
            alpha = 0.9f
        })
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawTextWithBlendMode(
    mask: Path,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    text: String,
    textOffset: Offset,
    unitTextOffset: Offset,
    unitTextStyle: TextStyle,
) {
    drawText(
        textMeasurer = textMeasurer,
        topLeft = textOffset,
        text = text,
        style = textStyle,
    )
    drawText(
        textMeasurer = textMeasurer,
        topLeft = unitTextOffset,
        text = "FT",
        style = unitTextStyle,
    )

    drawPath(
        path = mask,
        color = Water,
        blendMode = BlendMode.SrcIn
    )
}

val paint = Paint().apply {
    this.color = androidx.compose.ui.graphics.Color.Blue
    pathEffect = PathEffect.cornerPathEffect(100f)
}