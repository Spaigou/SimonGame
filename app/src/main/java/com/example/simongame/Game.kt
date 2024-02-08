package com.example.simongame

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.simongame.ui.theme.DarkBlue
import com.example.simongame.ui.theme.GhostWhite
import com.example.simongame.ui.theme.LightGreen
import com.example.simongame.ui.theme.SoftRed
import com.example.simongame.ui.theme.Yellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Game(
    private val context: Context,
    private val interactionSources: ButtonInteractionSources,
    private val sounds: List<Int>,
    val coroutine: CoroutineScope,
    private val navController: NavHostController
) {
    private var sequence: MutableList<Int> = mutableListOf()
    private var playerInput: MutableList<Int> = mutableListOf()
    private var turn = 0
    private var gameStarted = false

    private var level = 1
    private val maxLevelShared = context.getSharedPreferences("record", Context.MODE_PRIVATE)
    private var record = maxLevelShared.getInt("record", 1)


    @Composable
    fun Start() {
        var currentLevel by remember { mutableIntStateOf(1) }
        var currentRecord by remember { mutableIntStateOf(record) }
        var currentTurn by remember { mutableStateOf("Listen carefully!") }
        var buttonStatus by remember { mutableStateOf(false) }

        val updateLevelAndRecord: (Int, Int) -> Unit = { newLevel, newRecord ->
            currentLevel = newLevel
            currentRecord = newRecord
        }

        LaunchedEffect(Unit) {
            coroutine.launch {
                showSequence({ currentTurn = it }) { buttonStatus = it }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GhostWhite)
        ) {
            BackArrow(navController) { resetLevel() }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Level: $currentLevel",
                        fontSize = 30.sp
                    )
                    Text(
                        text = "Record: $currentRecord",
                        fontSize = 30.sp
                    )
                }
                Text(fontSize = 30.sp, text = currentTurn)
                Row {
                    ComposableButton(
                        color = DarkBlue,
                        sound = sounds[0],
                        interactionSources.blueButton,
                        updateLevelAndRecord,
                        { buttonStatus = it },
                        buttonStatus
                    ) { currentTurn = it }
                    ComposableButton(
                        color = SoftRed,
                        sound = sounds[1],
                        interactionSources.redButton,
                        updateLevelAndRecord,
                        { buttonStatus = it },
                        buttonStatus
                    ) { currentTurn = it }
                }
                Row {
                    ComposableButton(
                        color = LightGreen,
                        sound = sounds[2],
                        interactionSources.greenButton,
                        updateLevelAndRecord,
                        { buttonStatus = it },
                        buttonStatus
                    ) { currentTurn = it }
                    ComposableButton(
                        color = Yellow,
                        sound = sounds[3],
                        interactionSources.yellowButton,
                        updateLevelAndRecord,
                        { buttonStatus = it },
                        buttonStatus
                    ) { currentTurn = it }
                }
            }
        }
    }

    private suspend fun showSequence(
        updateTurn: (String) -> Unit,
        updateButtonStatus: (Boolean) -> Unit
    ) {
        updateButtonStatus(false)
        delay(1000)
        if (!gameStarted) {
            for (i in 0 until 4) {
                val sound = sounds.random()
                pressButton(sound)
                sequence.add(sound)
            }
            gameStarted = true
        } else {
            for (i in 0 until sequence.size) {
                pressButton(sequence[i])
            }
            val sound = sounds.random()
            pressButton(sound)
            sequence.add(sound)
        }
        updateTurn("Your turn!")
        updateButtonStatus(true)
    }

    private suspend fun pressButton(sound: Int) {
        val press = PressInteraction.Press(Offset.Zero)
        val interactionSource = defineButton(sound, interactionSources)
        interactionSource.emit(press)
        playAudio(sound)
        interactionSource.emit(PressInteraction.Release(press))
        delay(1100)
    }


    private suspend fun playerTurn(
        updateLevelAndRecord: (Int, Int) -> Unit,
        updateTurn: (String) -> Unit,
        updateButtonStatus: (Boolean) -> Unit
    ) {
        if (playerInput.isNotEmpty() && sequence.isNotEmpty()) {
            if (playerInput[turn] == sequence[turn]) {
                turn++
                if (turn == sequence.size) {
                    level++
                    if (record <= level) {
                        record = level
                        maxLevelShared.edit().putInt("record", record).apply()
                    }
                    updateLevelAndRecord(level, record)
                    updateTurn("Listen carefully!")
                    turn = 0
                    playerInput.clear()

                    if (level == 6) {
                        // easter egg
                        for (i in 0 until 20) {
                            sequence.add(sounds.random())
                        }
                        delay(500)
                        for (i in 0 until sequence.size) {
                            playAudio(sequence[i])
                            if (i % 2 == 0) delay(450)
                            else delay(300)
                        }
                        showGameOverScreen(
                            updateLevelAndRecord,
                            "You're win! This short melody was for you!",
                            updateTurn,
                            updateButtonStatus
                        )
                    } else {
                        showSequence(updateTurn, updateButtonStatus)
                    }
                }
            } else {
                showGameOverScreen(
                    updateLevelAndRecord,
                    "Level - $level\nRecord - $record",
                    updateTurn,
                    updateButtonStatus
                )
            }
        }
    }


    private fun showGameOverScreen(
        updateLevelAndRecord: (Int, Int) -> Unit,
        text: String,
        updateTurn: (String) -> Unit,
        updateButtonStatus: (Boolean) -> Unit
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Game over")
        alertDialogBuilder.setMessage(text)
        alertDialogBuilder.setCancelable(false)
        resetLevel()

        alertDialogBuilder.setPositiveButton("Restart") { dialog, _ ->
            updateLevelAndRecord(level, record)
            updateTurn("Listen carefully!")
            coroutine.launch {
                showSequence(updateTurn, updateButtonStatus)
            }
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Menu") { dialog, _ ->
            updateLevelAndRecord(level, record)
            updateTurn("Listen carefully!")
            navController.navigate(Screens.MainMenu) {
                popUpTo(Screens.NewGame) {
                    inclusive = true
                }
                popUpTo(Screens.MainMenu) {
                    inclusive = true
                }
            }
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun resetLevel() {
        playerInput.clear()
        sequence.clear()
        level = 1
        turn = 0
        gameStarted = false
    }

    suspend fun clickButton(
        sound: Int,
        updateLevelAndRecord: (Int, Int) -> Unit,
        updateTurn: (String) -> Unit,
        updateButtonStatus: (Boolean) -> Unit
    ) {
        playerInput.add(sound)
        playerTurn(updateLevelAndRecord, updateTurn, updateButtonStatus)
    }

    private fun defineButton(
        sound: Int,
        interactionSources: ButtonInteractionSources
    ): MutableInteractionSource {
        return when (sound) {
            sounds[0] -> interactionSources.blueButton
            sounds[1] -> interactionSources.redButton
            sounds[2] -> interactionSources.greenButton
            else -> interactionSources.yellowButton
        }
    }

    fun playAudio(sound: Int) {
        MediaPlayer.create(context, sound).start()
    }
}

data class ButtonInteractionSources(
    val blueButton: MutableInteractionSource,
    val redButton: MutableInteractionSource,
    val greenButton: MutableInteractionSource,
    val yellowButton: MutableInteractionSource
)

@Composable
fun getButtonInteractionSources(): ButtonInteractionSources {
    return remember {
        ButtonInteractionSources(
            blueButton = MutableInteractionSource(),
            redButton = MutableInteractionSource(),
            greenButton = MutableInteractionSource(),
            yellowButton = MutableInteractionSource()
        )
    }
}
