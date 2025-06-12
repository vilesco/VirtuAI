package com.texttovoice.virtuai.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import com.texttovoice.virtuai.R

@Composable
fun SplashScreen(
    navigateToStartChat: () -> Unit,
    navigateToLoginChat: () -> Unit
) {

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000
        ), label = ""
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)

//        Firebase.auth.currentUser?.let {
//            navigateToStartChat()
//            return@LaunchedEffect
//        }
//        navigateToLoginChat()

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            try {
                currentUser.reload().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navigateToStartChat()
                    } else {
                        Firebase.auth.signOut()
                        navigateToLoginChat()
                    }
                }
            } catch (e: Exception) {
                Firebase.auth.signOut()
                navigateToLoginChat()
            }
        } else {
            navigateToLoginChat()
        }
    }

    SplashDesign(alpha = alphaAnimation.value)
}

@Composable
fun SplashDesign(alpha: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .alpha(alpha = alpha),
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = stringResource(R.string.app_icon),
        )
    }
}

