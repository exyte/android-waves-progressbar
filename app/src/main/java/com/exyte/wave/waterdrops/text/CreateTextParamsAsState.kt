@file:OptIn(ExperimentalTextApi::class)

package com.exyte.wave.waterdrops.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import com.exyte.wave.toPx
import com.exyte.wave.waterdrops.ElementParams

@Composable
fun createTextParamsAsState(
    textStyle: TextStyle,
    waveProgress: Float,
    elementParams: ElementParams,
): State<TextParams> {
    val textMeasurer = rememberTextMeasurer(100)
    val unitTextStyle = remember(textStyle) { textStyle.copy(fontSize = textStyle.fontSize / 2) }

    val text by remember(waveProgress) {
        derivedStateOf {
            (waveProgress * 100).toInt().toString()
        }
    }

    val textProgressSize by remember(text) {
        derivedStateOf {
            textMeasurer.measure(
                text = AnnotatedString(text),
                style = textStyle,
            ).size
        }
    }

    val unitTextSize by remember(text) {
        derivedStateOf {
            textMeasurer.measure(
                text = AnnotatedString(text),
                style = unitTextStyle,
            ).size
        }
    }
    val textOffset = remember(elementParams.position, unitTextSize, textProgressSize) {
        derivedStateOf {
            Offset(
                elementParams.position.x + (elementParams.size.width - (unitTextSize.width + textProgressSize.width)) / 2,
                elementParams.position.y - 50f
            )
        }
    }

    val density = LocalDensity.current
    val unitTextOffset by remember(textOffset) {
        derivedStateOf {
            Offset(
                textOffset.value.x + textProgressSize.width,
                textOffset.value.y + (textStyle.fontSize / 2).toPx(density)
            )
        }
    }
    return produceState(
        initialValue = TextParams(
            textStyle = textStyle,
            unitTextStyle = unitTextStyle,
            textOffset = textOffset.value,
            unitTextOffset = unitTextOffset,
            text = text,
            textMeasurer = textMeasurer
        ),
        key1 = waveProgress,
        key2 = textOffset,
        key3 = textStyle,
    ) {
        this.value = TextParams(
            textStyle = textStyle,
            unitTextStyle = unitTextStyle,
            textOffset = textOffset.value,
            unitTextOffset = unitTextOffset,
            text = text,
            textMeasurer = textMeasurer
        )
    }
}