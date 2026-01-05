package com.example.creativecommissionstracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// visual theme the app should use
enum class ArtThemeType {
    CONTEMPORARY,
    CUSTOM,
    PORTRAIT
}

// Contemporary -> minimal black & white
private val ContemporaryColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF424242),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
)

// Custom Art -> fun, punchy, colourful
private val CustomColorScheme = lightColorScheme(
    primary = Color(0xFFFF4081),      // reddish-pink
    onPrimary = Color(0xFFFFFBA9),
    secondary = Color(0xFF00BCD4),    // teal-ish
    onSecondary = Color(0xFF000000),
    background = Color(0xFFB6FFFF),   // light blue-green
    onBackground = Color(0xFF000000),
    surface = Color(0xFFC5FFFF),     // light cyan
    onSurface = Color(0xFF212121)
)

// Portraits -> warm, renaissance-like
private val PortraitColorScheme = lightColorScheme(
    primary = Color(0xFF795548),      // brown
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFBCAAA4),    // light brown
    onSecondary = Color(0xFF000000),
    background = Color(0xFFFFF8E1),   // creamy
    onBackground = Color(0xFF3E2723),
    surface = Color(0xFFFFF3E0),
    onSurface = Color(0xFF3E2723)
)

@Composable
fun ArtAppTheme(
    artThemeType: ArtThemeType = ArtThemeType.CONTEMPORARY,
    content: @Composable () -> Unit
) {
    val colorScheme = when (artThemeType) {
        ArtThemeType.CONTEMPORARY -> ContemporaryColorScheme
        ArtThemeType.CUSTOM -> CustomColorScheme
        ArtThemeType.PORTRAIT -> PortraitColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}