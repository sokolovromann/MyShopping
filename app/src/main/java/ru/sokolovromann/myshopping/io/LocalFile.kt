package ru.sokolovromann.myshopping.io

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.async
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

class LocalFile @Inject constructor(
    private val context: Context
) {

    private val dispatcher = Dispatcher.IO

    suspend fun writeFile(path: String, text: String): Boolean = withContext(dispatcher) {
        return@withContext async {
            if (!checkWriteAndRead()) { return@async false }
            File(path).apply {
                parentFile?.mkdirs()
                writeText(text)
            }.isFile
        }.await()
    }

    suspend fun readLine(uri: Uri): String? = withContext(dispatcher) {
        return@withContext async {
            if (!checkWriteAndRead()) { return@async null }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine()
                }
            }
        }.await()
    }

    private fun checkWriteAndRead(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}