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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import br.com.healthwork.model.Colaborador
import br.com.healthwork.view.componentes.Avatar
import br.com.healthwork.view.componentes.EstadoVazio
import br.com.healthwork.viewmodel.ColaboradoresViewModel

@Composable
fun ColaboradoresScreen(
    viewModel: ColaboradoresViewModel
) {
    val colaboradores = viewModel.colaboradores

    LaunchedEffect(Unit) { viewModel.carregar() }

    var dialogoAberto by rememberSaveable { mutableStateOf(false) }
    var codigoEmEdicao by rememberSaveable { mutableStateOf<Long?>(null) }
    var codigoParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }
    val emEdicao = colaboradores.find { it.codigo == codigoEmEdicao }
    val paraExcluir = colaboradores.find { it.codigo == codigoParaExcluir }

    Box(Modifier.fillMaxSize()) {
        if (colaboradores.isEmpty()) {
            EstadoVazio(
                icone = Icons.Default.Person,
                titulo = "Nenhum colaborador cadastrado",
                texto = "Toque em \"Novo colaborador\" para cadastrar o primeiro.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (colaboradores.size == 1) "1 colaborador"
                        else "${colaboradores.size} colaboradores",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(colaboradores, key = { it.codigo }) { colaborador ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(colaborador.nome)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(colaborador.nome, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Código ${colaborador.codigo}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                codigoEmEdicao = colaborador.codigo
                                dialogoAberto = true
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { codigoParaExcluir = colaborador.codigo }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = {
                codigoEmEdicao = null
                dialogoAberto = true
            },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Novo colaborador") },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (dialogoAberto) {
        DialogoColaborador(
            colaborador = emEdicao,
            onSalvar = { nome ->
                viewModel.salvar(emEdicao, nome).also { erro ->
                    if (erro == null) dialogoAberto = false
                }
            },
            onFechar = { dialogoAberto = false }
        )
    }

    paraExcluir?.let { colaborador ->
        AlertDialog(
            onDismissRequest = { codigoParaExcluir = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Excluir colaborador") },
            text = {
                Text("Excluir ${colaborador.nome}? Todos os registros de ponto dele também serão excluídos.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.excluir(colaborador)
                        codigoParaExcluir = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { codigoParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DialogoColaborador(
    colaborador: Colaborador?,
    onSalvar: (String) -> String?,
    onFechar: () -> Unit
) {
    var nome by rememberSaveable { mutableStateOf(colaborador?.nome.orEmpty()) }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onFechar,
        icon = { Icon(Icons.Default.Person, contentDescription = null) },
        title = { Text(if (colaborador == null) "Novo colaborador" else "Editar colaborador") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        erro = null
                    },
                    label = { Text("Nome") },
                    singleLine = true,
                    isError = erro != null,
                    shape = RoundedCornerShape(8.dp)
                )
                erro?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { erro = onSalvar(nome) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}
