package com.texttovoice.virtuai.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.texttovoice.virtuai.R
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.Urbanist

@Composable
fun ImageStyleItem(
    text: String,
    image: Int,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .bounceClick(onClick = {
                onClick()
            })
            .background(
                shape = RoundedCornerShape(16.dp),
                color = if (selected) Green else MaterialTheme.colors.onSecondary
            )
            .border(
                2.dp,
                color = if (selected) Green else MaterialTheme.colors.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (image == R.drawable.none) {
            Icon(
                painter = painterResource(image),
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 120.dp, height = 120.dp)
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
            )
        } else {
            Image(
                painter = painterResource(image),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 120.dp, height = 120.dp)
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )


            )
        }

        Text(
            text = text,
            color = MaterialTheme.colors.surface,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = Urbanist,
                lineHeight = 25.sp
            ),
            modifier = Modifier.padding(5.dp),

            )
    }


}
