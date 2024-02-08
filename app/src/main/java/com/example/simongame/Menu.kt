package com.example.simongame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun ShowMenu(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate(Screens.NewGame)
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "New game", fontSize = 25.sp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(Screens.FreePlay)
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "Free play", fontSize = 25.sp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(Screens.About)
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "About", fontSize = 25.sp)
        }
    }
}

@Composable
fun BackArrow(navController: NavHostController, resetLevel: () -> Unit = {}) {
    Button(
        onClick = {
            resetLevel()
            navController.navigate(Screens.MainMenu) {
                popUpTo(Screens.NewGame) {
                    inclusive = true
                }
                popUpTo(Screens.MainMenu) {
                    inclusive = true
                }
            }
        },
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "<-", fontSize = 20.sp)
    }
}