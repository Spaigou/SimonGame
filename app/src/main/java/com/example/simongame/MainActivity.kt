package com.example.simongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simongame.ui.theme.SimonGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimonGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val interactionSources = getButtonInteractionSources()
                    val sounds = mutableListOf(
                        R.raw.phonk_1,
                        R.raw.phonk_2,
                        R.raw.phonk_3,
                        R.raw.phonk_4
                    )
                    val coroutine = rememberCoroutineScope()
                    val game = Game(
                        this@MainActivity,
                        interactionSources,
                        sounds,
                        coroutine,
                        navController
                    )

                    NavHost(navController = navController, startDestination = Screens.MainMenu) {
                        composable(Screens.MainMenu) {
                            ShowMenu(navController)
                        }
                        composable(Screens.NewGame) {
                            game.Start()
                        }
                        composable(Screens.FreePlay) {
                            FreePlay(navController, sounds)
                        }
                        composable(Screens.About) {
                            About(navController)
                        }
                    }
                }
            }
        }
    }
}

object Screens {
    const val MainMenu = "mainMenu"
    const val NewGame = "newGame"
    const val FreePlay = "freePlay"
    const val About = "about"
}
