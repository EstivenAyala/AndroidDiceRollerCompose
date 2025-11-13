package com.estiven.dicerollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estiven.dicerollerapp.ui.theme.DiceRollerAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
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

    //Estado 3D Animatable para controlar el progreso de la animación
    val rotationProgress = remember { Animatable(0f) }

    //Valores fijos (o aleatorios) para el giro final en 3 ejes
    var targetRotations by remember { mutableStateOf(Triple(0f, 0f, 0f)) } // X, Y, Z

    //Logica para lanzar el dado (Función suspendida para usar 'delay')
    fun rollDice() {
        if (isRolling) return //Ignora si ya esta rodando
        isRolling = true
        //performVibration(50)

        //Lanzamos una corrutina para manejar el tiempo de rodaje
        coroutineScope.launch {
            //Simulación de animación (Rodaje rápido en números)
            for (i in 0..9) { //Cambia el número 9 veces
                diceNumber = Random.nextInt(1,7) //Genera un número aleatorio entre 1 y 6
                rotationAngle = Random.nextFloat() * 360f //Gira a un angulo aleatorio
                delay(150)
            }
            // Calcular las rotaciones Finales (que seran gigantes para simular el giro)
            // Esto asegura que el dado gire muchas veces de forma caótica
            targetRotations = Triple(
                Random.nextFloat() * 1080 + 720f, // De 2 a 5 vueltas en X
                Random.nextFloat() * 1080 + 720f, // De 2 a 5 vueltas en Y
                Random.nextFloat() * 1080 + 720f // De 2 a 5 vueltas Z
            )

            // Ejecutar la animación del dado
            rotationProgress.animateTo(
                targetValue = 1f, //Mueve el progreso 0f a 1f
                animationSpec = tween(
                    durationMillis = 3000, // Duración de la animación (3 segundos)
                    easing = FastOutSlowInEasing //Un buen ritmo de animación
                )
            )

            //Finalizar animación

            //Añadir una ultima rotación antes de la pausa final
            rotationAngle = Random.nextFloat() * 360f
            diceNumber = Random.nextInt(1,7) //Ultimo numero de simulación antes del resultado final
            //Espera el tiempo restante para asegurar los 3 segundos de "rodaje"
            delay(1500)

            //Retraso final de 3 segundos total (aproximadamente, ya que la simulación usó 1.5s)
            //Calculamos el número final antes del retraso para que el fondo se actualice pronto
            val finalNumber = Random.nextInt(1,7)

            //perfomVibration(200)

            // Reiniciar el progreso para la proxima tirada y actualizar el numero final
            rotationProgress.snapTo(0f) // Detiene la animación instantáneamente para el reposo
            targetRotations = Triple(0f, 0f, 0f) // Reposición en (0,0,0)
            isRolling = false

            //Muestra el número final y detiene la aniamción
            rotationAngle = 0f //Restablece el ángulo al final para que el dado "se asiente"
            diceNumber = finalNumber
            isRolling = false
        }
    }

    //Calculo de valores animados
    // El valor 0.0f del Animatable representa el inicio, 1.0f representa el final de los 3s
    val progress = rotationProgress.value

    //Rotaciones
    var rotationX = targetRotations.first * progress // Rotacion en X animado
    var rotationY = targetRotations.second * progress // Rotación en Y animado
    var rotationZ = targetRotations.third * progress // Rotación en Z animado

    // Escala (Efecto de acercarse/alejarse) durante el lanzamiento
    val scale = (0.9f + 0.1f * sin(progress * Math.PI).toFloat()) //Hace que se agrande un poco al centro

    // Transparencia (alpha) para una transición suave
    var alpha = 0.5f + 0.5f + progress

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
                .size(200.dp)
                //Aquí se aplica la animación de la rotación
                .graphicsLayer {
                    cameraDistance = 12f * density
                    //Distancia de la camara
                    this.rotationX = rotationX
                    this.rotationY = rotationY
                    this.rotationZ = rotationZ //Intercambio de los ejes para un giro mas interesante

                    cameraDistance = 12f * density
                    scaleX = scale
                    scaleY = scale

                    alpha = if (isRolling) 1f else 1f //Mantiene la opacidad
                }
                //.rotate(animatedRotation) //Usa el valor animado de rotación
                .shadow(
                    elevation = 24.dp, //Intensidad de la sombra
                    shape = RoundedCornerShape(20.dp), // Forma de la sombra
                    spotColor = Color.Black //Color de la sombra
                )
                .border(
                    width = 4.dp, //Ancho del borde
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