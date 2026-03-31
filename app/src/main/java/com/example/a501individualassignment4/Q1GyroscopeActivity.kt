package com.example.a501individualassignment4

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.max
import kotlin.math.min

class Q1GyroscopeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GyroMazeGame()
                }
            }
        }
    }
}

@Composable
fun GyroMazeGame() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val gyro = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }

    var ballX by remember { mutableStateOf(120f) }
    var ballY by remember { mutableStateOf(120f) }
    var velX by remember { mutableStateOf(0f) }
    var velY by remember { mutableStateOf(0f) }

    val ballRadius = 28f

    // Simple obstacles
    val obstacles = remember {
        listOf(
            Rect(250f, 100f, 300f, 500f),
            Rect(450f, 300f, 700f, 350f),
            Rect(100f, 650f, 500f, 700f)
        )
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                // event.values are angular velocities around x/y/z axes
                // Use small scaling so motion feels controlled
                velX += -event.values[1] * 1.8f
                velY += event.values[0] * 1.8f

                // damp a bit
                velX *= 0.96f
                velY *= 0.96f

                ballX += velX
                ballY += velY
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        if (gyro != null) {
            sensorManager.registerListener(
                listener,
                gyro,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (gyro == null) {
            Text("No gyroscope found on this device.", modifier = Modifier.align(Alignment.Center))
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // walls
                ballX = min(max(ballRadius, ballX), w - ballRadius)
                ballY = min(max(ballRadius, ballY), h - ballRadius)

                // collision with obstacles
                val nextRect = Rect(
                    ballX - ballRadius,
                    ballY - ballRadius,
                    ballX + ballRadius,
                    ballY + ballRadius
                )

                if (obstacles.any { it.overlaps(nextRect) }) {
                    // simple response: undo motion
                    ballX -= velX
                    ballY -= velY
                    velX = 0f
                    velY = 0f
                }

                // border
                drawRect(Color.Black, style = Fill)

                // play field background
                drawRect(
                    color = Color(0xFFEFEFEF),
                    topLeft = Offset(12f, 12f),
                    size = androidx.compose.ui.geometry.Size(w - 24f, h - 24f)
                )

                // goal
                drawRect(
                    color = Color(0xFF8BC34A),
                    topLeft = Offset(w - 130f, h - 130f),
                    size = androidx.compose.ui.geometry.Size(80f, 80f)
                )

                // obstacles
                obstacles.forEach {
                    drawRect(
                        color = Color(0xFF444444),
                        topLeft = Offset(it.left, it.top),
                        size = androidx.compose.ui.geometry.Size(it.width, it.height)
                    )
                }

                // ball
                drawCircle(
                    color = Color(0xFF1976D2),
                    radius = ballRadius,
                    center = Offset(ballX, ballY)
                )
            }
        }
    }
}