package br.com.healthwork.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.healthwork.model.Colaborador
import br.com.healthwork.viewmodel.ColaboradoresViewModel
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ColaboradoresScreen(
    viewModel: ColaboradoresViewModel
) {
    val colaboradores = viewModel.colaboradores

    // Carrega a lista sempre que a tela abre.
    LaunchedEffect(Unit) { viewModel.carregar() }

    var dialogoAberto by rememberSaveable { mutableStateOf(false) }
    var codigoEmEdicao by rememberSaveable { mutableStateOf<Long?>(null) }
    var codigoParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }
    val emEdicao = colaboradores.find { it.codigo == codigoEmEdicao }
    val paraExcluir = colaboradores.find {it.codigo == codigoParaExcluir }

    Box(Modifier.fillMaxSize()) {
        if (colaboradores.isEmpty()) {
            Text(
                "Nenhum colaborador cadastrado.",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colaboradores, key = { it.codigo }) { colaborador ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(colaborador.nome, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Código: ${colaborador.codigo}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = {
                            codigoEmEdicao = colaborador.codigo
                            dialogoAberto = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { codigoParaExcluir = colaborador.codigo }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                codigoEmEdicao = null
                dialogoAberto = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Cadastrar colaborador")
        }
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
            title = { Text("Excluir colaborador") },
            text = {
                Text("Excluir ${colaborador.nome}? Todos os registros de ponto dele também serão excluídos.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluir(colaborador)
                    codigoParaExcluir = null
                }) { Text("Excluir") }
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
        title = { Text(if (colaborador == null) "Cadastrar colaborador" else "Editar colaborador") },
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
                    isError = erro != null
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
            TextButton(onClick = { erro = onSalvar(nome) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}
