package ca.gbc.comp3074.jean_oneal.lab5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import ca.gbc.comp3074.jean_oneal.lab5.ui.theme.Lab5Theme
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationComponent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationComponent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val fusedClient = remember(key1 = context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()

    val location = remember { mutableStateOf<Location?>(value = null) }
    val status = remember { mutableStateOf(value = "") }
    var locationCallback = remember {mutableStateOf<LocationCallback?>(null)}

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun fetchLocation(){
        if(!hasPermission()){
            status.value = "Location permission not granted"
            return
        }
        val cancelToken = CancellationTokenSource()

        status.value = "Fetching Location..."
        fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
            cancelToken.token
        ).addOnSuccessListener { loc ->
            if (loc == null){
                status.value = "Location not found"
            }
            else{
                location.value = loc
                status.value = "Location: (%.5f, %.5f)".format(loc.latitude, loc.longitude)
            }
        }.addOnFailureListener { e ->
            status.value = "Location fetch failed: ${e.localizedMessage ?: "Unkown Error"}"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
            results ->
            if(results.values.any{it}){
                fetchLocation()
            }
            else{
                status.value = "Location permission not granted"
            }
        }

    fun startTracking(){
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateIntervalMillis(2000L).build()

        locationCallback.value = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                location.value = result.lastLocation
                status.value = "Location: (%.5f, %.5f)".format(
                    result.lastLocation?.latitude,
                    result.lastLocation?.longitude
                )
            }
        }
        locationCallback.value?.let{
            fusedClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()){
        Text(
            text = status.value,
            modifier = modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if(!hasPermission()){
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else{
                    fetchLocation()
                }
            },
            modifier = modifier.fillMaxWidth()
            ){
            Text(text = "Get Location")
        }
        Button(
            onClick = {
                status.value = "Tracking Location...."
                if(!hasPermission()){
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
                startTracking()
            },
            modifier = modifier.fillMaxWidth()
        ){
            Text(text = "Start Tracking")
        }
        Button(
            onClick = {
                status.value = "Stopping Location tracking..."
                locationCallback.value?.let { fusedClient.removeLocationUpdates(it) }
                locationCallback.value = null
            },
            modifier = modifier.fillMaxWidth()
        ){
            Text(text = "Stop Tracking")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationComponentPreview() {
    Lab5Theme {
        LocationComponent()
    }
}