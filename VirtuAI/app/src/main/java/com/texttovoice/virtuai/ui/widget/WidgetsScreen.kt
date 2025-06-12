package com.texttovoice.virtuai.ui.widget

import android.appwidget.AppWidgetManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.common.components.AppBar
import com.texttovoice.virtuai.common.pin
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Typography
import com.texttovoice.virtuai.ui.theme.Urbanist

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WidgetsScreen(
    navigateToBack: () -> Unit,
) {

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppBar(
            onClickAction = navigateToBack,
            image = R.drawable.arrow_left,
            text = stringResource(R.string.widgets),
            MaterialTheme.colors.surface
        )

        val widgetManager = AppWidgetManager.getInstance(context)
        val widgetProviders = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            widgetManager.getInstalledProvidersForPackage("com.texttovoice.virtuai", null)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colors.onSecondary, RoundedCornerShape(90.dp))
                .padding(horizontal = 10.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(30.dp)
            )

            Text(
                text = stringResource(id = R.string.ask_me_anything),
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Start,
                style = Typography.body1
            )


            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.send),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(27.dp)
            )

        }

        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .bounceClick {
                        widgetProviders[0].pin(context)
                    }
                    ,
                elevation = 0.dp,
                backgroundColor = GreenShadow,
                shape = RoundedCornerShape(90.dp),
            ) {
                Row(
                    Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.add_to_home_screen),
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

        }


//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 10.dp)
//                .padding(horizontal = 16.dp)
//        )
//        {
//
//            items(languageList) {
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp)
//                        .padding(vertical = 15.dp)
//                        .click {
//
//                            navigateToBack()
//                        }
//                ) {
//                    Text(
//                        text = it.name,
//                        color = MaterialTheme.colors.surface,
//                        style = TextStyle(
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.W600,
//                            fontFamily = Urbanist,
//                            lineHeight = 25.sp
//                        ),
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    if (languageViewModel.selectedValue.value == it.code) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.done),
//                            contentDescription = null,
//                            tint = MaterialTheme.colors.primary,
//                            modifier = Modifier
//                                .size(27.dp)
//                        )
//                    }
//
//
//                }
//
//                Divider(
//                    color = MaterialTheme.colors.secondary, thickness = 1.dp,
//                )
//            }
//        }
    }

}