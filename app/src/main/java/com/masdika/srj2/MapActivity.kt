package com.masdika.srj2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(111.5232, -7.6298))
                .pitch(0.0)
                .zoom(10.0)
                .bearing(15.0)
                .build()
        )
        setContentView(mapView)

        // Location Permission
        if (PermissionsManager.areLocationPermissionsGranted(this@MapActivity)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }

        // Genereting user location
        with(mapView) {
            location.locationPuck = createDefault2DPuck(withBearing = true)
            location.enabled = true
            //location.puckBearing = PuckBearing.HEADING
            viewport.transitionTo(
                targetState = viewport.makeFollowPuckViewportState(),
                transition = viewport.makeImmediateViewportTransition()
            )
        }

        // This will get the most suitable DeviceLocationProvider that is available.
        val locationService: LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null
        val request = LocationProviderRequest.Builder()
            .interval(
                IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L)
                    .build()
            )
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
        } else {
            Log.i("Error : ", "Failed to get device location provider")
        }

        // Requesting Location Update
        val locationObserver = object : LocationObserver {
            override fun onLocationUpdateReceived(locations: MutableList<com.mapbox.common.location.Location>) {
                Log.i(TAG, "Location update received: " + locations)
            }
        }
        locationProvider?.addLocationObserver(locationObserver)
        //to Stop Receiving Update
        //locationProvider?.removeLocationObserver(locationObserver);
    }

    // Permission Listener
    var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        }

        override fun onPermissionResult(granted: Boolean) {
            if (granted) {
                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            } else {
                // User denied the permission
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}