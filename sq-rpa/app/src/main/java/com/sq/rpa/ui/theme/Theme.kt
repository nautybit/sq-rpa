package com.sq.rpa.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Teal500,
    onSecondary = White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Teal900,
    tertiary = Amber500,
    onTertiary = White,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Amber900,
    error = Red500,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red900,
    background = Grey50,
    onBackground = Grey900,
    surface = White,
    onSurface = Grey900,
    surfaceVariant = Grey100,
    onSurfaceVariant = Grey700,
    outline = Grey400
)

private val DarkColorScheme = darkColorScheme(
    primary = Green300,
    onPrimary = Green900,
    primaryContainer = Green700,
    onPrimaryContainer = Green100,
    secondary = Teal300,
    onSecondary = Teal900,
    secondaryContainer = Teal700,
    onSecondaryContainer = Teal100,
    tertiary = Amber300,
    onTertiary = Amber900,
    tertiaryContainer = Amber700,
    onTertiaryContainer = Amber100,
    error = Red300,
    onError = Red900,
    errorContainer = Red700,
    onErrorContainer = Red100,
    background = Grey900,
    onBackground = Grey100,
    surface = Grey800,
    onSurface = Grey100,
    surfaceVariant = Grey800,
    onSurfaceVariant = Grey300,
    outline = Grey600
)

@Composable
fun SQRPATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 