package com.texttovoice.virtuai.ui.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.texttovoice.virtuai.common.loadInterstitial
import com.texttovoice.virtuai.common.loadRewarded
import com.texttovoice.virtuai.common.removeInterstitial
import com.texttovoice.virtuai.navigation.BottomNavigationBar
import com.texttovoice.virtuai.navigation.NavGraph
import com.texttovoice.virtuai.navigation.Screen
import com.texttovoice.virtuai.ui.theme.ConversAITheme
import com.texttovoice.virtuai.ui.upgrade.PurchaseHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val CHANNEL_ID = "ConversAI"
    val CHANNEL_NAME = "ConversAI"
    val NOTIF_ID = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        loadRewarded(this)
        loadInterstitial(this)
        val purchaseHelper = PurchaseHelper(this)
        purchaseHelper.billingSetup()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnSuccessListener {
        }
        askNotificationPermission()
        createNotificationChannel()

        setContent {

//            var messages = viewModel.chatCompletionRequest.collectAsState()
//            messages.value.messages.forEach { message ->
//
//                println(message)
//
//            }
//            viewModel.getCurrentLanguageCode()
//            val currentLanguageCode  =  viewModel.currentLanguageCode.value
//
//            val locale = Locale(currentLanguageCode)
//            val config = Configuration()
//            config.setLocale(locale)
//            val resources = this@MainActivity.applicationContext.resources
//            resources?.updateConfiguration(config, resources.displayMetrics)

            val darkThemeCurrent by viewModel.darkMode.collectAsState()
            val darkTheme = remember { mutableStateOf(darkThemeCurrent) }

            val onDismissBottomSheet = remember { mutableStateOf(false) }


            ConversAITheme(darkTheme = darkTheme.value) {

                val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
                val canCancelState = rememberSaveable { (mutableStateOf(true)) }

//                val sheetState = rememberModalBottomSheetState(
//                    initialValue = ModalBottomSheetValue.Hidden,
//                    skipHalfExpanded = true
//                )
//
//                val bottomSheetNavigator = remember(sheetState) {
//                    BottomSheetNavigator(sheetState = sheetState)
//                }


                val bottomSheetNavigator = rememberBottomSheetNavigator(
                    skipHalfExpanded = true,
                    canCancelState = canCancelState
                )
                val navController = rememberAnimatedNavController(bottomSheetNavigator)

                val navBackStackEntry by navController.currentBackStackEntryAsState()

                when (navBackStackEntry?.destination?.route) {
                    Screen.Upgrade.route -> bottomBarState.value = false
                    Screen.Splash.route -> bottomBarState.value = false
                    Screen.ChooseMedia.route -> bottomBarState.value = false
                    Screen.Camera.route -> bottomBarState.value = false
                    Screen.UpgradeInfoDialog.route -> bottomBarState.value = false
                    Screen.LinkSummarize.route -> bottomBarState.value = false
                    Screen.ChooseScanImageType.route -> bottomBarState.value = false
                    "${Screen.ChooseGPT.route}?model={model}" -> bottomBarState.value = false
                    Screen.Login.route -> bottomBarState.value = false
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" -> bottomBarState.value =
                        false

                    null -> bottomBarState.value = false
                    else -> bottomBarState.value = true
                }

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    val params = Bundle()
                    params.putString("screen_name", destination.route)
                    params.putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route)
                    params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, destination.route)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
                }

                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)


                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(
                        topStart = 35.dp,
                        topEnd = 35.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .captionBarPadding()
                            .imePadding()
                            .statusBarsPadding(),
                    )
                    {
                        NavGraph(
                            navController = navController,
                            bottomBarState,
                            canCancelState,
                            darkMode = darkTheme,
                            purchaseHelper
                        )
                        Column(
                            Modifier
                                .fillMaxHeight(),
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            BottomNavigationBar(navController, bottomBarState)
                        }
                    }

                }
            }

        }
    }

    override fun onDestroy() {
        removeInterstitial()
        super.onDestroy()
    }

}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}


@ExperimentalMaterialNavigationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = false,
    canCancelState: MutableState<Boolean>,
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = animationSpec,
        confirmValueChange = { canCancelState.value },
        skipHalfExpanded = skipHalfExpanded
    )
    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}