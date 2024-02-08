package com.example.simongame

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun About(navController: NavHostController) {
    val context = LocalContext.current
    val record = context.getSharedPreferences("record", Context.MODE_PRIVATE).getInt("record", 1)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BackArrow(navController)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gameConditions = context.getString(R.string.game_conditions)
            val authorName = context.getString(R.string.author_name)
            val text = "Current record - $record\n$gameConditions\n$authorName"
            Text(text = text, fontSize = 20.sp)
        }
    }
}