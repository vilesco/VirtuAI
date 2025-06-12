package com.texttovoice.virtuai.ui.speech

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.currentSeconds
import com.texttovoice.virtuai.common.seconds
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ShowSpeechViewModel : ViewModel() {

    private var mediaPlayerForStream: MediaPlayer? = null
    val isVoicePlaying = mutableStateOf(false)
    val progress = mutableStateOf(0)
    var progressMax = mutableStateOf(0)
    val maxTime = mutableStateOf("00:00")
    val currentTime = mutableStateOf("00:00")
    private var handler = Handler()
    private var runnable: Runnable? = null

    fun playPauseAudio() {

        mediaPlayerForStream?.let {
            if (it.isPlaying) {
                isVoicePlaying.value = false
                it.pause()
            } else {
                isVoicePlaying.value = true
                it.start()
            }
        } ?: kotlin.run {


        }
    }


    public override fun onCleared() {
        mediaPlayerForStream?.release()
        mediaPlayerForStream = null
        runnable?.let { it1 -> handler.removeCallbacks(it1) }

    }

    fun seekMediaPlayer(i: Int) {
        mediaPlayerForStream?.let { mediaPlayerForStream ->
            mediaPlayerForStream.seekTo(i * 1000)
            currentTime.value = millisecondsToTimer(mediaPlayerForStream.currentPosition)
            progress.value = mediaPlayerForStream.currentSeconds
        }

    }


    fun saveToFile(context: Context, responseBody: ResponseBody): File {
        val file = File(context.cacheDir, "downloaded_audio.mp3")
        file.outputStream().use { fileOutputStream ->
            responseBody.byteStream().use { inputStream ->
                inputStream.copyTo(fileOutputStream)
            }
        }
        return file
    }

    fun prepareMediaPlayer(context: Context, audioFile: File) {

        try {
            mediaPlayerForStream?.release()
            mediaPlayerForStream = MediaPlayer.create(context, Uri.fromFile(audioFile))
            runBlocking {
                mediaPlayerForStream?.setOnPreparedListener { mediaPlayer ->

                    maxTime.value = millisecondsToTimer(mediaPlayer.duration)
                    progressMax.value = mediaPlayer.seconds

                    runnable = Runnable {
                        progress.value = mediaPlayer.currentSeconds
                        val currentDuration = mediaPlayer.currentPosition
                        currentTime.value = millisecondsToTimer(currentDuration)
                        runnable?.let { handler.postDelayed(it, 1000) }
                    }
                    handler.postDelayed(runnable!!, 1000)
                }
                mediaPlayerForStream?.setOnCompletionListener {
                    isVoicePlaying.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("prepareMediaPlayer", e.toString())
        }

    }

    private fun millisecondsToTimer(milliseconds: Int): String {
        var timerString = ""
        var minuteString = ""
        val secondsString: String
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000)
        if (hours > 0) {
            timerString = "$hours:"
        }
        minuteString = minutes.toString()
        if (minutes < 10) {
            minuteString = "0$minutes"
        }
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }


        timerString = "$timerString$minuteString:$secondsString"
        return timerString
    }


    fun askPermissions(
        context: Context,
        file: File,
        fileName: String,
        mimeType: String,
        onDownloadComplete: () -> Unit
    ) {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(context)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save audio from the Web.")
                    .setPositiveButton("Accept") { _, _ ->
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )

                        downloadAudio(context, file, fileName, mimeType, onDownloadComplete)
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
                downloadAudio(context, file, fileName, mimeType, onDownloadComplete)
            }
        } else {
            downloadAudio(context, file, fileName, mimeType, onDownloadComplete)
        }


    }

    private fun downloadAudio(
        context: Context, sourceFile: File, fileName: String, mimeType: String,
        onDownloadComplete: () -> Unit
    ) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, use MediaStore
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType) // Set appropriate MIME type
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

                uri?.let {
                    resolver.openOutputStream(it).use { outputStream ->
                        FileInputStream(sourceFile).use { inputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                }
            } else {
                // For older versions, directly write to the Downloads directory
                val destinationFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                FileInputStream(sourceFile).use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            onDownloadComplete()

        } catch (e: Exception) {
            Log.e("DownloadAudio", "Error downloading file", e)
        }


    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}

