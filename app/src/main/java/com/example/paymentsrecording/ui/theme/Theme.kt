package com.example.paymentsrecording.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** themeMode: 0=跟随系统, 1=浅色, 2=深色 */
@Composable
fun PaymentsTheme(
    themeMode: Int = 0,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (dark) {
        darkColorScheme(
            primary = PrimaryDark,
            onPrimary = OnPrimaryDark,
            primaryContainer = PrimaryContainerDark,
            onPrimaryContainer = OnPrimaryContainerDark,
            secondary = SecondaryDark,
            onSecondary = OnSecondaryDark,
            secondaryContainer = SecondaryContainerDark,
            onSecondaryContainer = OnSecondaryContainerDark,
            tertiary = TertiaryDark,
            onTertiary = OnTertiaryDark,
            tertiaryContainer = TertiaryContainerDark,
            onTertiaryContainer = OnTertiaryContainerDark,
            error = ErrorDark,
            onError = OnErrorDark,
            errorContainer = ErrorContainerDark,
            onErrorContainer = OnErrorContainerDark,
            background = BackgroundDark,
            onBackground = OnBackgroundDark,
            surface = SurfaceDark,
            onSurface = OnSurfaceDark,
            surfaceVariant = SurfaceVariantDark,
            onSurfaceVariant = OnSurfaceVariantDark,
            outline = OutlineDark
        )
    } else {
        lightColorScheme(
            primary = Primary,
            onPrimary = OnPrimary,
            primaryContainer = PrimaryContainer,
            onPrimaryContainer = OnPrimaryContainer,
            secondary = Secondary,
            onSecondary = OnSecondary,
            secondaryContainer = SecondaryContainer,
            onSecondaryContainer = OnSecondaryContainer,
            tertiary = Tertiary,
            onTertiary = OnTertiary,
            tertiaryContainer = TertiaryContainer,
            onTertiaryContainer = OnTertiaryContainer,
            error = Error,
            onError = OnError,
            errorContainer = ErrorContainer,
            onErrorContainer = OnErrorContainer,
            background = Background,
            onBackground = OnBackground,
            surface = Surface,
            onSurface = OnSurface,
            surfaceVariant = SurfaceVariant,
            onSurfaceVariant = OnSurfaceVariant,
            outline = Outline
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
