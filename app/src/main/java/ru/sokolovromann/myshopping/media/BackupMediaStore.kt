package ru.sokolovromann.myshopping.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import javax.inject.Inject

class BackupMediaStore @Inject constructor(
    private val context: Context
) {

    fun checkCorrectWriteFilesPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            val checkSelfPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            checkSelfPermission == PackageManager.PERMISSION_GRANTED
        }
    }
}