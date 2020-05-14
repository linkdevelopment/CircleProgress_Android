package com.linkdev.circleprogress

import android.content.res.Resources

object Utilities {
    fun dpToPx(resources: Resources, dp: Float): Float {
        val scale: Float = resources.getDisplayMetrics().density
        return dp * scale + 0.5f
    }
}