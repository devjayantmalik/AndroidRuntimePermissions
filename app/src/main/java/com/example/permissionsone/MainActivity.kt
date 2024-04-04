package com.example.permissionsone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var handlePermission: HandlePermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Create handle Permission
        handlePermission = HandlePermission(
            permission = android.Manifest.permission.CAMERA,
            permissionName = "Camera",
            context =  this@MainActivity,
            appPackageName = packageName,
            onPermissionGrantedSuccess = {
                Toast.makeText(this, "Congrats, You did allow us permission!", Toast.LENGTH_LONG).show()
            }
        )

        // Create launcher and attach to handlePermissions
        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { handlePermission.handleContract(it) }
        handlePermission.setLauncher(launcher)



        // Attach onClick listeners for buttons
        findViewById<Button>(R.id.btnRequestSinglePermission).setOnClickListener {
            // start process of permission requesting
            handlePermission.showPrompt()
            Log.d("logs", "you requested single permission.")
        }

        findViewById<Button>(R.id.btnRequestMultiplePermission).setOnClickListener {

        }

    }

    override fun onResume() {
        super.onResume()
        handlePermission.isPermissionGranted()
    }

}