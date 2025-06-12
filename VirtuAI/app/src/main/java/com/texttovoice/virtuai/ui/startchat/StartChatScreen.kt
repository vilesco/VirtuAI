package com.texttovoice.virtuai.ui.startchat

import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.BannerAdView
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Keyboard
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.components.AppBar
import com.texttovoice.virtuai.common.components.NoConnectionDialog
import com.texttovoice.virtuai.common.components.ThereIsUpdateDialog
import com.texttovoice.virtuai.common.keyboardAsState
import com.texttovoice.virtuai.data.model.ExamplesModel
import com.texttovoice.virtuai.ui.activity.isOnline
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.ui.upgrade.PurchaseHelper
import com.texttovoice.virtuai.ui.widget.ChatAiWidgetReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.Locale


@Composable
fun StartChatScreen(
    navigateToChat: (String, String, List<String>?, String) -> Unit,
    navigateToUpgrade: () -> Unit,
    navigateToWidgets: () -> Unit,
    startChatViewModel: StartChatViewModel = hiltViewModel(),
    purchaseHelper: PurchaseHelper,
    bottomBarState: MutableState<Boolean>,
) {
    val isKeyboardOpen by keyboardAsState() // Keyboard.Opened or Keyboard.Closed

    LaunchedEffect(isKeyboardOpen)
    {
        bottomBarState.value = isKeyboardOpen == Keyboard.Closed
    }

    val context = LocalContext.current

    val examples: List<ExamplesModel> = listOf(
        ExamplesModel(
            image = R.drawable.chat_vector,
            name = stringResource(id = R.string.explain),
            example = listOf(
                R.string.explain_example_1,
                R.string.explain_example_2,
                R.string.explain_example_3
            )
        ),
        ExamplesModel(
            image = R.drawable.create,
            name = stringResource(id = R.string.create),
            example = listOf(
                R.string.create_example_1,
                R.string.create_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.translate,
            name = stringResource(id = R.string.translate),
            example = listOf(
                R.string.translate_example_1,
                R.string.translate_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.mail_vector,
            name = stringResource(id = R.string.email),
            example = listOf(
                R.string.email_example_1,
                R.string.email_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.sugges,
            name = stringResource(id = R.string.recommend),
            example = listOf(
                R.string.recommend_example_1,
                R.string.recommend_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.history,
            name = stringResource(id = R.string.history_main),
            example = listOf(
                R.string.history_example_1,
                R.string.history_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.food_vector,
            name = stringResource(id = R.string.recipe),
            example = listOf(
                R.string.recipe_example_1,
                R.string.recipe_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.math,
            name = stringResource(id = R.string.math),
            example = listOf(
                R.string.math_example_1,
                R.string.math_example_2
            )
        ),
        ExamplesModel(
            image = R.drawable.social,
            name = stringResource(id = R.string.social),
            example = listOf(
                R.string.social_example_1,
                R.string.social_example_2,
                R.string.social_example_3
            )
        )
    )

    fun getExampleList(): List<String> {
        val result = mutableListOf<String>()
        for (exampleCategory in examples) {
            for (exampleItem in exampleCategory.example) {
                result.add(context.getString(exampleItem))
            }
        }
        return result
    }

    var exampleIndex by remember { mutableStateOf(0) }
    var typingHint by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    fun animateTypingRecursively(example: String, currentIndex: Int) {
        if (currentIndex < example.length) {
            val text = example[currentIndex]
            typingHint += text.toString()
            coroutineScope.launch {
                delay(50)
                animateTypingRecursively(example, currentIndex + 1)
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

    val addWidgetIntent = Intent(context, ChatAiWidgetReceiver::class.java)
    addWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_PICK

    var showDialog by remember {
        mutableStateOf(false)
    }
    var showUpdateDialog by remember {
        mutableStateOf(false)
    }

    val freeMessageCount by startChatViewModel.freeMessageCount.collectAsState()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var hasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startChatViewModel.getCurrentLanguageCode()
        val currentLanguageCode = startChatViewModel.currentLanguageCode.value

        changeLanguage(context, currentLanguageCode)


        startChatViewModel.getFirstTime()
        startChatViewModel.getProVersion()
        startChatViewModel.getProVersionFromAPI()
        startChatViewModel.getFreeMessageCount()
        startChatViewModel.getFreeMessageCountFromAPI()
        startChatViewModel.isThereUpdate()



        delay(1000)

        purchaseHelper.checkPurchase {
            if (it.not()) {

                startChatViewModel.setProVersion(false)
            }
        }

        if (startChatViewModel.isProVersion.value.not() && startChatViewModel.isFirstTime.value && isOnline(
                context
            ) && startChatViewModel.isThereUpdate.value.not()
        ) {
            startChatViewModel.setFirstTime(false)
//            navigateToUpgrade()
        }

        if (isOnline(context).not()) {
            showDialog = true
        }

        if (startChatViewModel.isThereUpdate.value) {
            showUpdateDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (exampleIndex < getExampleList().size) {
            val example = getExampleList()[exampleIndex]
            typingHint = ""
            animateTypingRecursively(example, 0)
        } else {
            exampleIndex = 0
            typingHint = ""
        }
    }



    if (showDialog) {
        NoConnectionDialog {
            showDialog = false
        }
    }

    if (showUpdateDialog) {
        ThereIsUpdateDialog {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            Constants.FEEDBACK
                        )
                    )
                )
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.muratozturk.virtuai")
                    )
                )
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AppBar(
                onClickAction = {},
                image = R.drawable.app_icon,
                text = stringResource(R.string.app_name),
                Green,
                isMainPage = true
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        navigateToWidgets()
                    },
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                ) {

                    Icon(
                        painter = painterResource(R.drawable.widget),
                        contentDescription = "image",
                        tint = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .padding(2.dp)
                            .width(27.dp)
                            .height(27.dp)
                    )

                }
                Spacer(modifier = Modifier.width(10.dp))


                if (startChatViewModel.isProVersion.value) {
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
                                navigateToUpgrade()
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


        if (startChatViewModel.isProVersion.value.not()) {
            BannerAdView()
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(5.dp),
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {

                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .border(
                            2.dp,
                            color = MaterialTheme.colors.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (startChatViewModel.isProVersion.value.not()) {
                        Text(
                            text = stringResource(R.string.pro),
                            color = MaterialTheme.colors.primary,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W700,
                                fontFamily = Urbanist,
                                lineHeight = 25.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(GreenShadow, shape = RoundedCornerShape(90.dp))
                                .padding(horizontal = 9.dp)
                        )

                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .bounceClick {
                                if (startChatViewModel.isProVersion.value.not()) {
                                    navigateToUpgrade()
                                } else {
                                    navigateToChat("SCAN", "", null, "")
                                }

                            }
                            .background(
                                MaterialTheme.colors.onSecondary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 10.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.scan_description),
                            color = MaterialTheme.colors.surface,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = Urbanist,
                                lineHeight = 25.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                painter = painterResource(R.drawable.scan),
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier
                                    .size(width = 27.dp, height = 27.dp)

                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = stringResource(id = R.string.camera),
                                color = MaterialTheme.colors.primary,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W600,
                                    fontFamily = Urbanist,
                                    lineHeight = 25.sp
                                )
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp)
                            .bounceClick {
                                if (startChatViewModel.isProVersion.value.not()) {
                                    navigateToUpgrade()
                                } else {
                                    navigateToChat("WEB", "", null, "")
                                }

                            }
                            .background(
                                MaterialTheme.colors.onSecondary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 10.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(id = R.string.summarize_this_webpage),
                            color = MaterialTheme.colors.surface,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = Urbanist,
                                lineHeight = 25.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                painter = painterResource(R.drawable.link),
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier
                                    .size(width = 27.dp, height = 27.dp)
                                    .padding(2.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = stringResource(id = R.string.add_url),
                                color = MaterialTheme.colors.primary,
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

                Spacer(modifier = Modifier.height(5.dp))

                examples.forEach { example ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(example.image),
                                contentDescription = stringResource(R.string.app_name),
                                tint = MaterialTheme.colors.surface,
                                modifier = Modifier
                                    .size(width = 30.dp, height = 30.dp)

                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = example.name,
                                color = MaterialTheme.colors.surface,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W700,
                                    fontFamily = Urbanist,
                                    lineHeight = 25.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        example.example.forEach { exampleString ->

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .bounceClick(onClick = {
                                        text =
                                            text.copy(text = context.getString(exampleString))
                                    })
                                    .fillMaxWidth()
                                    .background(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                    .padding(15.dp)
                            ) {
                                Text(
                                    text = stringResource(id = exampleString),
                                    color = MaterialTheme.colors.onSurface,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W600,
                                        fontFamily = Urbanist,
                                        lineHeight = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }

                        }


                    }

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .padding(all = 5.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp)
                    .heightIn(max = 120.dp)
                    .border(
                        1.dp,
                        if (hasFocus) Green else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        if (hasFocus) GreenShadow else MaterialTheme.colors.secondary,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
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
                            .weight(1f) // This will make the TextField take up available space // Add padding to the end
                            .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colors.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = {
                            val processedText = URLEncoder.encode(text.text, "UTF-8")

                            navigateToChat(
                                "",
                                "",
                                null,
                                processedText,
                            )
                        },
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.send),
                            "sendMessage",
                            modifier = Modifier.size(25.dp),
                            tint = MaterialTheme.colors.surface,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

//        Column(
//            Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//
//            Image(
//                painter = painterResource(R.drawable.app_icon),
//                contentDescription = stringResource(R.string.app_name),
//                modifier = Modifier.size(150.dp)
//            )
//
//            Spacer(modifier = Modifier.height(30.dp))
//
//            Column(
//                modifier = Modifier
//                    .background(MaterialTheme.colors.background)
//                    .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(35.dp))
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                if (startChatViewModel.isProVersion.value.not()) {
//                    Text(
//                        text = stringResource(R.string.pro),
//                        color = MaterialTheme.colors.primary,
//                        style = TextStyle(
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.W700,
//                            fontFamily = Urbanist,
//                            lineHeight = 25.sp
//                        ),
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .background(GreenShadow, shape = RoundedCornerShape(90.dp))
//                            .padding(horizontal = 9.dp)
//                    )
//
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp)
//                        .bounceClick {
//                            if (startChatViewModel.isProVersion.value.not()) {
//                                navigateToUpgrade()
//                            } else {
//                                navigateToChat("SCAN", "", null)
//                            }
//
//                        }
//                        .background(
//                            MaterialTheme.colors.onSecondary,
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .padding(vertical = 10.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.scan_description),
//                        color = MaterialTheme.colors.surface,
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.W600,
//                            fontFamily = Urbanist,
//                            lineHeight = 25.sp
//                        )
//                    )
//                    Spacer(modifier = Modifier.height(5.dp))
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//
//                        Icon(
//                            painter = painterResource(R.drawable.scan),
//                            tint = MaterialTheme.colors.primary,
//                            contentDescription = stringResource(R.string.app_name),
//                            modifier = Modifier
//                                .size(width = 27.dp, height = 27.dp)
//
//                        )
//                        Spacer(modifier = Modifier.width(15.dp))
//                        Text(
//                            text = stringResource(id = R.string.camera),
//                            color = MaterialTheme.colors.primary,
//                            style = TextStyle(
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.W600,
//                                fontFamily = Urbanist,
//                                lineHeight = 25.sp
//                            )
//                        )
//
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(5.dp))
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(1.dp)
//                        .bounceClick {
//                            if (startChatViewModel.isProVersion.value.not()) {
//                                navigateToUpgrade()
//                            } else {
//                                navigateToChat("WEB", "", null)
//                            }
//
//                        }
//                        .background(
//                            MaterialTheme.colors.onSecondary,
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .padding(vertical = 10.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    Text(
//                        text = stringResource(id = R.string.summarize_this_webpage),
//                        color = MaterialTheme.colors.surface,
//                        style = TextStyle(
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.W600,
//                            fontFamily = Urbanist,
//                            lineHeight = 25.sp
//                        )
//                    )
//                    Spacer(modifier = Modifier.height(5.dp))
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//
//                        Icon(
//                            painter = painterResource(R.drawable.link),
//                            tint = MaterialTheme.colors.primary,
//                            contentDescription = stringResource(R.string.app_name),
//                            modifier = Modifier
//                                .size(width = 27.dp, height = 27.dp)
//                                .padding(2.dp)
//                        )
//                        Spacer(modifier = Modifier.width(15.dp))
//                        Text(
//                            text = stringResource(id = R.string.add_url),
//                            color = MaterialTheme.colors.primary,
//                            style = TextStyle(
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.W600,
//                                fontFamily = Urbanist,
//                                lineHeight = 25.sp
//                            )
//                        )
//
//                    }
//
//
//                }
//            }
//
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            AnimatedButton(
//                onClick = {
//                    navigateToChat(
//                        "",
//                        "",
//                        null
//                    )
//                },
//                text = stringResource(R.string.start_chat)
//            )
//
//        }
    }

}

