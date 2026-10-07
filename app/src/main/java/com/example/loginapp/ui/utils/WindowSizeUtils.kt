package com.example.loginapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class WindowSizeClass {
    COMPACT,
    MEDIUM_EXPANDED
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp < 600) {
        WindowSizeClass.COMPACT
    } else {
        WindowSizeClass.MEDIUM_EXPANDED
    }
}