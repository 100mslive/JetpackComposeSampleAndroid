package com.aniketkadam.videocon.permissions

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class PermissionState(
    val permission: String,
    val hasPermission: Flow<Boolean>,
    val shouldShowRationale: Flow<Boolean>,
    private val launcher: ActivityResultLauncher<String>
) {
    fun launchPermissionRequest() = launcher.launch(permission)
}

@ExperimentalCoroutinesApi
internal class PermissionResultCall(
    key: String,
    private val activity: ComponentActivity,
    private val permission: String
) {

    // defer this to allow construction before onCreate
    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val showRationale = MutableStateFlow<Boolean?>(null)

    // Don't do this in onCreate because compose setContent may be called in Activity usage before
    // onCreate is dispatched to this lifecycle observer (as a result, need to manually unregister)
    private var call: ActivityResultLauncher<String> = activity.activityResultRegistry.register(
        "LocationPermissions#($key)",
        ActivityResultContracts.RequestPermission()
    ) { result ->
        onPermissionResult(result)
    }

    /**
     * Call this after [Activity.onCreate] to perform the initial permissions checks
     */
    fun initialCheck() {
        hasPermission.value = checkPermission()
        showRationale.value = checkShowRationale()
    }

    fun unregister() {
        call.unregister()
    }

    fun checkSelfPermission(): PermissionState {
        return PermissionState(
            permission,
            hasPermission.filterNotNull(),
            showRationale.filterNotNull(),
            call
        )
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("ObsoleteSdkInt") // Depends on the app that's calling it. It will always be final in the app this may change between other apps where this is used.
    private fun checkShowRationale(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.shouldShowRequestPermissionRationale(permission)
        } else {
            false
        }
    }

    private fun onPermissionResult(result: Boolean) {
        hasPermission.value = result
        showRationale.value = checkShowRationale()
    }
}