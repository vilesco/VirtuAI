package com.texttovoice.virtuai.ui.speech

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.texttovoice.virtuai.common.components.AppBar
import com.texttovoice.virtuai.ui.theme.*
import com.texttovoice.virtuai.common.Keyboard
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.collectAsEffect
import com.texttovoice.virtuai.common.components.VoiceStyleItem
import com.texttovoice.virtuai.common.hideKeyboard
import com.texttovoice.virtuai.common.keyboardAsState
import com.texttovoice.virtuai.common.showRewarded
import com.texttovoice.virtuai.data.model.VoiceStyle
import com.texttovoice.virtuai.ui.chat.AdsAndProVersion
import com.yagmurerdogan.toasticlib.Toastic
import okhttp3.ResponseBody
import com.texttovoice.virtuai.R


@Composable
fun TextToSpeechScreen(
    viewModel: TextToSpeechViewModel = hiltViewModel(),
    navigateToShowImage: (String) -> Unit,
    bottomBarState: MutableState<Boolean>,
    navigateToUpgradeInfo: () -> Unit,
    navigateToUpgrade: () -> Unit,
) {
    val context = LocalContext.current

    val voiceStyleList: List<VoiceStyle> = listOf(
        VoiceStyle(
            voiceName = stringResource(R.string.alloy),
            image = R.drawable.alloy,
            voice = "alloy",
            voiceFile = R.raw.alloy
        ),
        VoiceStyle(
            voiceName = stringResource(R.string.echo),
            image = R.drawable.echo,
            voice = "echo",
            voiceFile = R.raw.echo
        ),
        VoiceStyle(
            voiceName = stringResource(R.string.fable),
            image = R.drawable.fable,
            voice = "fable",
            voiceFile = R.raw.fable
        ),
        VoiceStyle(
            voiceName = stringResource(R.string.onyx),
            image = R.drawable.onyx,
            voice = "onyx",
            voiceFile = R.raw.onyx
        ),
        VoiceStyle(
            voiceName = stringResource(R.string.nova),
            image = R.drawable.nova,
            voice = "nova",
            voiceFile = R.raw.nova
        ),
        VoiceStyle(
            voiceName = stringResource(R.string.shimmer),
            image = R.drawable.shimmer,
            voice = "shimmer",
            voiceFile = R.raw.shimmer
        ),
    )

    val freeMessageCount by viewModel.freeMessageCount.collectAsState()
    val isProVersion by viewModel.isProVersion.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.selectedValue.value = voiceStyleList[0].voice
        viewModel.selectedVoiceImage.value = voiceStyleList[0].image

    }

    LaunchedEffect(Unit) {
        viewModel.getProVersion()
        viewModel.getFreeMessageCount()
    }

    val promptText = remember { mutableStateOf("") }
    var hasFocus by remember { mutableStateOf(false) }
    var responseBody by remember { mutableStateOf<ResponseBody?>(null) }

    val isKeyboardOpen by keyboardAsState() // Keyboard.Opened or Keyboard.Closed

    LaunchedEffect(isKeyboardOpen)
    {
        bottomBarState.value = isKeyboardOpen == Keyboard.Closed
    }


    DisposableEffect(Unit) {
        onDispose {
            viewModel.onCleared()
        }
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        ShowSpeechDialog(
            image = viewModel.selectedVoiceImage.value,
            responseBody = responseBody,
            onConfirm = { /*TODO*/ },
            onDismiss = { showDialog = false })
    }


    val generatedImage by viewModel.state.collectAsState(initial = null)

    viewModel.state.collectAsEffect { createdAudio ->
        createdAudio?.let {
            when (createdAudio) {
                is Resource.Success -> {
                    responseBody = createdAudio.data
                    showDialog = true
                }

                is Resource.Error -> {
                    createdAudio.throwable.message?.let {
                        Toastic
                            .toastic(
                                context = context,
                                message = it,
                                duration = Toastic.LENGTH_LONG,
                                type = Toastic.ERROR,
                                isIconAnimated = true
                            )
                            .show()
                    }
                }

                else -> {}
            }
        }

    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppBar(
                    onClickAction = {},
                    image = R.drawable.app_icon,
                    text = stringResource(R.string.text_to_speech),
                    Green
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    if (isProVersion) {
                        Text(
                            text = stringResource(R.string.pro),
                            color = MaterialTheme.colors.primary,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = Urbanist,
                                lineHeight = 25.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(GreenShadow, shape = RoundedCornerShape(90.dp))
                                .padding(horizontal = 9.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .background(GreenShadow, shape = RoundedCornerShape(90.dp))
                                .click {
                                    navigateToUpgradeInfo()
                                }
                                .padding(horizontal = 9.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.app_icon),
                                contentDescription = "image",
                                modifier = Modifier
                                    .width(27.dp)
                                    .height(27.dp)
                                    .padding(end = 5.dp)
                            )

                            Text(
                                text = freeMessageCount.toString(),
                                color = MaterialTheme.colors.primary,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.W600,
                                    fontFamily = Urbanist,
                                    lineHeight = 25.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }


            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = promptText.value,
                    onValueChange = {
                        promptText.value = it
                    },
                    label = null,
                    placeholder = {
                        Text(
                            stringResource(id = R.string.enter_prompt),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onSurface,
                            fontFamily = Urbanist,
                            fontWeight = FontWeight.W600
                        )
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.surface,
                        fontSize = 16.sp,
                        fontFamily = Urbanist,
                        fontWeight = FontWeight.W600
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 200.dp)
                        .heightIn(max = 200.dp)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .border(
                            1.dp,
                            if (hasFocus) Green else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        backgroundColor = if (hasFocus) GreenShadow else MaterialTheme.colors.secondary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp)
                )
                {

                    items(voiceStyleList) {
                        VoiceStyleItem(
                            text = it.voiceName,
                            image = it.image,
                            selected = viewModel.selectedValue.value == it.voice,
                            isVoicePlaying = viewModel.playingVoice.value == it.voiceFile,
                            onClick = {
                                viewModel.selectedValue.value = it.voice
                                viewModel.selectedVoiceImage.value = it.image
                                viewModel.playPauseAudio(context, it.voiceFile)

                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = viewModel.showAdsAndProVersion.value,
                    enter = scaleIn(
                        initialScale = 0f,
                        animationSpec = tween(1000)
                    ),
                    exit = scaleOut(
                        targetScale = 1f,
                        animationSpec = tween(1000)
                    ),
                    content = {
                        AdsAndProVersion(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClickWatchAd = {
                                showRewarded(context) {
                                    viewModel.showAdsAndProVersion.value = false
                                    viewModel.increaseFreeMessageCount()
                                }
                            },
                            onClickUpgrade = {
                                navigateToUpgrade()
                            })


                    })

                Row(
                    Modifier
                        .fillMaxWidth()
                        .bounceClick(onClick = {
                            if (promptText.value.isEmpty()) {
                                Toastic
                                    .toastic(
                                        context = context,
                                        message = context.resources.getString(R.string.please_enter_prompt),
                                        duration = Toastic.LENGTH_LONG,
                                        type = Toastic.ERROR,
                                        isIconAnimated = true
                                    )
                                    .show()
                                return@bounceClick
                            }
                            if (generatedImage is Resource.Loading) {
                                return@bounceClick
                            }
                            (context as Activity).hideKeyboard()
                            viewModel.onCleared()
                            if (viewModel.isProVersion.value.not()) {

                                if (freeMessageCount > 0) {
                                    viewModel.decreaseFreeMessageCount()
                                } else {
                                    viewModel.showAdsAndProVersion.value = true
                                    return@bounceClick
                                }
                            }

                            viewModel.createSpeech(
                                context,
                                promptText.value
                            )
                        })
                        .height(85.dp)
                        .padding(15.dp)
                        .background(Green, RoundedCornerShape(99.dp))
                        .border(
                            1.dp,
                            color = Green,
                            shape = RoundedCornerShape(99.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (generatedImage is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(35.dp),
                            color = White
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.convert_to_speech),
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
