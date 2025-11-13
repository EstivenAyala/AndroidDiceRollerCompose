package com.estiven.dicerollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estiven.dicerollerapp.ui.theme.DiceRollerAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

val DiceColors = mapOf(
    1 to Color(0xFFF44336), // Rojo
    2 to Color(0xFFFF9800), // Naranja
    3 to Color(0xFF4CAF50), // Verde
    4 to Color(0xFF2196F3), // Azul
    5 to Color(0xFF9C27B0), // Púrpura
    6 to Color(0xFFFFEB3B), // Amarillo
    0 to Color(0xFFF0E6D2)  // Color "Hueso" inicial (usaremos 0 como estado inicial)
)

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
    //Estado del dadi: almacena el numero actual (1-6) y 0 para el estado inicial
    var diceNumber by remember { mutableStateOf(1) }
    //Estado de animación: Indica si el dado está rodando (bloquea el tap y muestra la animación)
    var isRolling by remember { mutableStateOf(false) }
    // Color de fondo aimado: Tansisciona el color suavemente (300ms por defecto).
    val targetColor = DiceColors[diceNumber] ?: DiceColors[0]!! //Usa el color mapeado o hueso si es nuelo
    val animateColor by animateColorAsState(targetColor, label = "colorAnimation")

    //Coroputine Scope para manejar el retraso de 3 segundos
    val coroutineScope = rememberCoroutineScope()

    //Estado de rotación y aniamción
    var rotationAngle by remember { mutableStateOf(0f) } //Angulo de rotación actual
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 300), //La rotación individual durara 300ms
        label = "diceRotationAnimation"
    )

    //Logica para lanzar el dado (Función suspendida para usar 'delay')
    fun rollDice() {
        if (isRolling) return //Ignora si ya esta rodando
        isRolling = true
        //Lanzamos una corrutina para manejar el tiempo de rodaje
        coroutineScope.launch {
            //Simulación de animación (Rodaje rápido en números)
            for (i in 0..9) { //Cambia el número 9 veces
                diceNumber = Random.nextInt(1,7) //Genera un número aleatorio entre 1 y 6
                rotationAngle = Random.nextFloat() * 360f //Gira a un angulo aleatorio
                delay(150)
            }

            //Añadir una ultima rotación antes de la pausa final
            rotationAngle = Random.nextFloat() * 360f
            diceNumber = Random.nextInt(1,7) //Ultimo numero de simulación antes del resultado final
            //Espera el tiempo restante para asegurar los 3 segundos de "rodaje"
            delay(1500)

            //Retraso final de 3 segundos total (aproximadamente, ya que la simulación usó 1.5s)
            //Calculamos el número final antes del retraso para que el fondo se actualice pronto
            val finalNumber = Random.nextInt(1,7)

            //Muestra el número final y detiene la aniamción
            rotationAngle = 0f //Restablece el ángulo al final para que el dado "se asiente"
            diceNumber = finalNumber
            isRolling = false
        }
    }

    //-----------------Estructura de la UI (Interfaz de Usuario)

    //Usamos un Box para poder apilar elementos (imagen de fondo, luego el dado, botones, etc.)
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(animateColor)
            .clickable(enabled = !isRolling) { rollDice() }, //El tap en cualquier lugar lanza el dado
        contentAlignment = Alignment.Center //Esto centrara cualquier contenido que agreguemos despues
    ) {
        //Muestra la imagen del dado

        val imageResource = when (diceNumber) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        Image(
            painter = painterResource(id = imageResource), //Aqui usamos la imagen que agregastes
            contentDescription = "Dado mostrando el número $diceNumber",
            modifier = Modifier
                .size(250.dp)
                //Aquí se aplica la animación de la rotación
                .rotate(animatedRotation) //Usa el valor animado de rotación
                .shadow(
                    elevation = 16.dp, //Intensidad de la sombra
                    shape = RoundedCornerShape(20.dp), // Forma de la sombra
                    spotColor = Color.Black //Color de la sombra
                )
                .border(
                    width = 5.dp, //Ancho del borde
                    color = Color.DarkGray.copy(alpha = 0.5f), //Color del borde
                    shape = RoundedCornerShape(20.dp) //Forma del borde
                )
                .clip(RoundedCornerShape(20.dp)) //Recorta las esquinas de la imagen misma
        )
        // Muestra un indicador de "Rodando..." si está en animación
        if (isRolling){
            Text(
                text = "Rodando...",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DiceRollerAppTheme {
        DiceRollScreen()
    }
}