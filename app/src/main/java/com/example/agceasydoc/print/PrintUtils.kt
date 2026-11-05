package com.example.agceasydoc.print

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.graphics.createBitmap

fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

fun renderComposableToBitmap(
    activity: ComponentActivity,
    width: Int,
    height: Int,
    content: @Composable () -> Unit
): Bitmap {

    val rootView = activity.window.decorView as ViewGroup

    val composeView = ComposeView(activity).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnDetachedFromWindow
        )
        setContent(content)

        layoutParams = ViewGroup.LayoutParams(width, height)
    }

    rootView.addView(composeView)

    composeView.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )
    composeView.layout(0, 0, width, height)

    val bitmap = createBitmap(width, height)
    val canvas = android.graphics.Canvas(bitmap)
    composeView.draw(canvas)

    rootView.removeView(composeView)

    return bitmap
}
