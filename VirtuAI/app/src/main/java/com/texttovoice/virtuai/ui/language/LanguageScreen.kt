package com.texttovoice.virtuai.ui.language

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.common.components.AppBar
import com.texttovoice.virtuai.data.model.LanguageModel
import com.texttovoice.virtuai.ui.theme.Urbanist
import java.util.*
import com.texttovoice.virtuai.R

@Composable
fun LanguageScreen(
    navigateToBack: () -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel(),
) {
    val languageList: List<LanguageModel> = listOf(
        LanguageModel(
            name = "English", code = "en"
        ), LanguageModel(
            name = "Türkçe", code = "tr"
        ), LanguageModel(
            name = "Español", code = "es"
        ), LanguageModel(
            name = "Français", code = "fr"
        ), LanguageModel(
            name = "Русский", code = "ru"
        ), LanguageModel(
            name = "عربي", code = "ar"
        ), LanguageModel(
            name = "辛斯", code = "zh"
        ), LanguageModel(
            name = "हिन्दी", code = "hi"
        ), LanguageModel(
            name = "বাংলা", code = "bn"
        )
    )


    LaunchedEffect(true) {
        languageViewModel.getCurrentLanguage()
    }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(
            onClickAction = navigateToBack,
            image = R.drawable.arrow_left,
            text = stringResource(R.string.language),
            MaterialTheme.colors.surface
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp)
        )
        {

            items(languageList) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 15.dp)
                        .click {
                            languageViewModel.selectedValue.value = it.code
                            languageViewModel.setCurrentLanguage(it.code, it.name)
                            changeLanguage(context, it.code)
                            navigateToBack()
                        }
                ) {
                    Text(
                        text = it.name,
                        color = MaterialTheme.colors.surface,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (languageViewModel.selectedValue.value == it.code) {
                        Icon(
                            painter = painterResource(id = R.drawable.done),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .size(27.dp)
                        )
                    }


                }

                Divider(
                    color = MaterialTheme.colors.secondary, thickness = 1.dp,
                )
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