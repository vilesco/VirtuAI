package com.texttovoice.virtuai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.R

@Composable
fun ChooseMediaTypeBottomSheet(
    onChooseScanTypeClick: () -> Unit = {},
    onLinkClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onProVersionClick: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val isProVersion by viewModel.isProVersion.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProVersion()
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

        Text(
            text = stringResource(R.string.choose_media_source),
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


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .padding(horizontal = 16.dp)
                .click {
                    if (isProVersion.not())
                    {
                        onProVersionClick()
                    }else{
                        onChooseScanTypeClick()
                    }

                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(R.drawable.scan),
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 27.dp, height = 27.dp)

            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.camera),
                color = MaterialTheme.colors.surface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(15.dp))
            if (isProVersion.not())
            {
                Text(
                    text = stringResource(R.string.pro),
                    color = MaterialTheme.colors.primary,
                    style = TextStyle(
                        fontSize = 16.sp,
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
            Icon(
                painter = painterResource(id = R.drawable.right),
                contentDescription = null,
                tint = MaterialTheme.colors.surface,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(30.dp)
            )
        }

        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .padding(horizontal = 16.dp)
                .click {
                    if (isProVersion.not())
                    {
                        onProVersionClick()
                    }else{
                        onLinkClick()
                    }


                },
            verticalAlignment = Alignment.CenterVertically
        ) {




            Icon(
                painter = painterResource(R.drawable.link),
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 27.dp, height = 27.dp)
                    .padding(2.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.link),
                color = MaterialTheme.colors.surface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(15.dp))
            if (isProVersion.not())
            {
                Text(
                    text = stringResource(R.string.pro),
                    color = MaterialTheme.colors.primary,
                    style = TextStyle(
                        fontSize = 16.sp,
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
            Icon(
                painter = painterResource(id = R.drawable.right),
                contentDescription = null,
                tint = MaterialTheme.colors.surface,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(30.dp)
            )
        }


        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(10.dp)
        )

    }
}