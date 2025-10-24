package com.cd.uielementmanager.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt


fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

fun sendTouchToUnderlyingView(context: Context, position: Offset) {
    val activity = context as? Activity ?: return
    val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
    val screenX = position.x.roundToInt()
    val screenY = position.y.roundToInt()

    val downTime = SystemClock.uptimeMillis()
    val eventTime = SystemClock.uptimeMillis()
    val motionEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        MotionEvent.ACTION_DOWN,
        screenX.toFloat(),
        screenY.toFloat(),
        0
    )

    // Dispatch to the root view
    rootView.dispatchTouchEvent(motionEvent)

    motionEvent.action = MotionEvent.ACTION_UP
    rootView.dispatchTouchEvent(motionEvent)
    motionEvent.recycle()
}

fun sendTouchToUnderlyingView(
    context: Context,
    offset: Offset,
    highlightX: Dp,
    highlightY: Dp
) {
    val activity = context.findActivity() ?: return
    val rootView: View = activity.window?.decorView?.rootView ?: return

    val density = context.resources.displayMetrics.density
    val xPx = highlightX.value * density + offset.x
    val yPx = highlightY.value * density + offset.y

    val downEvent = MotionEvent.obtain(
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        MotionEvent.ACTION_DOWN,
        xPx,
        yPx,
        0
    )
    val upEvent = MotionEvent.obtain(
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        MotionEvent.ACTION_UP,
        xPx,
        yPx,
        0
    )

    rootView.dispatchTouchEvent(downEvent)
    rootView.dispatchTouchEvent(upEvent)

    downEvent.recycle()
    upEvent.recycle()
}


fun forwardMotionEventToUnderlyingViews(context: Context, sourceEvent: MotionEvent): Boolean {
    val activity = context.findActivity() ?: return false
    val rootView =
        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content) ?: return false

    // Find root view's location on screen so we convert coordinates correctly
    val rootLoc = IntArray(2)
    rootView.getLocationOnScreen(rootLoc)
    val rootLeft = rootLoc[0]
    val rootTop = rootLoc[1]

    // Convert input (sourceEvent.rawX/rawY are screen coords in many cases)
    // We'll use rawX/rawY if available, otherwise use x/y + location of source view if you have it.
    val screenX = sourceEvent.rawX
    val screenY = sourceEvent.rawY

    // Coordinates relative to rootView (what dispatchTouchEvent expects)
    val relativeX = screenX - rootLeft
    val relativeY = screenY - rootTop

    // Create a new MotionEvent with same downTime so it's a valid gesture sequence for the target
    val downTime = sourceEvent.downTime.takeIf { it > 0L } ?: SystemClock.uptimeMillis()
    val eventTime = SystemClock.uptimeMillis()
    val action = sourceEvent.actionMasked

    val newEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        action,
        relativeX,
        relativeY,
        sourceEvent.metaState
    ).apply {
        // Copy pointer properties if multi-touch needed; for basic taps this suffices.
        // Also copy tool type / source if needed:
        source = sourceEvent.source
        // setLocation not needed because we passed coordinates above
    }

    // Option A: dispatch to the root view (simpler)
    val dispatched = rootView.dispatchTouchEvent(newEvent)

    // Option B (preferred if you want exact target): find the child view under the coordinates and dispatch to it
    // val target = findViewAt(rootView, relativeX.toInt(), relativeY.toInt())
    // val dispatched = target?.dispatchTouchEvent(newEvent) ?: rootView.dispatchTouchEvent(newEvent)

    newEvent.recycle()
    return dispatched
}

