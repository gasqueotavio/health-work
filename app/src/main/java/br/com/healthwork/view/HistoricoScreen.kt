package br.com.healthwork.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import br.com.healthwork.model.RegistroComNome
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.model.TipoRegistro
import br.com.healthwork.utils.Formatos
import br.com.healthwork.view.componentes.BadgeTipo
import br.com.healthwork.view.componentes.EstadoVazio
import br.com.healthwork.view.componentes.FotoMiniatura
import br.com.healthwork.view.componentes.InfoLinha
import br.com.healthwork.viewmodel.HistoricoViewModel

@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel
) {
    val registros = viewModel.registros

    LaunchedEffect(Unit) { viewModel.carregar() }

    var idParaAjustar by rememberSaveable { mutableStateOf<Long?>(null) }
    var idParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }
    val paraAjustar = registros.find { it.registro.id == idParaAjustar }?.registro
    val paraExcluir = registros.find { it.registro.id == idParaExcluir }?.registro

    Box(Modifier.fillMaxSize()) {
        if (registros.isEmpty()) {
            EstadoVazio(
                icone = Icons.AutoMirrored.Filled.List,
                titulo = "Nenhum registro de ponto",
                texto = "Os pontos batidos aparecem aqui.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Excluir registro") },
            text = {
                Text("Excluir o registro de ${registro.tipo.descricao.lowercase()} de ${Formatos.dataHora(registro.dataHora)}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.excluir(registro)
                        idParaExcluir = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Excluir") }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FotoMiniatura(
                    caminho = registro.fotoPath,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.nomeColaborador,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        BadgeTipo(registro.tipo)
                    }
                    InfoLinha(
                        Icons.Default.DateRange,
                        "${Formatos.data(registro.dataHora)} às ${Formatos.hora(registro.dataHora)}"
                    )
                    InfoLinha(Icons.Default.Place, registro.endereco)
                }
            }

            if (registro.justificativa != null) {
                val original = registro.dataHoraOriginal?.let { Formatos.dataHora(it) }.orEmpty()
                val tipoOriginal = registro.tipoOriginal?.descricao.orEmpty()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Ajustado",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            "Original: $tipoOriginal em $original",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Justificativa: ${registro.justificativa}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(Modifier.align(Alignment.End)) {
                TextButton(onClick = onAjustar) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Corrigir")
                }
                TextButton(
                    onClick = onExcluir,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Excluir")
                }
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
        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
        title = { Text("Corrigir registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = data,
                        onValueChange = { data = it; erro = null },
                        label = { Text("Data") },
                        placeholder = { Text("dd/mm/aaaa") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = hora,
                        onValueChange = { hora = it; erro = null },
                        label = { Text("Hora") },
                        placeholder = { Text("hh:mm") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
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
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                erro?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { erro = onSalvar(data, hora, tipo, justificativa) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}
