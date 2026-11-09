package br.com.healthwork.view.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/** Paleta do app: azul como cor principal e verde de destaque. Só tema claro. */
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

    // Cards, menus, diálogos e barra de navegação ficam brancos sobre o fundo azulado.
    surfaceContainerLowest = Branco,
    surfaceContainerLow = Branco,
    surfaceContainer = Branco,
    surfaceContainerHigh = Branco,
    surfaceContainerHighest = Branco,

    outline = Contorno,
    outlineVariant = ContornoSuave
)

/** Cantos um pouco menos arredondados que o padrão (diálogos, menus, botão flutuante, chips). */
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
