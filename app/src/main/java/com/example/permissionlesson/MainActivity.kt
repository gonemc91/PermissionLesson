package com.example.permissionlesson

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.permissionlesson.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val feature1PermissionRequestLauncher = registerForActivityResult(
        RequestPermission(),
        ::onGotPermissionResultForFeature1
    )

    private val feature2PermissionRequestLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionsResultForFeature2
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.requestCameraPermissionButton.setOnClickListener {
            feature1PermissionRequestLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.requestRecordAudioAndLocationPermissionsButton.setOnClickListener {
            feature2PermissionRequestLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO))
        }


    }



    private fun onGotPermissionResultForFeature1(granted: Boolean){
        if (granted) {
            onCameraPermissionGranted()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                //show dialog with explanation here
                askForOpeningAppSettings()
            }else{
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotPermissionsResultForFeature2(grantedResults: Map<String, Boolean>){
        if(grantedResults.entries.all { it.value }){
            Toast.makeText(this, "Location & record audio granted", Toast.LENGTH_SHORT).show()
        }
    }





    private fun askForOpeningAppSettings(){
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        }else{
            AlertDialog.Builder(this)
                .setTitle("Permissions denied")
                .setMessage("You have denied permissions forever. " +
                "You can change your decision in app settings.\n\n" +
                "Would you like to open app setting?")
                .setPositiveButton("Open"){_,_->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }

    }



    private fun  onRecordAudioLocationPermissionsFGranted(){
        Toast.makeText(this, R.string.audio_and_location_permissions_granted, Toast.LENGTH_SHORT).show()
    }

    private fun onCameraPermissionGranted(){
        Toast.makeText(this, R.string.camera_permission_granted, Toast.LENGTH_SHORT).show()
    }

    private companion object{
        const val  RQ_PERMISSIONS_FOR_FEATURE_1_CODE = 1
        const val  RQ_PERMISSIONS_FOR_FEATURE_2_CODE = 2
    }

}