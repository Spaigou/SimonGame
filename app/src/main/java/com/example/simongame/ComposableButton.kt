package com.example.simongame

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun darkenColor(originalColor: Color, factor: Float): Color {
    val red = (originalColor.red * factor).coerceIn(0f, 1f)
    val green = (originalColor.green * factor).coerceIn(0f, 1f)
    val blue = (originalColor.blue * factor).coerceIn(0f, 1f)
    val alpha = originalColor.alpha

    return Color(red, green, blue, alpha)
}

@Composable
fun Game.ComposableButton(
    color: Color,
    sound: Int,
    interactionSource: MutableInteractionSource,
    updateLevelAndRecord: (Int, Int) -> Unit,
    updateButtonStatus: (Boolean) -> Unit,
    buttonStatus: Boolean,
    updateTurn: (String) -> Unit,
) {
    var isEnabled by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(if (!isEnabled) 0.9f else 1f, label = "")
    val disabledColor: Color = darkenColor(color, 0.8f)

    Button(
        onClick = {
            handleClick(sound, updateLevelAndRecord, updateTurn, updateButtonStatus) {
                isEnabled = it
            }
        },
        interactionSource = interactionSource,
        enabled = isEnabled && buttonStatus,
        modifier = Modifier
            .padding(16.dp)
            .width(150.dp)
            .height(200.dp)
            .scale(scale),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = disabledColor
        )
    ) {}
}


fun Game.handleClick(
    sound: Int,
    updateLevelAndRecord: (Int, Int) -> Unit,
    updateTurn: (String) -> Unit,
    updateButtonStatus: (Boolean) -> Unit,
    setButtonEnabled: (Boolean) -> Unit
) {
    setButtonEnabled(false)

    coroutine.launch {
        playAudio(sound)
        delay(300)
        setButtonEnabled(true)
        clickButton(sound, updateLevelAndRecord, updateTurn, updateButtonStatus)
    }
}