package com.exyte.wave.waterdrops

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import com.exyte.wave.animating.WaveState
import com.exyte.wave.animating.waveProgressAsState
import com.exyte.wave.toPx
import com.exyte.wave.ui.theme.Water
import com.exyte.wave.waterdrops.canvas.drawTextWithBlendMode
import com.exyte.wave.waterdrops.canvas.drawWaves
import com.exyte.wave.waterdrops.wave.WaterDropText
import com.exyte.wave.waterdrops.wave.createAnimationsAsState

@OptIn(ExperimentalTextApi::class)
@Composable
fun WaterDropLayout(
    modifier: Modifier = Modifier,
    waveDurationInMills: Long = 6000L,
    content: () -> WaterDropText,
) {
    val waveParams = remember { content().waveParams }
    val animations = createAnimationsAsState(pointsQuantity = waveParams.pointsQuantity)

    val waveDuration by rememberSaveable { mutableStateOf(waveDurationInMills) }
    var waveState by remember { mutableStateOf(WaveState.StartReady) }

    val waveProgress by waveProgressAsState(
        timerState = waveState,
        timerDurationInMillis = waveDuration
    )

    val elementParams by remember { mutableStateOf(ElementParams()) }
    var containerSize by remember { mutableStateOf(IntSize(0, 0)) }

    val dropWaterDuration = rememberDropWaterDuration(
        elementSize = elementParams.size,
        containerSize = containerSize,
        duration = waveDuration
    )

    val waterLevel = remember(waveProgress, containerSize.height) {
        derivedStateOf {
            (waveProgress * containerSize.height).toInt()
        }
    }

    val levelState = createLevelAsState(
        waterLevelProvider = { waterLevel.value },
        bufferY = waveParams.bufferY,
        elementParams = elementParams
    )

    val paths = createPathsAsState(
        containerSize = containerSize,
        elementParams = elementParams,
        levelState = levelState,
        waterLevelProvider = { waterLevel.value.toFloat() },
        dropWaterDuration = dropWaterDuration,
        animations = animations,
        waveParams = waveParams
    )

    val textStyle = remember { content().textStyle }
    val textMeasurer = rememberTextMeasurer(100)
    val unitTextStyle = remember(textStyle) { textStyle.copy(fontSize = textStyle.fontSize / 2) }

    val text by remember {
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
        Offset(
            elementParams.position.x + (elementParams.size.width - (unitTextSize.width + textProgressSize.width)) / 2,
            elementParams.position.y - 50f
        )
    }

    val density = LocalDensity.current
    val unitTextProgress by remember(textOffset) {
        derivedStateOf {
            Offset(
                textOffset.x + textProgressSize.width,
                textOffset.y + (textStyle.fontSize / 2).toPx(density)
            )
        }
    }

    Canvas(
        modifier = Modifier
            .background(Water)
            .fillMaxSize()
    ) {
        drawWaves(paths.value)
    }

    Box(
        modifier = modifier
            .clickable {
                waveState = if (waveState == WaveState.Animating) {
                    WaveState.StartReady
                } else {
                    WaveState.Animating
                }
            }
            .onGloballyPositioned {
                containerSize = IntSize(it.size.width, it.size.height)
            }
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawTextWithBlendMode(
                    mask = paths.value[0],
                    textStyle = textStyle,
                    unitTextStyle = unitTextStyle,
                    textOffset = textOffset,
                    text = text,
                    unitTextOffset = unitTextProgress,
                    textMeasurer = textMeasurer,
                )
            }
    ) {
        Text(
            modifier = content().modifier
                .align(content().align)
                .onGloballyPositioned {
                    elementParams.position = it.positionInParent()
                    elementParams.size = it.size
                },
            text = "46FT",
            style = content().textStyle
        )
    }
}

@Stable
data class ElementParams(
    var size: IntSize = IntSize.Zero,
    var position: Offset = Offset(0f, 0f),
)