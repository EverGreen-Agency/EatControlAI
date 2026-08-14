package com.eatcontrolai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val EatControlColorScheme = darkColorScheme(
    primary = EcColors.Mint,
    onPrimary = EcColors.OnMint,
    secondary = EcColors.Blue,
    onSecondary = EcColors.TextPrimary,
    tertiary = EcColors.Purple,
    background = EcColors.Background,
    onBackground = EcColors.TextPrimary,
    surface = EcColors.Surface,
    onSurface = EcColors.TextPrimary,
    surfaceVariant = EcColors.SurfaceRaised,
    onSurfaceVariant = EcColors.TextSoft,
    outline = EcColors.TextMuted,
    outlineVariant = EcColors.Line,
    error = EcColors.Red,
    onError = EcColors.OnMint
)

/**
 * O app é escuro em qualquer configuração do sistema — [isSystemInDarkTheme] é ignorado de
 * propósito para que a demo tenha aparência previsível em qualquer aparelho, inclusive no celular
 * emprestado pela organização no dia do hackathon.
 */
@Composable
fun EatControlAITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EatControlColorScheme,
        typography = EatControlTypography
    ) {
        CompositionLocalProvider(
            LocalContentColor provides EcColors.TextPrimary,
            content = content
        )
    }
}
