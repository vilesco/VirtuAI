package com.texttovoice.virtuai.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.R

@Composable
fun AppBar(
    onClickAction: () -> Unit,
    image: Int,
    text: String,
    tint: Color,
    menuItems: (@Composable () -> Unit)? = null,
    isMainPage: Boolean = false,
    isChatPage : Boolean = false
) {

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center)
    {
        if (!isMainPage) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                color = MaterialTheme.colors.surface,
                style = TextStyle(
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                    fontFamily = Urbanist,
                    textAlign = TextAlign.Center
                )
            )
        }


        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {


            if (image == R.drawable.arrow_left) {


                if (isChatPage) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(image),
                            contentDescription = "image",
                            tint = tint,
                            modifier = Modifier
                                .click { onClickAction() }
                                .width(27.dp)
                                .height(27.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = text,
                            modifier = Modifier,
                            color = MaterialTheme.colors.surface,
                            style = TextStyle(
                                fontWeight = FontWeight.W700,
                                fontSize = 20.sp,
                                fontFamily = Urbanist,
                                textAlign = TextAlign.Center
                            )
                        )


                    }
                } else {

                    Icon(
                        painter = painterResource(image),
                        contentDescription = "image",
                        tint = tint,
                        modifier = Modifier
                            .click { onClickAction() }
                            .width(27.dp)
                            .height(27.dp)
                    )



                }

            } else {
                if (!isMainPage) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = "image",
                        modifier = Modifier
                            .click { onClickAction() }
                            .height(27.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(image),
                            contentDescription = "image",
                            tint = tint,
                            modifier = Modifier
                                .height(27.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))


                        Text(
                            text = text,
                            modifier = Modifier,
                            color = Green,
                            style = TextStyle(
                                fontWeight = FontWeight.W800,
                                fontSize = 20.sp,
                                fontFamily = Urbanist,
                                textAlign = TextAlign.Center
                            )
                        )


                    }

                }


            }




            Spacer(modifier = Modifier.weight(1f))

            if (menuItems != null) {
                menuItems()
            }

        }
    }


}