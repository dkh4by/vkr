package com.example.electronicreception.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView

@Composable
fun YandexMapPicker(
    latitude: Double?,
    longitude: Double?,
    onPointSelected: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context)
    }

    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                onPointSelected(point.latitude, point.longitude)
            }

            override fun onMapLongTap(map: Map, point: Point) {
                onPointSelected(point.latitude, point.longitude)
            }
        }
    }

    DisposableEffect(Unit) {
        mapView.mapWindow.map.addInputListener(inputListener)
        mapView.onStart()

        onDispose {
            mapView.mapWindow.map.removeInputListener(inputListener)
            mapView.onStop()
        }
    }

    LaunchedEffect(latitude, longitude) {
        val lat = latitude ?: 55.1789
        val lng = longitude ?: 58.4330

        val point = Point(lat, lng)

        mapView.mapWindow.map.move(
            CameraPosition(
                point,
                14.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.4f),
            null
        )

        mapView.mapWindow.map.mapObjects.clear()
        mapView.mapWindow.map.mapObjects.addPlacemark(point)
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        factory = {
            mapView
        }
    )
}