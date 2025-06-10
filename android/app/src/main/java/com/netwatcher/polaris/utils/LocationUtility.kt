package eh.learning.homepage.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher

object LocationUtility {

    fun isLocationEnabled(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            false
        }
    }

    fun requestEnableLocation(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        launcher.launch(intent)
    }
}