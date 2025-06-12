package com.texttovoice.virtuai.ui.login


import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.bounceClick
import com.texttovoice.virtuai.data.model.User
import com.texttovoice.virtuai.ui.theme.Green
import com.texttovoice.virtuai.ui.theme.GreenShadow
import com.texttovoice.virtuai.ui.theme.Urbanist
import com.yagmurerdogan.toasticlib.Toastic
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.texttovoice.virtuai.R


@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

@Composable
fun LoginScreen(
    navigateToStartChat: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isLoading by remember { mutableStateOf(false) }
    var showErrorToast by remember {
        mutableStateOf(false)
    }


    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            isLoading = false
            user = result.user
            viewModel.saveUser(
                User(
                    "",
                    user?.email ?: "",
                    false,
                    Constants.Preferences.FREE_MESSAGE_COUNT_DEFAULT
                )
            )
            navigateToStartChat()
        },
        onAuthError = {
            isLoading = true
            user = null
            showErrorToast = true

        }
    )

    if (showErrorToast) {
        Toastic
            .toastic(
                context = context,
                message = context.resources.getString(R.string.cannot_login),
                duration = Toastic.LENGTH_LONG,
                type = Toastic.ERROR,
                isIconAnimated = true
            )
            .show()
        showErrorToast = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .width(200.dp)
                .height(170.dp),
            tint = Green,
            painter = painterResource(id = R.drawable.app_icon),

            contentDescription = stringResource(R.string.app_icon),
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colors.primary,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.W700,
                fontFamily = Urbanist,
                lineHeight = 25.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(GreenShadow, shape = RoundedCornerShape(90.dp))
                .padding(horizontal = 15.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.welcome_description),
            color = MaterialTheme.colors.surface,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                fontFamily = Urbanist,
                lineHeight = 25.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(35.dp))



        Row(
            Modifier
                .fillMaxWidth()
                .bounceClick(onClick = {
                    isLoading = true
                    val gso =
                        GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Constants.WEB_CLIENT_ID)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                })
                .height(85.dp)
                .padding(15.dp)
                .border(
                    1.dp,
                    color = Green,
                    shape = RoundedCornerShape(99.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(35.dp))
            } else {
                Image(
                    modifier = Modifier
                        .size(35.dp),
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = stringResource(R.string.app_icon),
                )
            }


            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = stringResource(R.string.login_with_google),
                color = MaterialTheme.colors.surface,
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


