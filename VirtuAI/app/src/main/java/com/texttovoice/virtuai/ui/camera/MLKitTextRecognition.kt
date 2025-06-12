package com.texttovoice.virtuai.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.ui.theme.Green
import java.util.concurrent.Executors

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST = 34

private fun foregroundPermissionApproved(context: Context): Boolean {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    )
}

private fun requestForegroundPermission(context: Context) {
    val provideRationale = foregroundPermissionApproved(context)

    if (provideRationale) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )
    }
}


@Composable
fun MLKitTextRecognition(onNavigateBack: () -> Unit, onShot : (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val extractedText = remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        requestForegroundPermission(context)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextRecognitionView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            extractedText = extractedText,
            onNavigateBack = onNavigateBack,
            onShot = onShot
        )
        Text(
            text = extractedText.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            color = Color.Black
        )
    }
}

@Composable
fun TextRecognitionView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    extractedText: MutableState<String>,
    onNavigateBack: () -> Unit,
    onShot : (String) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()
    val textRecognizer = remember { TextRecognition.getClient() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(
                                cameraExecutor,
                                ObjectDetectorImageAnalyzer(textRecognizer, extractedText)
                            )
                        }
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageAnalysis,
                        preview
                    )
                }, executor)
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                previewView
            }
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight().padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { onNavigateBack() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "back",
                        Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        onShot(extractedText.value)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(Green, shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "back",
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp).size(40.dp)
                    )
                }
            }
        }

    }
}

class ObjectDetectorImageAnalyzer(
    private val textRecognizer: TextRecognizer,
    private val extractedText: MutableState<String>
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(image)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        extractedText.value = it.result?.text ?: ""
                    }
                    imageProxy.close()
                }
        }
    }
}
