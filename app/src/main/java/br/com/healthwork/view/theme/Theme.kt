package br.com.healthwork.view.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val CoresDoApp = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Branco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulEscuro,

    secondary = VerdeSaude,
    onSecondary = Branco,
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = VerdeEscuro,

    background = FundoApp,
    onBackground = TextoPrincipal,

    surface = Branco,
    onSurface = TextoPrincipal,
    surfaceVariant = CinzaAzulado,
    onSurfaceVariant = TextoSecundario,

    surfaceContainerLowest = Branco,
    surfaceContainerLow = Branco,
    surfaceContainer = Branco,
    surfaceContainerHigh = Branco,
    surfaceContainerHighest = Branco,

    outline = Contorno,
    outlineVariant = ContornoSuave
)

private val FormasDoApp = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(14.dp)
)

@Composable
fun HealthWorkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CoresDoApp,
        typography = Tipografia,
        shapes = FormasDoApp,
        content = content
    )
}
