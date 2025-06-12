package com.texttovoice.virtuai.ui.image

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Keyboard
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.collectAsEffect
import com.texttovoice.virtuai.common.components.ImageStyleItem
import com.texttovoice.virtuai.common.hideKeyboard
import com.texttovoice.virtuai.common.keyboardAsState
import com.texttovoice.virtuai.common.showRewarded
import com.texttovoice.virtuai.data.model.ImageStyle
import com.texttovoice.virtuai.data.model.Sizes
import com.texttovoice.virtuai.ui.chat.AdsAndProVersion
import com.yagmurerdogan.toasticlib.Toastic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import com.texttovoice.virtuai.R

@Composable
fun GenerateImageScreen(
    viewModel: GenerateImageViewModel = hiltViewModel(),
    navigateToShowImage: (String) -> Unit,
    bottomBarState: MutableState<Boolean>,
    navigateToUpgradeInfo: () -> Unit,
    navigateToUpgrade: () -> Unit,
) {
    val context = LocalContext.current

    val promptList: List<Int> = listOf(
        R.string.prompt_1,
        R.string.prompt_2,
        R.string.prompt_3,
        R.string.prompt_4,
        R.string.prompt_5,
        R.string.prompt_6,
        R.string.prompt_7,
        R.string.prompt_8,
        R.string.prompt_9,
        R.string.prompt_10,
    )

    fun getExampleList(): List<String> {
        val result = mutableListOf<String>()
        for (prompt in promptList) {
            result.add(context.getString(prompt))
        }
        return result
    }

    var exampleIndex by remember { mutableStateOf(0) }
    var typingHint by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    fun animateTypingRecursively(prompt: String, currentIndex: Int) {
        if (currentIndex < prompt.length) {
            val text = prompt[currentIndex]
            typingHint += text.toString()
            coroutineScope.launch {
                delay(50)
                animateTypingRecursively(prompt, currentIndex + 1)
            }
        } else {
            coroutineScope.launch {
                delay(1000)
                exampleIndex++


                if (exampleIndex < getExampleList().size) {
                    val example = getExampleList()[exampleIndex]
                    typingHint = ""
                    animateTypingRecursively(example, 0)
                } else {
                    exampleIndex = 0
                    typingHint = ""

                    val example = getExampleList()[exampleIndex]
                    typingHint = ""
                    animateTypingRecursively(example, 0)
                }
            }
        }
    }


    fun changeLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

    }

    LaunchedEffect(Unit) {

        viewModel.getCurrentLanguageCode()
        val currentLanguageCode = viewModel.currentLanguageCode.value

        changeLanguage(context, currentLanguageCode)

        if (exampleIndex < getExampleList().size) {
            val example = getExampleList()[exampleIndex]
            typingHint = ""
            animateTypingRecursively(example, 0)
        } else {
            exampleIndex = 0
            typingHint = ""
        }
    }


    val imageStylesList: List<ImageStyle> = listOf(
        ImageStyle(
            text = stringResource(R.string.no_style),
            image = R.drawable.none
        ),
        ImageStyle(
            text = stringResource(R.string.realistic),
            image = R.drawable.realistic,
            prompt = Constants.Prompts.REALISTIC
        ),
        ImageStyle(
            text = stringResource(R.string.cartoon),
            image = R.drawable.cartoon,
            prompt = Constants.Prompts.CARTOON
        ),
        ImageStyle(
            text = stringResource(R.string.pencil_sketch),
            image = R.drawable.pencil_sketch,
            prompt = Constants.Prompts.PENCIL_SKETCH
        ),
        ImageStyle(
            text = stringResource(R.string.oil_painting),
            image = R.drawable.oil_painting,
            prompt = Constants.Prompts.OIL_PAINTING
        ),
        ImageStyle(
            text = stringResource(R.string.water_color),
            image = R.drawable.water_color,
            prompt = Constants.Prompts.WATER_COLOR
        ),
        ImageStyle(
            text = stringResource(R.string.pop_art),
            image = R.drawable.pop_art,
            prompt = Constants.Prompts.POP_ART
        ),
        ImageStyle(
            text = stringResource(R.string.surrealist),
            image = R.drawable.surrealist,
            prompt = Constants.Prompts.SURREALIST
        ),
        ImageStyle(
            text = stringResource(R.string.pixel_art),
            image = R.drawable.pixel_art,
            prompt = Constants.Prompts.PIXEL_ART
        ),
        ImageStyle(
            text = stringResource(R.string.nouveau),
            image = R.drawable.nouveau,
            prompt = Constants.Prompts.NOUVEAU
        ),
        ImageStyle(
            text = stringResource(R.string.abstract_art),
            image = R.drawable.abstract_art,
            prompt = Constants.Prompts.ABSTRACT_ART
        ),
    )

    val freeMessageCount by viewModel.freeMessageCount.collectAsState()
    val isProVersion by viewModel.isProVersion.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.selectedValue.value = imageStylesList[0].text
    }

    LaunchedEffect(Unit) {
        viewModel.getProVersion()
        viewModel.getFreeMessageCount()
    }

    val promptText = remember { mutableStateOf("") }
    var hasFocus by remember { mutableStateOf(false) }

    val isKeyboardOpen by keyboardAsState() // Keyboard.Opened or Keyboard.Closed

    LaunchedEffect(isKeyboardOpen)
    {
        bottomBarState.value = isKeyboardOpen == Keyboard.Closed
    }


    val generatedImage by viewModel.state.collectAsState(initial = null)

    viewModel.state.collectAsEffect { generatedImage ->
        generatedImage?.let {
            when (generatedImage) {
                is Resource.Success -> {

                    generatedImage.data.data.getOrNull(0)?.url?.let { imageUrl ->
                        Log.e("GenerateImageScreen", "Generated Image Url: $imageUrl")
                        navigateToShowImage(imageUrl)
                    }


                }

                is Resource.Error -> {
                    generatedImage.throwable.message?.let {
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
                    text = stringResource(R.string.generate_image),
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
                            typingHint,
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

                    items(imageStylesList) {
                        ImageStyleItem(
                            text = it.text,
                            image = it.image,
                            selected = viewModel.selectedValue.value == it.text,
                            onClick = {
                                viewModel.selectedValue.value = it.text
                                viewModel.selectedPrompt.value = it.prompt
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = viewModel.showAdsAndProVersion.value,
                    enter = scaleIn(
                        initialScale =  0f ,
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

                            if (viewModel.isProVersion.value.not()) {

                                if (freeMessageCount > 0) {
                                    viewModel.decreaseFreeMessageCount()
                                } else {
                                    viewModel.showAdsAndProVersion.value = true
                                    return@bounceClick
                                }
                            }

                            viewModel.generateImage(
                                "${promptText.value}, ${viewModel.selectedPrompt}",
                                1,
                                Sizes.SIZE_1024
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
                            text = stringResource(R.string.generate_image),
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
