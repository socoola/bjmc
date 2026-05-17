package com.bjmc.classpet.util

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bjmc.classpet.ui.presentation.ClassroomPresentation

class CastingManager(context: Context) {
    private val displayManager =
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var presentation: ClassroomPresentation? = null

    val displays: Array<Display>
        get() = displayManager.displays

    fun getSecondaryDisplay(): Display? {
        return displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }
    }

    fun isCasting(): Boolean = presentation?.isShowing == true

    fun startCasting(context: Context) {
        val display = getSecondaryDisplay() ?: return
        presentation?.dismiss()
        presentation = ClassroomPresentation(context, display)
        presentation?.show()
    }

    fun stopCasting() {
        presentation?.dismiss()
        presentation = null
    }

    fun dismiss() {
        presentation?.dismiss()
        presentation = null
    }
}

@Composable
fun rememberCastingManager(): CastingManager {
    val context = LocalContext.current
    return remember { CastingManager(context) }
}
