package com.texttovoice.virtuai.ui.speech

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import okhttp3.ResponseBody
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.texttovoice.virtuai.ui.theme.White
import java.io.File
import com.texttovoice.virtuai.R

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShowSpeechDialog(
    image: Int,
    responseBody: ResponseBody?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: ShowSpeechViewModel = viewModel()
) {
    val context = LocalContext.current
    var copyForDownload = File(context.cacheDir, "downloaded_audio.mp3")

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.speech_wave))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(key1 = Unit) {
        responseBody?.let {
            val audioFile = viewModel.saveToFile(context, it)
            viewModel.prepareMediaPlayer(context, audioFile)
            copyForDownload = audioFile
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onCleared()
        }
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
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            context.unregisterReceiver(onDownloadComplete)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background, RoundedCornerShape(35.dp))
                .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(35.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier
            ) {

                Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = painterResource(image),
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(
                                color = MaterialTheme.colors.onSecondary,
                                RoundedCornerShape(
                                    topStart = 35.dp,
                                    topEnd = 35.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStart = 35.dp,
                                    topEnd = 35.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )


                    )

                    if (viewModel.isVoicePlaying.value) {


                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 60.dp)
                        )

                    }
                }

            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = viewModel.currentTime.value,
                    color = MaterialTheme.colors.surface,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = Urbanist,
                        lineHeight = 25.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(5.dp))
                Slider(
                    modifier = Modifier
                        .weight(1f),
                    value = viewModel.progress.value.toFloat(),
                    onValueChange = { newPosition ->
                        viewModel.seekMediaPlayer(newPosition.toInt())
                    },
                    valueRange = 0f..viewModel.progressMax.value.toFloat(),
                    onValueChangeFinished = {

                    }
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = viewModel.maxTime.value,
                    color = MaterialTheme.colors.surface,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = Urbanist,
                        lineHeight = 25.sp
                    ),
                    textAlign = TextAlign.Center
                )

            }
            Spacer(modifier = Modifier.height(10.dp))

            IconButton(
                onClick = {
                    viewModel.playPauseAudio()
                },
                modifier = Modifier
                    .width(27.dp)
                    .height(27.dp)
                    .background(
                        color = MaterialTheme.colors.onPrimary,
                        RoundedCornerShape(90.dp)
                    )
            ) {
                Icon(
                    painter = if (viewModel.isVoicePlaying.value) painterResource(R.drawable.pause) else painterResource(
                        R.drawable.play_icon
                    ),
                    contentDescription = "image",
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Card(
                    modifier = Modifier
                        .height(60.dp)
                        .weight(1f)
                        .bounceClick {
                            onDismiss()
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
                                responseBody?.let {
                                    isDownloading.value = true
                                    viewModel.askPermissions(
                                        context = context,
                                        file = copyForDownload,
                                        fileName = "AI_Generated_Speech_${viewModel.getRandomString(5)}",
                                        mimeType = responseBody
                                            .contentType()
                                            .toString(),
                                        onDownloadComplete = {
                                            isDownloading.value = false
                                            isDownloadCompleted.value = true
                                        })
                                }
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
}




