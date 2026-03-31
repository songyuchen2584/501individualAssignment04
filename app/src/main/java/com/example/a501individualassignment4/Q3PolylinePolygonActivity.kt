package com.example.a501individualassignment4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class Q3PolylinePolygonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PolylinePolygonScreen()
                }
            }
        }
    }
}

@Composable
fun PolylinePolygonScreen() {
    val context = LocalContext.current

    val trail = remember {
        listOf(
            LatLng(42.3465, -71.0972),
            LatLng(42.3472, -71.0950),
            LatLng(42.3483, -71.0927),
            LatLng(42.3491, -71.0908)
        )
    }

    val area = remember {
        listOf(
            LatLng(42.3458, -71.1000),
            LatLng(42.3476, -71.1000),
            LatLng(42.3476, -71.0968),
            LatLng(42.3458, -71.0968)
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(trail.first(), 15f)
    }

    var width by remember { mutableStateOf(10f) }
    var useBlue by remember { mutableStateOf(true) }

    val polylineColor = if (useBlue) Color.Blue else Color.Red
    val polygonStroke = if (useBlue) Color(0xFF2E7D32) else Color(0xFF6A1B9A)
    val polygonFill = if (useBlue) Color(0x552E7D32) else Color(0x556A1B9A)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Overlay width: ${width.toInt()}")
            Slider(
                value = width,
                onValueChange = { width = it },
                valueRange = 4f..24f
            )

            Button(onClick = { useBlue = !useBlue }) {
                Text("Toggle Colors")
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            Polyline(
                points = trail,
                color = polylineColor,
                width = width,
                clickable = true,
                jointType = JointType.ROUND,
                onClick = {
                    Toast.makeText(context, "Trail: short hiking route", Toast.LENGTH_SHORT).show()
                }
            )

            Polygon(
                points = area,
                strokeColor = polygonStroke,
                fillColor = polygonFill,
                strokeWidth = width,
                clickable = true,
                onClick = {
                    Toast.makeText(context, "Area: highlighted park", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}