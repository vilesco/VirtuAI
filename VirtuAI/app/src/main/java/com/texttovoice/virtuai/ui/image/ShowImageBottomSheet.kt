package com.texttovoice.virtuai.ui.image

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.components.DiscardGenerateWarnDialog
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.ui.theme.White
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import java.io.File
import java.net.URL

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShowImageBottomSheet(
    imageData: String,
    canCancelState: MutableState<Boolean>,
    onCancelClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {

    val uriHandler = LocalUriHandler.current

    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }
    val cancelClicked = remember { mutableStateOf(false) }

    BackHandler(enabled = true, onBack = {
        if (!cancelClicked.value) {
            showDialog.value = true
            return@BackHandler
        } else {
            onCancelClick()
        }
    })



    if (showDialog.value) {
        DiscardGenerateWarnDialog(
            onConfirm = {
                showDialog.value = false
                onCancelClick() },
            onDismiss = { showDialog.value = false })
    }


    val isDownloading = remember { mutableStateOf(false) }
    val isDownloadCompleted = remember { mutableStateOf(false) }

    // BroadcastReceiver to listen for download completion
    val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
                isDownloading.value = false
                isDownloadCompleted.value = true
                // Optionally, perform additional actions upon completion
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        canCancelState.value = false
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            canCancelState.value = true
            context.unregisterReceiver(onDownloadComplete)
        }
    }



    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(35.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(3.dp)
                .background(MaterialTheme.colors.onPrimary, RoundedCornerShape(90.dp))
        )

        Box(contentAlignment = Alignment.CenterEnd) {
            Text(
                text = stringResource(R.string.generated_image),
                color = MaterialTheme.colors.surface,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )

            Card(
                modifier = Modifier
                    .size(40.dp)
                    .click {
                        uriHandler.openUri(Constants.HELP)
                    },
                elevation = 5.dp,
                backgroundColor = Green,
                shape = RoundedCornerShape(90.dp),

                ) {
                Row(
                    Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        contentDescription = null,
                        tint = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(2.dp)
                    )

                }

            }
        }


        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(10.dp)
        )
        GlideImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            imageModel = URL(imageData),
            imageOptions = ImageOptions(
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.Center,
            ),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(35.dp))
                }
            },


            )

        Row(modifier = Modifier.padding(vertical = 20.dp)) {
            Card(
                modifier = Modifier
                    .height(60.dp)
                    .weight(1f)
                    .bounceClick {
                        cancelClicked.value = true
                        onCancelClick()
                    },
                elevation = 0.dp,
                backgroundColor = GreenShadow,
                shape = RoundedCornerShape(90.dp),
            ) {
                Row(
                    Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = Green,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                            fontFamily = Urbanist
                        ),
                        textAlign = TextAlign.Center
                    )

                }
            }

            Spacer(modifier = Modifier.width(20.dp))
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .bounceClick {

                        if (!isDownloading.value && !isDownloadCompleted.value) {
                            isDownloading.value = true
                            askPermissions(imageData, context)
                        }

                    },
                elevation = 5.dp,
                backgroundColor = Green,
                shape = RoundedCornerShape(90.dp),
            ) {
                Row(
                    Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isDownloading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(35.dp),
                            color = White
                        )
                    } else
                        if (isDownloadCompleted.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.done),
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = stringResource(R.string.done),
                                color = White,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W700,
                                    fontFamily = Urbanist
                                ),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.download),
                                color = White,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W700,
                                    fontFamily = Urbanist
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                }


            }
        }
    }
}

fun askPermissions(url: String, context: Context) {

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
                .setMessage("Permission required to save photos from the Web.")
                .setPositiveButton("Accept") { _, _ ->
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                    )

                    downloadImage(context, url)
                }
                .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
            downloadImage(context, url)
        }
    } else {
        downloadImage(context, url)
    }


}

fun downloadImage(context: Context, url: String) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadUri = Uri.parse(url)
    val request = DownloadManager.Request(downloadUri).apply {
        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        setAllowedOverRoaming(false)
        setTitle("Downloading")
        setDescription("Downloading image")
        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DCIM,
            "${File.separator}AI_Generated_Image${getRandomString(5)}.jpg"
        )
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    }

    downloadManager.enqueue(request)
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

