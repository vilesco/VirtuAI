package com.texttovoice.virtuai.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.ui.theme.White
import com.texttovoice.virtuai.R

@Composable
fun UpgradeInfoBottomSheet(
    onConfirmClick: () -> Unit = {}
) {
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
        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = stringResource(R.string.upgrade_info_description),
            color = MaterialTheme.colors.surface,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = Urbanist,
                lineHeight = 25.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Row(modifier = Modifier.padding(vertical = 20.dp)) {

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .bounceClick {
                        onConfirmClick()
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
                        text = stringResource(R.string.upgrade_to_pro),
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