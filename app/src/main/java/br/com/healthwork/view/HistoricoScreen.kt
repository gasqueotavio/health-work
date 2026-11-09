package br.com.healthwork.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.healthwork.model.RegistroComNome
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.model.TipoRegistro
import br.com.healthwork.utils.Formatos
import br.com.healthwork.view.componentes.FotoMiniatura
import br.com.healthwork.viewmodel.HistoricoViewModel
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel
) {
    val registros = viewModel.registros

    // Carrega os registros sempre que a tela abre.
    LaunchedEffect(Unit) { viewModel.carregar() }

    var idParaAjustar by rememberSaveable { mutableStateOf<Long?>(null) }
    var idParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }
    val paraAjustar = registros.find { it.registro.id == idParaAjustar }?.registro
    val paraExcluir = registros.find { it.registro.id == idParaExcluir }?.registro

    Box(Modifier.fillMaxSize()) {
        if (registros.isEmpty()) {
            Text("Nenhum registro de ponto.", modifier = Modifier.align(Alignment.Center))
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(registros, key = { it.registro.id }) { item ->
                CartaoRegistro(
                    item = item,
                    onAjustar = { idParaAjustar = item.registro.id },
                    onExcluir = { idParaExcluir = item.registro.id }
                )
            }
        }
    }

    paraAjustar?.let { registro ->
        DialogoAjuste(
            registro = registro,
            onSalvar = { data, hora, tipo, justificativa ->
                viewModel.ajustar(registro, data, hora, tipo, justificativa).also { erro ->
                    if (erro == null) idParaAjustar = null
                }
            },
            onFechar = { idParaAjustar = null }
        )
    }

    paraExcluir?.let { registro ->
        AlertDialog(
            onDismissRequest = { idParaExcluir = null },
            title = { Text("Excluir registro") },
            text = {
                Text("Excluir o registro de ${registro.tipo.descricao.lowercase()} de ${Formatos.dataHora(registro.dataHora)}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluir(registro)
                    idParaExcluir = null
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { idParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CartaoRegistro(
    item: RegistroComNome,
    onAjustar: () -> Unit,
    onExcluir: () -> Unit
) {
    val registro = item.registro
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FotoMiniatura(caminho = registro.fotoPath, modifier = Modifier.size(80.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.nomeColaborador, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${registro.tipo.descricao} • ${Formatos.data(registro.dataHora)} às ${Formatos.hora(registro.dataHora)}",
                        fontWeight = FontWeight.Medium
                    )
                    Text(registro.endereco, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (registro.justificativa != null) {
                Column(Modifier.padding(top = 8.dp)) {
                    val original = registro.dataHoraOriginal?.let { Formatos.dataHora(it) }.orEmpty()
                    val tipoOriginal = registro.tipoOriginal?.descricao.orEmpty()
                    Text(
                        "Ajustado — original: $tipoOriginal em $original",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Justificativa: ${registro.justificativa}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(Modifier.align(Alignment.End)) {
                TextButton(onClick = onAjustar) { Text("Corrigir") }
                TextButton(onClick = onExcluir) { Text("Excluir") }
            }
        }
    }
}

@Composable
private fun DialogoAjuste(
    registro: RegistroPonto,
    onSalvar: (data: String, hora: String, tipo: TipoRegistro, justificativa: String) -> String?,
    onFechar: () -> Unit
) {
    var data by rememberSaveable { mutableStateOf(Formatos.data(registro.dataHora)) }
    var hora by rememberSaveable { mutableStateOf(Formatos.hora(registro.dataHora)) }
    var tipo by rememberSaveable { mutableStateOf(registro.tipo) }
    var justificativa by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Corrigir registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it; erro = null },
                    label = { Text("Data (dd/mm/aaaa)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it; erro = null },
                    label = { Text("Hora (hh:mm)") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoRegistro.entries.forEach { opcao ->
                        FilterChip(
                            selected = tipo == opcao,
                            onClick = { tipo = opcao },
                            label = { Text(opcao.descricao) }
                        )
                    }
                }
                OutlinedTextField(
                    value = justificativa,
                    onValueChange = { justificativa = it; erro = null },
                    label = { Text("Justificativa") },
                    minLines = 2
                )
                erro?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { erro = onSalvar(data, hora, tipo, justificativa) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}
