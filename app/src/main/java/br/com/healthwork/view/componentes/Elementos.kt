package br.com.healthwork.view.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.healthwork.model.TipoRegistro

/** Círculo com as iniciais do colaborador. */
@Composable
fun Avatar(nome: String, modifier: Modifier = Modifier) {
    val iniciais = nome.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = modifier
            .size(44.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciais,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/** Etiqueta colorida: Entrada em verde, Saída em azul. */
@Composable
fun BadgeTipo(tipo: TipoRegistro, modifier: Modifier = Modifier) {
    val entrada = tipo == TipoRegistro.ENTRADA
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (entrada) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (entrada) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = tipo.descricao,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/** Linha com ícone pequeno e texto (data, endereço etc.). */
@Composable
fun InfoLinha(
    icone: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    maxLinhas: Int = 2
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLinhas,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Mensagem centralizada para quando a lista está vazia. */
@Composable
fun EstadoVazio(
    icone: ImageVector,
    titulo: String,
    texto: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
