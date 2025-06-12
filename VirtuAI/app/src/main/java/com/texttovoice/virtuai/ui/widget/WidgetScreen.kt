package com.texttovoice.virtuai.ui.widget


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import com.texttovoice.virtuai.ui.activity.MainActivity
import com.texttovoice.virtuai.R


object WidgetScreen : GlanceAppWidget() {


    @Composable
    @GlanceComposable
    override fun Content() {
        val context = LocalContext.current

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(50.dp)
                .cornerRadius(90.dp)
                .background(Color.White)
                .padding(horizontal = 10.dp)
                .clickable(actionStartActivity(MainActivity::class.java)),
            verticalAlignment = androidx.glance.layout.Alignment.Vertical.CenterVertically,
            horizontalAlignment = androidx.glance.layout.Alignment.Horizontal.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(
                    resId = R.drawable.app_icon
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .defaultWeight()
                    .size(30.dp)
            )
            Text(
                text = context.getString(R.string.ask_me_anything),
                modifier = GlanceModifier.padding(start = 10.dp)
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Image(
                provider = ImageProvider(
                    resId = R.drawable.send
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(27.dp)
            )

        }
    }



}

class ChatAiWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_PICK) {
            // Create and configure the widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    ChatAiWidgetProvider::class.java
                )
            )[0]
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)

            // Configure the widget's UI and functionality using RemoteViews

            // Add the widget to the home screen
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        super.onReceive(context, intent)
    }
}

class ChatAiWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = WidgetScreen
}