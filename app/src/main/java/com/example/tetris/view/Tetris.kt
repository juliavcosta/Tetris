package com.example.tetris.view

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.model.PecaTetris
import com.example.tetris.model.TipoDePeca
import com.example.tetris.ui.theme.BLACK80
import com.example.tetris.ui.theme.GREEN
import com.example.tetris.ui.theme.WHITE
import kotlin.random.Random
import kotlinx.coroutines.*

const val LARGURA = 300
const val ALTURA = 600
const val TAMANHO_BLOCO = 30
const val LARGURA_TABULEIRO = LARGURA / TAMANHO_BLOCO
const val ALTURA_TABULIERO = ALTURA / TAMANHO_BLOCO
const val BORDA_TABULEIRO = 2f
const val TIMER_DELAY = 700

@Composable
fun Tetris(){
    var tabuleiro = remember { mutableStateOf(Array(ALTURA_TABULIERO) { Array(LARGURA_TABULEIRO) { Color.Black } }) }
    var peca by remember { mutableStateOf(gerarNovaPeca()) }
    var pontuacao by remember { mutableStateOf(0) }
    var level by remember { mutableIntStateOf(1)}
    var gameOver by remember { mutableStateOf(false)}
    var context = LocalContext.current

    fun getTimerDelay(pontuacao: Int): Int{
        return when{
            pontuacao >= 2500 -> 100
            pontuacao >= 1500 -> 200
            pontuacao >= 1000 -> 300
            pontuacao >= 500 -> 500
            else -> TIMER_DELAY
        }
    }

    fun reinicarJogo(){
        tabuleiro = Array(ALTURA_TABULIERO){Array(LARGURA_TABULEIRO){Color.Black} }
        peca = gerarNovaPeca()
        pontuacao = 0
        level = 1
        gameOver = false
    }
    when (pontuacao){
        500 -> level = 2
        1000 -> level = 3
        1500 -> level = 4
        2500 -> level = 5
    }

    LaunchedEffect(Unit) {
        while(!gameOver){
            val delayTime = getTimerDelay(pontuacao).toLong()
            delay(delayTime)
            if (moverPeca(tabuleiro,peca,0,1)){
                peca = peca.copy(y = peca.y + 1)
            }else{
                tabuleiro = fixarPecaTabuleiro(tabuleiro,peca)
                val removerLinhasCompletas = removerLinhasCompletas(tabuleiro)
                pontuacao += removerLinhasCompletas * 100
                peca = gerarNovaPeca()

                if(!moverPeca(tabuleiro,peca,0,0)){
                    gameOver = true
                    Toast.makeText(context,"Fim do Jogo!: $pontuacao", Toast.LENGTH_LONG).show()
                    delay(2000)
                    reinicarJogo()
                }
            }
        }
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
            },
            fontSize = 18.sp,
            color = GREEN,
            modifier = Modifier.padding(20.dp)


        )
        Text(
            text = "Lv: $level",
            fontSize = 18.sp,
            color = WHITE,
            modifier = Modifier.padding(10.dp)

        )
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(600.dp)
                .border(2.dp, WHITE)
                .background(Color.Gray)
                .clickable {

                    peca = peca.copy(indexRotacao = (peca.indexRotacao + 1) % peca.tipo.shapes.size)
                    if(!moverPeca(tabuleiro, peca,0,0 )){
                        peca = peca.copy(indexRotacao = (peca.indexRotacao - 1) % peca.tipo.shapes.size)
                        }
                }
                .pointerInput(Unit){
                    detectDragGestures { _, dragAmount ->

                        val dx = (dragAmount.x / TAMANHO_BLOCO). toInt().coerceIn(-1, 1)
                        val dy = (dragAmount.y / TAMANHO_BLOCO). toInt().coerceIn(0, 1)



                        if (!gameOver) {
                            if (dx != 0) {
                                if (moverPeca(tabuleiro, peca, dx, 0)) peca = peca.copy(x = peca.x + dx)

                            }
                            if (dy > 0){
                                if(moverPeca(tabuleiro, peca, dx = 0, dy)) peca = peca.copy(y = peca.y + dy)

                            }
                        }
                    }
                    detectTapGestures{
                        peca = peca.copy(indexRotacao = (peca.indexRotacao + 1) % peca.tipo.shapes.size)
                        if(!moverPeca(tabuleiro, peca,0,0 )){
                            peca = peca.copy(indexRotacao = (peca.indexRotacao - 1) % peca.tipo.shapes.size)

                        }


                    }

                }

        ){
            Canvas(modifier = Modifier.fillMaxSize()){
                val canvasLargura = size.width
                val canvasAltura = size.height
                val tamanhoBloco = (canvasLargura / LARGURA_TABULEIRO).coerceAtMost(canvasAltura / ALTURA_TABULIERO)

                val larguraTabuleiro = (canvasLargura / tamanhoBloco ).toInt()
                val alturaTabuleiro = (canvasAltura / tamanhoBloco).toInt()

                drawRect(color = Color.Black, size = Size(canvasLargura, canvasAltura))
                for (x in 0 until larguraTabuleiro){

                    drawLine(
                        color = WHITE,
                        start = Offset(x * tamanhoBloco, 0f ),
                        end = Offset(x * tamanhoBloco, canvasAltura)

                        )
                }
                 for(y in 0 until alturaTabuleiro){
                     drawLine(
                         color = WHITE,
                         start = Offset(0f, y * tamanhoBloco),
                         end = Offset(canvasLargura, y * tamanhoBloco)
                     )

                 }
                val formaDaPeca = peca.tipo. shapes[peca.indexRotacao]
                formaDaPeca.forEachIndexed{ i, row ->
                    row.forEachIndexed { j, block ->
                    if (block==1){
                        drawRect(
                            color = peca.tipo.cor,
                            topLeft = Offset(
                                (peca.x + j ) * tamanhoBloco + BORDA_TABULEIRO / 2,
                                (peca.y + i) * tamanhoBloco + BORDA_TABULEIRO / 2
                            ),
                            size = Size(tamanhoBloco - BORDA_TABULEIRO, tamanhoBloco - BORDA_TABULEIRO)
                        )
                    }
                        
                    }


                }

                for(y in tabuleiro[].indices){
                    for (x in tabuleiro[y].indices){
                        if (tabuleiro[y][x] != Color.Black){
                            drawRect(
                                color = tabuleiro[y][x],
                                topLeft = Offset(x * tamanhoBloco + BORDA_TABULEIRO / 2, y * tamanhoBloco + BORDA_TABULEIRO / 2),
                                size = Size(tamanhoBloco - BORDA_TABULEIRO, tamanhoBloco - BORDA_TABULEIRO)
                            )
                        }
                    }
                }

            }
        }
    }

}
private fun gerarNovaPeca(): PecaTetris{
    val tipoPeca = TipoDePeca.entries[Random.nextInt(TipoDePeca.entries.size)]
    return PecaTetris(tipo = tipoPeca,  LARGURA_TABULEIRO / 2 - tipoPeca.shapes[0][0].size/ 2, 0)
}
private fun moverPeca(tabuleiro: MutableState<Array<Array<Color>>>, peca: PecaTetris, dx: Int, dy: Int):Boolean{
    peca.tipo.shapes[peca.indexRotacao].forEachIndexed { i, row ->  
        row.forEachIndexed { j, block ->
            if (block ==1){
                val novoX = peca.x + j + dx
                val novoY =peca.y + i + dy
                if (novoX < 0 || novoX >= LARGURA_TABULEIRO || novoY >= ALTURA_TABULIERO || tabuleiro.value[novoY][novoX] != Color.Black) {
                    return false


                }

            }

        }
    }
    return true

}
private fun fixarPecaTabuleiro(tabuleiro: Array<Array<Color>>, peca: PecaTetris): Array<Array<Color>> {
    peca.tipo.shapes[peca.indexRotacao].forEachIndexed { i, row ->
        row.forEachIndexed { j, block ->
            if (block == 1) {
                tabuleiro[peca.y + i][peca.x + j] = peca.tipo.cor
            }
        }
    }
    return tabuleiro
}

private fun removerLinhasCompletas(tabuleiro: Array<Array<Color>>): Int {
    var limparLinhas = 0
    for (i in tabuleiro.indices.reversed()) {
        if (tabuleiro[i].all { it != Color.Black }) {
            for (k in i downTo 1) {
                tabuleiro[k] = tabuleiro[k - 1]
            }
            tabuleiro[0] = Array(LARGURA_TABULEIRO){Color.Black}
            limparLinhas++
        }
    }
    return limparLinhas
}
