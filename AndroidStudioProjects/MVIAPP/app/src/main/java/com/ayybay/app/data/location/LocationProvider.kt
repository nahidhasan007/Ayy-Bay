package com.ayybay.app.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String?
)

/**
 * Thin wrapper over FusedLocationProviderClient + Geocoder for one-shot "where am I"
 * lookups (prayer times, Qibla). Not a continuous location subscription -- this app has
 * no need to track movement, only to know the user's city once (or on request).
 */
class LocationProvider(private val context: Context) {

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Returns null if permission isn't granted or no location could be resolved. */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        val fresh = suspendCancellableCoroutine<Location?> { cont ->
            val cancellationSource = CancellationTokenSource()
            cont.invokeOnCancellation { cancellationSource.cancel() }
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationSource.token)
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        }

        val location = fresh ?: suspendCancellableCoroutine<Location?> { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        }

        return location?.let { loc ->
            DeviceLocation(
                latitude = loc.latitude,
                longitude = loc.longitude,
                placeName = reverseGeocode(loc.latitude, loc.longitude)
            )
        }
    }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()
            address?.locality ?: address?.subAdminArea ?: address?.adminArea ?: address?.countryName
        } catch (e: Exception) {
            null
        }
    }
}
