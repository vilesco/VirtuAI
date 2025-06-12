package com.texttovoice.virtuai.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Constants.TRANSITION_ANIMATION_DURATION
import com.texttovoice.virtuai.ui.image.GenerateImageScreen
import com.texttovoice.virtuai.ui.camera.MLKitTextRecognition
import com.texttovoice.virtuai.ui.chat.ChatScreen
import com.texttovoice.virtuai.ui.chat.ChooseGPTBottomSheet
import com.texttovoice.virtuai.ui.chat.ChooseMediaTypeBottomSheet
import com.texttovoice.virtuai.ui.chat.ChooseScanImageTypeBottomSheet
import com.texttovoice.virtuai.ui.chat.LinkSummarizeBottomSheet
import com.texttovoice.virtuai.ui.history.DeleteHistoryBottomSheet
import com.texttovoice.virtuai.ui.history.HistoryScreen
import com.texttovoice.virtuai.ui.language.LanguageScreen
import com.texttovoice.virtuai.ui.settings.LogoutBottomSheet
import com.texttovoice.virtuai.ui.settings.SettingsScreen
import com.texttovoice.virtuai.ui.splash.SplashScreen
import com.texttovoice.virtuai.ui.startchat.StartChatScreen
import com.texttovoice.virtuai.ui.upgrade.PurchaseHelper
import com.texttovoice.virtuai.ui.chat.UpgradeInfoBottomSheet
import com.texttovoice.virtuai.ui.image.ShowImageBottomSheet
import com.texttovoice.virtuai.ui.login.LoginScreen
import com.texttovoice.virtuai.ui.speech.TextToSpeechScreen
import com.texttovoice.virtuai.ui.upgrade.UpgradeScreen
import com.texttovoice.virtuai.ui.widget.WidgetsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterialNavigationApi::class)
@ExperimentalAnimationApi
@Composable
fun NavGraph(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    canCancelState: MutableState<Boolean>,
    darkMode: MutableState<Boolean>,
    purchaseHelper: PurchaseHelper
) {

    val paddingBottom =
        animateDpAsState(
            if (bottomBarState.value) 56.dp else 0.dp,
            animationSpec = tween(TRANSITION_ANIMATION_DURATION), label = ""
        )

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(bottom = paddingBottom.value)

    ) {

        bottomSheet(route = "${Screen.ChooseGPT.route}?model={model}") {
            ChooseGPTBottomSheet(
                model = it.arguments?.getString("model") ?: "",
                onCancelClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.GPT, it)
                    navController.popBackStack()
                },
                onProVersionClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.GPT, it)
                    navController.popBackStack()
                    navController.navigate(Screen.Upgrade.route)
                })
        }


        bottomSheet(route = Screen.ChooseScanImageType.route) {
            ChooseScanImageTypeBottomSheet(onCancelClick = {
                navController.popBackStack()
            }, onCameraClick = {
                navController.popBackStack()
                navController.navigate(Screen.Camera.route)
            }, onGalleryClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.OCR, it)
                navController.popBackStack()
            })
        }


        bottomSheet(route = Screen.ChooseMedia.route) {
            ChooseMediaTypeBottomSheet(onChooseScanTypeClick = {
                navController.popBackStack()
                navController.navigate(Screen.ChooseScanImageType.route)
            }, onLinkClick = {
                navController.popBackStack()
                navController.navigate(Screen.LinkSummarize.route)
            }, onCancelClick = {
                navController.popBackStack()
            }, onProVersionClick = {
                navController.popBackStack()
                navController.navigate(Screen.Upgrade.route)
            })
        }

        bottomSheet(route = Screen.LinkSummarize.route) {
            LinkSummarizeBottomSheet(onConfirmClick = { link, content ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.LINK_SUMMARIZE, link)


                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.LINK_SUMMARIZE_CONTENT, content)

                navController.popBackStack()
            })
        }

        bottomSheet(route = Screen.UpgradeInfoDialog.route) {
            UpgradeInfoBottomSheet(onConfirmClick = {
                navController.popBackStack()
                navController.navigate(Screen.Upgrade.route)
            })
        }

        bottomSheet(route = Screen.DeleteHistory.route) {
            DeleteHistoryBottomSheet(onConfirmClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Constants.IS_DELETE, true)
                navController.popBackStack()
            }, onCancelClick = {
                navController.popBackStack()
            })
        }
        bottomSheet(route = "${Screen.ShowImage.route}?imageData={imageData}") {
            val data = it.arguments?.getString("imageData") ?: ""



            ShowImageBottomSheet(
                imageData = data,
                canCancelState = canCancelState,
                onConfirmClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.IS_DELETE, true)
                    navController.popBackStack()
                }, onCancelClick = {
                    navController.popBackStack()
                })
        }


        bottomSheet(route = Screen.Logout.route) {
            LogoutBottomSheet(onConfirmClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }, onCancelClick = {
                navController.popBackStack()
            })
        }



        composable(
            route = Screen.Splash.route
        ) {
            SplashScreen(
                navigateToStartChat = {
                    navController.navigate(Screen.StartChat.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToLoginChat = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Login.route
        ) {

            LoginScreen(
                navigateToStartChat = {
                    navController.navigate(Screen.StartChat.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.StartChat.route,
            enterTransition = {
                when (initialState.destination.route) {
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }
        ) {
            StartChatScreen(
                navigateToChat = { name, role, examples, messageDefault ->
                    val examplesString = examples?.joinToString(separator = "|")
                    navController.navigate("${Screen.Chat.route}?name=$name&role=$role&examples=$examplesString&messageDefault=$messageDefault")
                },
                navigateToWidgets = {
                    navController.navigate(Screen.Widgets.route)
                },
                navigateToUpgrade = {
                    navController.navigate(Screen.Upgrade.route)
                },
                purchaseHelper = purchaseHelper,
                bottomBarState = bottomBarState,
            )
        }

        composable(route = "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}",
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.StartChat.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.History.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.StartChat.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.History.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.StartChat.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.History.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Screen.StartChat.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.History.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {

            var exampleList: List<String> = emptyList()

            if (it.arguments?.getString("examples") != "null") {
                exampleList =
                    it.arguments?.getString("examples")?.split("|")?.toTypedArray()?.toList()
                        ?: emptyList()
            }
            val screenResult = navController.currentBackStackEntry
                ?.savedStateHandle
            val inoutDefaultMessage =
                rememberSaveable { mutableStateOf(it.arguments?.getString("messageDefault") ?: "") }
            ChatScreen(
                navigateToBack = {
                    navController.popBackStack()
                },
                navigateToUpgrade = {
                    navController.navigate(Screen.Upgrade.route)
                },
                it.arguments?.getString("name"),
                exampleList,
                inoutDefaultMessage,
                savedStateHandle = screenResult,
                navigateToChooseMedia = {
                    navController.navigate(Screen.ChooseMedia.route)
                },
                navigateToUpgradeInfo = {
                    navController.navigate(Screen.UpgradeInfoDialog.route)
                },
                navigateToScan = {
                    navController.navigate(Screen.ChooseScanImageType.route)
                },
                navigateToSummarize = {
                    navController.navigate(Screen.LinkSummarize.route)
                },
            ) { model ->
                navController.navigate("${Screen.ChooseGPT.route}?model=$model")
            }
        }

        composable(
            route = Screen.GenerateImage.route
        ) {
            GenerateImageScreen(navigateToShowImage = {
                val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                navController.navigate("${Screen.ShowImage.route}?imageData=$encodedUrl")
            }, bottomBarState = bottomBarState, navigateToUpgradeInfo = {
                navController.navigate(Screen.UpgradeInfoDialog.route)
            }, navigateToUpgrade = {
                navController.navigate(Screen.Upgrade.route)
            })
        }

        composable(
            route = Screen.TextToSpeech.route
        ) {
            TextToSpeechScreen(navigateToShowImage = {
                val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                navController.navigate("${Screen.ShowImage.route}?imageData=$encodedUrl")
            }, bottomBarState = bottomBarState, navigateToUpgradeInfo = {
                navController.navigate(Screen.UpgradeInfoDialog.route)
            }, navigateToUpgrade = {
                navController.navigate(Screen.Upgrade.route)
            })
        }


        composable(route = Screen.History.route) {

            val screenResult = navController.currentBackStackEntry
                ?.savedStateHandle

            HistoryScreen(
                navigateToChat = { name, role, examples, id ->
                    val examplesString = examples?.joinToString(separator = "|")
                    navController.navigate("${Screen.Chat.route}?name=$name&role=$role&examples=$examplesString&id=$id")
                },
                navigateToDeleteConversations = {
                    navController.navigate(Screen.DeleteHistory.route)
                },
                savedStateHandle = screenResult
            )
        }


        composable(route = Screen.Settings.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Languages.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Languages.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.Languages.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {
            SettingsScreen(
                darkMode = darkMode,
                navigateToLanguages = {
                    navController.navigate(Screen.Languages.route)
                },
                navigateToUpgrade = {
                    navController.navigate(Screen.Upgrade.route)
                },
                navigateToLogout = {
                    navController.navigate(Screen.Logout.route)
                },
                purchaseHelper = purchaseHelper
            )
        }

        composable(route = Screen.Widgets.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.StartChat.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.StartChat.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Screen.StartChat.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {

            WidgetsScreen(
                navigateToBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Languages.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Settings.route ->
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Settings.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Screen.Settings.route ->
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {

            LanguageScreen(
                navigateToBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Upgrade.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Settings.route ->
                        slideInVertically(
                            initialOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.StartChat.route ->
                        slideInVertically(
                            initialOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideInVertically(
                            initialOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideInVertically(
                            initialOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    Screen.Settings.route ->
                        slideOutVertically(
                            targetOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.StartChat.route ->
                        slideOutVertically(
                            targetOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    Screen.GenerateImage.route ->
                        slideOutVertically(
                            targetOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )


                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideOutVertically(
                            targetOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {

            UpgradeScreen(
                purchaseHelper,
                navigateToBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Camera.route,
            enterTransition = {
                when (initialState.destination.route) {
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideInVertically(
                            initialOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "${Screen.Chat.route}?name={name}&role={role}&examples={examples}&id={id}&messageDefault={messageDefault}" ->
                        slideOutVertically(
                            targetOffsetY = { fullWidth -> fullWidth },
                            animationSpec = tween(TRANSITION_ANIMATION_DURATION)
                        )

                    else -> null
                }
            }) {

            MLKitTextRecognition(onNavigateBack = {
                navController.popBackStack()
            },
                onShot = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.OCR, it)
                    navController.popBackStack()
                })
        }
    }

}
