package com.nexters.fooddiary.presentation.widget

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun FoodDiaryWidgetContent() {
    val context = LocalContext.current
    
    GlanceTheme {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(WidgetConstants.WIDGET_PADDING)
                .background(GlanceTheme.colors.background)
                .cornerRadius(WidgetConstants.WIDGET_CORNER_RADIUS)
                .clickable(
                    actionStartActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(WidgetConstants.DEEP_LINK_CAMERA)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.widget_take_photo),
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
    }
}

