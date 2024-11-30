package com.example.mtgachievementleaderboardtracking.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Black, // Ensuring dark background
    surface = Color(0xFF121212), // Dark surface color
    onBackground = Color.White, // Text color on dark background
    onSurface = Color.White // Text color on dark surface
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White, // Light background for light mode
    surface = Color(0xFFF5F5F5), // Light surface color
    onBackground = Color.Black, // Text color on light background
    onSurface = Color.Black // Text color on light surface
)

@Composable
fun MTGAchievementLeaderboardTrackingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Choose dynamic color scheme for Android 12+ or use static dark/light theme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply MaterialTheme with selected colorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Add your typography settings here
        content = content
    )
}