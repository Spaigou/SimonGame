package com.example.simongame

import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.simongame.ui.theme.DarkBlue
import com.example.simongame.ui.theme.LightGreen
import com.example.simongame.ui.theme.SoftRed
import com.example.simongame.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FreePlay(navController: NavHostController, sounds: List<Int>) {
    Box {
        BackArrow(navController)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                SoundButton(
                    color = DarkBlue,
                    sound = sounds[0]
                )
                SoundButton(
                    color = SoftRed,
                    sound = sounds[1]
                )
            }
            Row {
                SoundButton(
                    color = LightGreen,
                    sound = sounds[2]
                )
                SoundButton(
                    color = Yellow,
                    sound = sounds[3]
                )
            }
        }
    }
}

@Composable
fun SoundButton(
    color: Color,
    sound: Int
) {
    var isEnabled by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(if (!isEnabled) 0.9f else 1f, label = "")
    val disabledColor: Color = darkenColor(color, 0.8f)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Button(
        onClick = {
            isEnabled = false
            scope.launch {
                MediaPlayer.create(context, sound).start()
                delay(300)
                isEnabled = true
            }
        },
        enabled = isEnabled,
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
