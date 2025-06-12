package com.texttovoice.virtuai.common.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.TableStyle
import com.halilibo.richtext.ui.material.MaterialRichText
import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.data.model.MessageModel
import com.texttovoice.virtuai.ui.chat.ChatViewModel
import com.texttovoice.virtuai.ui.theme.*

@Composable
fun MessageCard(
    message: MessageModel,
    isHuman: Boolean = false,
    isLast: Boolean = false,
    isFirst: Boolean = true
) {
    Column(
        horizontalAlignment = if (isHuman) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = if (isHuman) 0.dp else 10.dp)
            .padding(start = if (isHuman) 10.dp else 0.dp)
            .padding(vertical = 4.dp)
            .padding(top = if (isLast) 50.dp else 0.dp)
    ) {
        if (isHuman) {
            HumanMessageCard(message = message)
        } else {
            BotMessageCard(message = message, isFirst = isFirst)
        }
    }
}

@Composable
fun HumanMessageCard(message: MessageModel) {
    Box(
        modifier = Modifier
            .widthIn(0.dp)
            .background(
                Green,
                shape = RoundedCornerShape(
                    topEnd = 16.dp,
                    topStart = 16.dp,
                    bottomEnd = 5.dp,
                    bottomStart = 16.dp
                )
            ),
    ) {
        if (message.question.startsWith(Constants.START_WEB_LINK)) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.link),
                    tint = MaterialTheme.colors.surface,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .size(width = 27.dp, height = 27.dp)
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                     text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = White)) {
                            append(stringResource(id = R.string.web_page) + " (")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W800, color = Color.Blue)) {
                            append(message.question.split("||")[1])
                        }
                        withStyle(style = SpanStyle(color = White)) {
                            append(")")
                        }
                    },
                    color = White,
                    modifier = Modifier,
                    textAlign = TextAlign.Start,
                    style = Typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        } else {
            Text(
                text = message.question,
                color = White,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                textAlign = TextAlign.End,
                style = Typography.body1
            )
        }

    }


}

@Composable
fun BotMessageCard(
    message: MessageModel,
    isFirst: Boolean = true,
    viewModel: ChatViewModel = hiltViewModel()
) {

    val isGenerating by viewModel.isGenerating.collectAsState()
    val isProVersion by viewModel.isProVersion.collectAsState()
    val freeMessageCount by viewModel.freeMessageCount.collectAsState()

    LaunchedEffect(key1 = Unit ){
        viewModel.getProVersion()
        viewModel.getFreeMessageCount()
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message.answer.trimIndent())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current


    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {

        Box(
            modifier = Modifier
                .widthIn(0.dp)
                .background(
                    MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(
                        topEnd = 16.dp,
                        topStart = 5.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            expanded = true
                        }
                    )
                },
        ) {
            Column {
                MaterialRichText(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    style = RichTextStyle(
                        codeBlockStyle = CodeBlockStyle(
                            textStyle = TextStyle(
                                fontFamily = Urbanist,
                                fontSize = 14.sp,
                                color = White
                            ),
                            wordWrap = true,
                            modifier = Modifier.background(
                                shape = RoundedCornerShape(6.dp),
                                color = CodeBackground,
                            )
                        ),
                        tableStyle = TableStyle(borderColor = MaterialTheme.colors.surface),
                    )
                ) {
                    Markdown(
                        content = message.answer.trimIndent()
                    )
                }

                if (!isGenerating and isFirst) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                            .horizontalScroll(rememberScrollState())

                    ) {
                        Row(
                            modifier = Modifier
                                .bounceClick {

                                    if (isProVersion.not()) {

                                        if (freeMessageCount <= 0) {
                                            viewModel.showAdsAndProVersion.value = true
                                            return@bounceClick
                                        }
                                    }
                                    viewModel.regenerateAnswer()

                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                painterResource(R.drawable.regenerate),
                                "shareMessage",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(all = 2.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.regenerate),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                        Row(
                            modifier = Modifier
                                .bounceClick {
                                    clipboardManager.setText(AnnotatedString((message.answer.trimIndent())))
                                    expanded = false
                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Icon(
                                painterResource(R.drawable.copy),
                                "shareMessage",
                                modifier = Modifier.size(25.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.copy),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                        Row(
                            modifier = Modifier
                                .bounceClick {
                                    context.startActivity(shareIntent)
                                    expanded = false
                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                painterResource(R.drawable.share),
                                "shareMessage",
                                modifier = Modifier.size(25.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.share),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                        Row(
                            modifier = Modifier
                                .bounceClick {
                                    uriHandler.openUri(Constants.HELP)
                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                painterResource(R.drawable.flag),
                                "shareMessage",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(all = 2.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.report),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                        Row(
                            modifier = Modifier
                                .bounceClick {
                                    uriHandler.openUri(Constants.FEEDBACK)
                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                painterResource(R.drawable.feedback),
                                "shareMessage",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(all = 2.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.feedback),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                    }
                } else if (!isFirst) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                            .horizontalScroll(rememberScrollState())

                    ) {
                        Row(
                            modifier = Modifier
                                .bounceClick {
                                    clipboardManager.setText(AnnotatedString((message.answer.trimIndent())))
                                    expanded = false
                                }
                                .padding(end = 10.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(99.dp)
                                )
                                .padding(all = 5.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Icon(
                                painterResource(R.drawable.copy),
                                "shareMessage",
                                modifier = Modifier.size(25.dp),
                                tint = MaterialTheme.colors.surface,
                            )
                            Text(
                                text = stringResource(R.string.copy),
                                color = MaterialTheme.colors.surface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = Typography.body1
                            )
                        }
                    }
                }


            }

        }

//        MaterialTheme(
//            colors = MaterialTheme.colors.copy(surface = MaterialTheme.colors.surface),
//            shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(16))
//        ) {
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//                modifier = Modifier
//                    .background(MaterialTheme.colors.onSecondary, RoundedCornerShape(16.dp))
//                    .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(16.dp)),
//                properties = PopupProperties(focusable = false)
//            ) {
//                DropdownMenuItem(
//                    onClick = {
//                        clipboardManager.setText(AnnotatedString((message.answer.trimIndent())))
//                        expanded = false
//                    }
//                ) {
//                    Icon(
//                        painterResource(R.drawable.copy),
//                        "copyMessage",
//                        modifier = Modifier.size(25.dp),
//                        tint = MaterialTheme.colors.surface,
//                    )
//                    Text(
//                        text = stringResource(R.string.copy),
//                        color = MaterialTheme.colors.surface,
//                        modifier = Modifier.padding(horizontal = 10.dp),
//                        style = Typography.body1
//                    )
//                }
//                Divider(
//                    color = MaterialTheme.colors.secondary, thickness = 1.dp,
//                )
//                DropdownMenuItem(
//                    onClick = {
//                        context.startActivity(shareIntent)
//                        expanded = false
//                    }
//                )
//                {
//                    Icon(
//                        painterResource(R.drawable.share),
//                        "shareMessage",
//                        modifier = Modifier.size(25.dp),
//                        tint = MaterialTheme.colors.surface,
//                    )
//                    Text(
//                        text = stringResource(R.string.share),
//                        color = MaterialTheme.colors.surface,
//                        modifier = Modifier.padding(horizontal = 10.dp),
//                        style = Typography.body1
//                    )
//
//                }
//
//            }
//        }
//            Spacer(modifier = Modifier.width(10.dp))
//
//            Column {
//                IconButton(
//                    modifier = Modifier.size(25.dp),
//                    onClick = {
//
//                    }) {
//                    Icon(
//                        painterResource(R.drawable.copy),
//                        "copyMessage",
//                        modifier = Modifier.size(25.dp),
//                        tint = IconColor,
//                    )
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//                IconButton(
//                    modifier = Modifier.size(25.dp),
//                    onClick = {
//
//                    }) {
//                    Icon(
//                        painterResource(R.drawable.share),
//                        "shareMessage",
//                        modifier = Modifier.size(25.dp),
//                        tint = IconColor,
//                    )
//                }
//            }


    }


}