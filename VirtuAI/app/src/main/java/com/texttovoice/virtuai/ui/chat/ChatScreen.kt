package com.texttovoice.virtuai.ui.chat

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.components.AppBar
import com.texttovoice.virtuai.common.components.MessageCard
import com.texttovoice.virtuai.common.components.TextInput
import com.texttovoice.virtuai.common.showRewarded
import com.texttovoice.virtuai.data.model.MessageModel
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.RedShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.components.TextToSpeechWarnDialog
import com.texttovoice.virtuai.common.hideKeyboard
import com.texttovoice.virtuai.common.showInterstitial
import com.texttovoice.virtuai.data.model.GPTModel
import com.texttovoice.virtuai.ui.theme.Green_Thunder
import com.texttovoice.virtuai.ui.theme.Purple_Stars
import kotlinx.coroutines.delay
import java.net.URLDecoder

@Composable
fun ChatScreen(
    navigateToBack: () -> Unit,
    navigateToUpgrade: () -> Unit,
    name: String?,
    examples: List<String>?,
    inputTextDefault: MutableState<String>,
    viewModel: ChatViewModel = hiltViewModel(),
    savedStateHandle: SavedStateHandle? = null,
    navigateToChooseMedia: () -> Unit,
    navigateToUpgradeInfo: () -> Unit,
    navigateToScan: () -> Unit,
    navigateToSummarize: () -> Unit,
    navigateToChooseGPT: (String) -> Unit,
) {

    val freeMessageCount by viewModel.freeMessageCount.collectAsState()
    val isProVersion by viewModel.isProVersion.collectAsState()
    val conversationId by viewModel.currentConversationState.collectAsState()
    val messagesMap by viewModel.messagesState.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var currentName by remember {
        mutableStateOf(name ?: "")
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        TextToSpeechWarnDialog {
            showDialog = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getProVersion()
        viewModel.getFreeMessageCount()
        viewModel.getGPTModel()
        delay(2000)
        if (!isProVersion) {
            showInterstitial(context) {}
        }
    }

    LaunchedEffect(Unit) {

        if (name == "WEB") {
            currentName = ""
            navigateToSummarize()
        } else if (name == "SCAN") {
            currentName = ""
            navigateToScan()
        }


    }
    BackHandler(enabled = true, onBack = {
        viewModel.stopSpeech()
        navigateToBack()
    })
    val messages: List<MessageModel> =
        if (messagesMap[conversationId] == null) listOf() else messagesMap[conversationId]!!

    val paddingBottom =
        animateDpAsState(
            if (isGenerating) {
                90.dp
            } else if (viewModel.showAdsAndProVersion.value) {
                190.dp
            } else {
                0.dp
            },
            animationSpec = tween(Constants.TRANSITION_ANIMATION_DURATION), label = ""
        )

    // TODO - UpgradeView when turn back asking same question
    val inputText = remember {
        mutableStateOf( URLDecoder.decode(inputTextDefault.value, "UTF-8"))
    }

    LaunchedEffect(key1 = Unit ){
        inputTextDefault.value = ""
    }



    if (savedStateHandle?.get<String>(Constants.GPT) != null) {
        if (savedStateHandle.get<String>(Constants.GPT) == GPTModel.gpt35Turbo.name) {
            viewModel.setGPTModel(GPTModel.gpt35Turbo)
        } else {
            viewModel.setGPTModel(GPTModel.gpt4)
        }
        savedStateHandle.remove<String>(Constants.GPT)
    }

    if (savedStateHandle?.get<String>(Constants.OCR) != null) {
        inputText.value = savedStateHandle.get<String>(Constants.OCR)!!
        savedStateHandle.remove<String>(Constants.OCR)
    }

    if (savedStateHandle?.get<String>(Constants.LINK_SUMMARIZE) != null && savedStateHandle?.get<String>(
            Constants.LINK_SUMMARIZE_CONTENT
        ) != null
    ) {

        if (isProVersion.not()) {

            if (freeMessageCount > 0) {
                viewModel.sendMessage(
                    "${Constants.START_WEB_LINK}${
                        savedStateHandle.get<String>(
                            Constants.LINK_SUMMARIZE
                        )
                    }||${savedStateHandle.get<String>(Constants.LINK_SUMMARIZE_CONTENT)}"
                )
                savedStateHandle.remove<String>(Constants.LINK_SUMMARIZE)
                savedStateHandle.remove<String>(Constants.LINK_SUMMARIZE_CONTENT)
            } else {
                viewModel.showAdsAndProVersion.value = true
            }
        } else {
            viewModel.sendMessage(
                "${Constants.START_WEB_LINK}${
                    savedStateHandle.get<String>(
                        Constants.LINK_SUMMARIZE
                    )
                }||${savedStateHandle.get<String>(Constants.LINK_SUMMARIZE_CONTENT)}"
            )
            savedStateHandle.remove<String>(Constants.LINK_SUMMARIZE)
            savedStateHandle.remove<String>(Constants.LINK_SUMMARIZE_CONTENT)
        }


    }


    Column(
        Modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AppBar(
                onClickAction = {
                    viewModel.stopSpeech()
                    navigateToBack()
                },
                image = R.drawable.arrow_left,
                text = if (currentName.isNullOrBlank()) {
                    stringResource(R.string.app_name)
                } else {
                    currentName
                },
                MaterialTheme.colors.surface,
                isMainPage = true,
                isChatPage = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        viewModel.toggleTextToSpeech()
                        if (viewModel.textToSpeech.value) {
                            showDialog = true
                        }
                    },
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                ) {


                    Icon(
                        painter = painterResource(if (viewModel.textToSpeech.value) R.drawable.volume_up else R.drawable.volume_off),
                        contentDescription = "image",
                        tint = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .width(27.dp)
                            .height(27.dp)
                    )

                }
                Spacer(modifier = Modifier.width(10.dp))
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

        if (messages.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick {
                        (context as Activity).hideKeyboard()
                        navigateToChooseGPT(viewModel.selectedGPT.value.name)
                    }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .background(
                        MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            if (viewModel.selectedGPT.value == GPTModel.gpt35Turbo) {
                                R.drawable.thunder
                            } else {
                                R.drawable.stars
                            }
                        ),
                        contentDescription = "image",
                        tint = if (viewModel.selectedGPT.value == GPTModel.gpt35Turbo) {
                            Green_Thunder
                        } else {
                            Purple_Stars
                        },
                        modifier = Modifier
                            .width(27.dp)
                            .height(27.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(
                            id = if (viewModel.selectedGPT.value == GPTModel.gpt35Turbo) {
                                R.string.gpt_35
                            } else {
                                R.string.gpt_4
                            }
                        ),
                        color = MaterialTheme.colors.surface,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        )
                    )

                }

                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = null,
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(30.dp)
                )

            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colors.onPrimary,
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Icon(
                    painter = painterResource(
                        if (viewModel.selectedGPTForTitle.value == GPTModel.gpt35Turbo) {
                            R.drawable.thunder
                        } else {
                            R.drawable.stars
                        }
                    ),
                    contentDescription = "image",
                    tint = if (viewModel.selectedGPTForTitle.value == GPTModel.gpt35Turbo) {
                        Green_Thunder
                    } else {
                        Purple_Stars
                    },
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(
                        id = if (viewModel.selectedGPTForTitle.value == GPTModel.gpt35Turbo) {
                            R.string.gpt_35
                        } else {
                            R.string.gpt_4
                        }
                    ),
                    color = MaterialTheme.colors.surface,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = Urbanist,
                        lineHeight = 25.sp
                    )
                )


            }


        }


        Box(
            modifier = Modifier
                .weight(1f)
        )
        {


            if (messages.isEmpty() and viewModel.showAdsAndProVersion.value.not()) {
                if (examples.isNullOrEmpty()) {
                    Capabilities(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    Examples(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        examples = examples,
                        inputText = inputText
                    )
                }
            } else {
                MessageList(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = paddingBottom.value), messages
                )
            }

            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AnimatedVisibility(
                    visible = isGenerating,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(Constants.TRANSITION_ANIMATION_DURATION)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(Constants.TRANSITION_ANIMATION_DURATION)
                    ),
                    content = {
                        StopButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            viewModel.stopGenerate()
                        }
                    })

                AnimatedVisibility(
                    visible = viewModel.showAdsAndProVersion.value,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(Constants.TRANSITION_ANIMATION_DURATION)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(Constants.TRANSITION_ANIMATION_DURATION)
                    ),
                    content = {
                        AdsAndProVersion(
                            modifier = Modifier
                                .fillMaxWidth(),
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

            }

        }



        TextInput(inputText = inputText) {
            navigateToChooseMedia()
        }
    }

}

@Composable
fun AdsAndProVersion(modifier: Modifier, onClickWatchAd: () -> Unit, onClickUpgrade: () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Column {
            Text(
                text = stringResource(R.string.you_reach_free_message_limit),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = RedShadow,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(10.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 15.dp)
                        .bounceClick(onClick = onClickUpgrade)
                        .background(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colors.onSecondary
                        )
                        .border(
                            2.dp,
                            color = MaterialTheme.colors.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 15.dp, horizontal = 20.dp)
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.star_vector),
                        contentDescription = stringResource(R.string.app_name),
                        tint = Green,
                        modifier = Modifier
                            .size(width = 30.dp, height = 30.dp)


                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stringResource(id = R.string.upgrade_to_pro),
                        color = MaterialTheme.colors.onSurface,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 15.dp)
                        .bounceClick(onClick = onClickWatchAd)
                        .background(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colors.onSecondary
                        )
                        .border(
                            2.dp,
                            color = MaterialTheme.colors.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 15.dp, horizontal = 20.dp)
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.video),
                        contentDescription = stringResource(R.string.app_name),
                        tint = Green,
                        modifier = Modifier
                            .size(width = 30.dp, height = 30.dp)


                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stringResource(id = R.string.watch_ad),
                        color = MaterialTheme.colors.onSurface,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        )
                    )
                }
            }
        }


    }
}

@Composable
fun StopButton(modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 15.dp)
                .bounceClick(onClick = onClick)
                .background(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colors.onSecondary
                )
                .border(
                    2.dp,
                    color = MaterialTheme.colors.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 15.dp, horizontal = 20.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.square),
                contentDescription = stringResource(R.string.app_name),
                tint = Green,
                modifier = Modifier
                    .size(width = 30.dp, height = 30.dp)


            )
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(id = R.string.stop_generating),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                )
            )
        }
    }
}

@Composable
fun Capabilities(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(width = 80.dp, height = 80.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.capabilities),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.capabilities_1),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.capabilities_2),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.capabilities_3),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.capabilities_desc),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
fun Examples(
    modifier: Modifier = Modifier,
    examples: List<String>,
    inputText: MutableState<String>
) {
    Box(modifier = modifier) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.type_something_like),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center
            )


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(examples) { example ->
                    Text(
                        text = example,
                        color = MaterialTheme.colors.onSurface,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .bounceClick(
                                onClick = {
                                    inputText.value = example
                                })
                            .background(
                                color = MaterialTheme.colors.onSecondary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                            .fillMaxWidth()

                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }


        }
    }
}

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messages: List<MessageModel>,
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier) {
        LazyColumn(
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize(),
            reverseLayout = true,
            state = listState,
        ) {
            items(messages.size) { index ->
                Box(modifier = Modifier.padding(bottom = if (index == 0) 10.dp else 0.dp)) {
                    Column {
                        MessageCard(
                            message = messages[index],
                            isLast = index == messages.size - 1,
                            isFirst = index == 0,
                            isHuman = true
                        )
                        MessageCard(
                            message = messages[index], isFirst = index == 0,
                        )
                    }
                }
            }
        }
    }
}
