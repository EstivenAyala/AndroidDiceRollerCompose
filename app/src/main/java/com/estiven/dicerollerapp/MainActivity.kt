package com.estiven.dicerollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.estiven.dicerollerapp.ui.theme.DiceRollerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerAppTheme {
                //Superficie de contenedor utilizando el color background del tema
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestro composable principal que mostrare el fondo
                    DiceRollScreen()
                }
                }
            }
        }
}

@Composable
fun DiceRollScreen(modifier: Modifier = Modifier) {
    //Usamos un Box para poder apilar elementos (imagen de fondo, luego el dado, botones, etc.)
    Box (
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center //Esto centrara cualquier contenido que agreguemos despues
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dice_1_initial), //Aqui usamos la imagen que agregastes
            contentDescription = "Fondo con dado en 1",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop //Escala la imagen para que llene todo el espacio
        )
        //Aqui es donde se agregara el botón "Roll Dice" y la lógica
        //De momento solo se ve el fondo
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiceRollerAppTheme {
        DiceRollScreen()
    }
}