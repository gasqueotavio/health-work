package br.com.healthwork.view

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import br.com.healthwork.utils.Formatos
import br.com.healthwork.view.componentes.SeletorColaborador
import br.com.healthwork.viewmodel.EspelhoViewModel

@Composable
fun EspelhoScreen(
    viewModel: EspelhoViewModel
) {
    val context = LocalContext.current
    val selecionado = viewModel.selecionado
    val registros = viewModel.registrosDoMes

    // Carrega os colaboradores sempre que a tela abre.
    LaunchedEffect(Unit) { viewModel.carregar() }

    // Abre o seletor de aplicativos do Android (WhatsApp, e-mail etc.) com o CSV.
    val arquivo = viewModel.arquivoParaCompartilhar
    LaunchedEffect(arquivo) {
        if (arquivo == null || selecionado == null) return@LaunchedEffect
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivo
        )
        val titulo = "Espelho de ponto - ${selecionado.nome} - ${Formatos.mes(viewModel.mes)}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_SUBJECT, titulo)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartilhar espelho de ponto"))
        viewModel.compartilhamentoIniciado()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SeletorColaborador(
            colaboradores = viewModel.colaboradores,
            selecionado = selecionado,
            onSelecionar = { viewModel.selecionar(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.mesAnterior() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
            }
            Text(
                Formatos.mes(viewModel.mes),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.proximoMes() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
            }
        }

        Button(
            onClick = { viewModel.gerarEspelho() },
            enabled = selecionado != null && registros.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Compartilhar espelho")
        }

        when {
            selecionado == null -> Text("Selecione um colaborador para ver o espelho do mês.")
            registros.isEmpty() -> Text("Nenhum registro neste mês.")
            else -> LazyColumn(Modifier.weight(1f)) {
                items(registros, key = { it.id }) { registro ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(Formatos.data(registro.dataHora))
                        Text(Formatos.hora(registro.dataHora))
                        Text(registro.tipo.descricao, modifier = Modifier.weight(1f))
                        if (registro.justificativa != null) {
                            Text(
                                "Ajustado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
