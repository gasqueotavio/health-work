package br.com.healthwork.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.utils.Formatos
import br.com.healthwork.view.componentes.BadgeTipo
import br.com.healthwork.view.componentes.FotoMiniatura
import br.com.healthwork.view.componentes.InfoLinha
import br.com.healthwork.view.componentes.SeletorColaborador
import br.com.healthwork.viewmodel.BaterPontoViewModel
import br.com.healthwork.viewmodel.EtapaPonto
import kotlinx.coroutines.delay

private val PERMISSOES_LOCALIZACAO = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@Composable
fun BaterPontoScreen(
    viewModel: BaterPontoViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.carregar() }

    var agora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            agora = System.currentTimeMillis()
            delay(1_000)
        }
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        viewModel.fotoCapturada(sucesso)
    }

    val permissaoLocalizacao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) viewModel.baterPonto() else viewModel.permissaoNegada()
    }

    val etapa = viewModel.etapa
    LaunchedEffect(etapa) {
        val foto = viewModel.fotoPendente
        if (etapa == EtapaPonto.AGUARDANDO_FOTO && foto != null) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                foto
            )
            viewModel.cameraAberta()
            try {
                camera.launch(uri)
            } catch (e: ActivityNotFoundException) {
                viewModel.fotoCapturada(false)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(Formatos.diaPorExtenso(agora), style = MaterialTheme.typography.bodyLarge)
                Text(Formatos.hora(agora), style = MaterialTheme.typography.headlineMedium)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (viewModel.colaboradores.isEmpty()) {
                    Text(
                        "Cadastre um colaborador na aba Colaboradores para bater o ponto.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SeletorColaborador(
                    colaboradores = viewModel.colaboradores,
                    selecionado = viewModel.selecionado,
                    onSelecionar = { viewModel.selecionar(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                viewModel.proximoTipo?.let { tipo ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Próxima marcação",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        BadgeTipo(tipo)
                    }
                }
            }
        }

        Button(
            onClick = {
                val temPermissao = PERMISSOES_LOCALIZACAO.any {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                if (temPermissao) {
                    viewModel.baterPonto()
                } else {
                    permissaoLocalizacao.launch(PERMISSOES_LOCALIZACAO)
                }
            },
            enabled = viewModel.selecionado != null && etapa == EtapaPonto.OCIOSO,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Bater ponto", style = MaterialTheme.typography.titleMedium)
        }

        val textoProgresso = when (etapa) {
            EtapaPonto.OBTENDO_LOCALIZACAO -> "Obtendo localização e endereço..."
            EtapaPonto.AGUARDANDO_FOTO, EtapaPonto.CAMERA_ABERTA -> "Tire a foto do comprovante..."
            EtapaPonto.SALVANDO -> "Salvando registro..."
            EtapaPonto.OCIOSO -> null
        }
        if (textoProgresso != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp)
                Text(textoProgresso)
            }
        }

        viewModel.mensagem?.let { mensagem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(mensagem, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        viewModel.ultimoRegistro?.let { UltimoRegistro(it) }
    }
}

@Composable
private fun UltimoRegistro(registro: RegistroPonto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Último registro",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                BadgeTipo(registro.tipo)
            }
            InfoLinha(Icons.Default.DateRange, Formatos.dataHora(registro.dataHora))
            InfoLinha(Icons.Default.Place, registro.endereco, maxLinhas = 3)
            FotoMiniatura(
                caminho = registro.fotoPath,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
