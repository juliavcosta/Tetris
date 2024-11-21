package com.example.tetris.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import com.example.tetris.ui.theme.BLACK80
import com.example.tetris.ui.theme.WHITE

@Composable
fun Tetris(){

    var pontuacao by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(BLACK80),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = buildAnnotatedString {
                append("Pontuação: ")
                withStyle(
                    style = SpanStyle(
                        color = WHITE
                    )
                ){
                    append("$pontuacao")
                }
            }
        )
    }

}