package com.texttovoice.virtuai.ui.chat

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.ui.theme.White
import com.yagmurerdogan.toasticlib.Toastic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import com.texttovoice.virtuai.R

@Composable
fun LinkSummarizeBottomSheet(
    onCancelClick: () -> Unit = {},
    onConfirmClick: (String, String) -> Unit = { _, _ -> }
) {

    val linkText = remember { mutableStateOf("") }
    var hasFocus by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var showErrorToast by remember {
        mutableStateOf(false)
    }



    if (showErrorToast) {
        Toastic
            .toastic(
                context = context,
                message = context.resources.getString(R.string.invalid_url),
                duration = Toastic.LENGTH_LONG,
                type = Toastic.ERROR,
                isIconAnimated = true
            )
            .show()
        showErrorToast = false
    }


    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .border(1.dp, MaterialTheme.colors.onPrimary, RoundedCornerShape(35.dp))
            .padding(16.dp)
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(3.dp)
                .background(MaterialTheme.colors.onPrimary, RoundedCornerShape(90.dp))
        )

        Text(
            text = stringResource(R.string.enter_or_paste_link),
            color = MaterialTheme.colors.surface,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.W700,
                fontFamily = Urbanist,
                lineHeight = 25.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(10.dp)
        )

        OutlinedTextField(
            value = linkText.value,
            onValueChange = {
                linkText.value = it
            },
            label = null,
            placeholder = {
                Text(
                    stringResource(R.string.https),
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
                .height(50.dp)
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


        Row(modifier = Modifier.padding(vertical = 20.dp)) {

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .bounceClick {
                        CoroutineScope(Dispatchers.IO).launch {
                            var text = ""
                            try {
                                if (Patterns.WEB_URL.matcher(linkText.value).matches())
                                {
                                    val document = Jsoup
                                        .connect(linkText.value)
                                        .get()

                                    // Extract the text from the HTML content
                                    text = document.text()
                                    Log.e("LinkSummarizeBottomSheet", text)

                                } else {
                                    showErrorToast = true
                                    return@launch
                                }

                            } catch (e: IOException) {
                                Log.e("LinkSummarizeBottomSheet", "Failed to fetch HTML content", e)
                            }

                            withContext(Dispatchers.Main) {
                                onConfirmClick(linkText.value, text)
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
                    Text(
                        text = stringResource(R.string.add),
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