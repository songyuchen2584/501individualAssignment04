package com.example.a501individualassignment4

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

class Q2LocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LocationMapScreen()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun LocationMapScreen() {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasPermission by remember { mutableStateOf(false) }
    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    var addressText by remember { mutableStateOf("Address will appear here") }
    var customMarkers by remember { mutableStateOf(listOf<LatLng>()) }

    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    userLatLng = latLng
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val results = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        addressText = results?.firstOrNull()?.getAddressLine(0) ?: "No address found"
                    } catch (e: Exception) {
                        addressText = "Could not resolve address"
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Request Location")
            }

            Button(onClick = {
                customMarkers = emptyList()
            }) {
                Text("Clear Markers")
            }
        }

        Text(
            text = addressText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapClick = { latLng ->
                customMarkers = customMarkers + latLng
            }
        ) {
            userLatLng?.let {
                Marker(
                    state = rememberUpdatedMarkerState(position = it),
                    title = "You are here"
                )
            }

            customMarkers.forEachIndexed { index, latLng ->
                Marker(
                    state = rememberUpdatedMarkerState(position = latLng),
                    title = "Custom Marker ${index + 1}"
                )
            }
        }
    }
}