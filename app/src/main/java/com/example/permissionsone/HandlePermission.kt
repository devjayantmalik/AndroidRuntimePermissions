package com.example.permissionsone

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.startActivity
import java.lang.Exception

class HandlePermission(
    private val permission: String,
    private val permissionName: String,
    private val context: Activity,
    private val appPackageName: String,
    private val onPermissionGrantedSuccess: () -> Unit
) {
    private var launcher: ActivityResultLauncher<String>? = null
    private var isSuccessInvokedOnce: Boolean = false

    fun setLauncher(launch: ActivityResultLauncher<String>) {
        launcher = launch
    }

    private fun useLauncher(): ActivityResultLauncher<String> {
        if (launcher == null) throw Exception("Launcher is not yet set. Kindly use setLauncher to set it prior to using it.")
        return launcher as ActivityResultLauncher<String>
    }

    fun handleContract(isGranted: Boolean) {
        // Return if permission is Granted
        if (isGranted) return

        // if permission is declined
        // open settings incase we are permanently declined this permission
        if (!shouldShowRequestPermissionRationale(context, permission)) {
            createPermanentDeclinedAlert()
            return
        }

        // Tell user why we need this permission, and retry to access this permission.
        createReAttemptAlert()
        return
    }


    fun showPrompt() {
        // Execute onPermissionGrantedSuccess incase permission is already allowed.
        if(isPermissionGranted()) return

        // Ask user for permission.
        useLauncher().launch(permission)
    }



    fun isPermissionGranted(): Boolean {
        // Return if already invoked.
        if (isSuccessInvokedOnce) return true

        // invoke onPermissionGrantedSuccess if all permissions are granted.
        val isPermissionGranted = ActivityCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED
        if (isPermissionGranted) {
            isSuccessInvokedOnce = true
            onPermissionGrantedSuccess()
            return true
        }

        // Return false in case permission is not granted.
        return false;
    }



    private fun createPermanentDeclinedAlert() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Permission Required")
        alert.setMessage("Permission: '$permissionName' is Required to use this application but it is declined. Allow this permission in app settings to continue.")
        alert.setPositiveButton("Grant Permission") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:$appPackageName"))
            startActivity(context, intent, null)
        }

        alert.show()
    }

    private fun createReAttemptAlert() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Permission Required")
        alert.setMessage("Permission: '$permissionName' is Required to use this application but it is declined. Click the Retry button to allow it.")
        alert.setPositiveButton("Grant Permission") { _, _ ->
            showPrompt()
        }

        alert.show()
    }
}