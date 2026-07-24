package ru.sokolovromann.myshopping.core.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import androidx.core.net.toUri

class LocalFileManager @Inject constructor(
    private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun writeFile(path: String, content: String): Boolean =
        withContext(ioDispatcher) {
            if (!checkWriteAndRead()) return@withContext false
            try {
                File(path).apply {
                    parentFile?.mkdirs()
                    writeText(content)
                }.isFile
            } catch (_: Exception) { false }
        }

    suspend fun readFile(uriString: String): String? =
        withContext(ioDispatcher) {
            if (!checkWriteAndRead()) return@withContext null
            try {
                val inputStream = context.contentResolver.openInputStream(uriString.toUri())
                inputStream?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                }
            } catch (_: Exception) { null }
        }

    private fun checkWriteAndRead(): Boolean =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else true
}