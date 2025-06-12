package com.texttovoice.virtuai.common

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.fragment.app.Fragment
import com.texttovoice.virtuai.ui.widget.ChatAiWidgetProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


enum class ButtonState { Pressed, Idle }


val MediaPlayer.seconds: Int
    get() {
        return this.duration / 1000
    }

val MediaPlayer.currentSeconds: Int
    get() {
        return this.currentPosition / 1000
    }

enum class Keyboard {
    Opened, Closed
}

@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}



fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun String.toFormattedDate(): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}


fun Modifier.bounceClick(onClick: () -> Unit = {}) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.90f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .click {
            onClick()
        }
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.click(onClick: () -> Unit = {}) = composed {
    this
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                onClick()
            }
        )

}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(this))
    } else {
        this
    }
}


fun <T> debounce(timeMillis: Long = 300L, action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Default).launch {
            delay(timeMillis)
            withContext(Dispatchers.Main) {
                action(param)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun AppWidgetProviderInfo.pin(context: Context) {
    val successCallback = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, ChatAiWidgetProvider::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    AppWidgetManager.getInstance(context).requestPinAppWidget(provider, null, successCallback)
}

