package com.texttovoice.virtuai.ui.chat

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.texttovoice.virtuai.common.click
import com.texttovoice.virtuai.R

@Composable
fun ChooseScanImageTypeBottomSheet(
    onCameraClick: () -> Unit = {},
    onGalleryClick: (String) -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val textRecognizer = remember { TextRecognition.getClient() }
    val extractedText = remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    imageUri?.let { imageUri ->
        val resolver = context.contentResolver
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(resolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(resolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }
        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    extractedText.value = it.result?.text ?: ""
                    onGalleryClick(extractedText.value)

                }
            }
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

                    onCameraClick()

                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(R.drawable.camera),
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 27.dp, height = 27.dp)

            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.camera_scan),
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

                    galleryLauncher.launch("image/*")

                },
            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                painter = painterResource(R.drawable.gallery),
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(width = 27.dp, height = 27.dp)
                    .padding(2.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.gallery),
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