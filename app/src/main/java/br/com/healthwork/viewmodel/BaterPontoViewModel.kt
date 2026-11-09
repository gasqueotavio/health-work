package br.com.healthwork.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.data.repository.RegistroPontoRepository
import br.com.healthwork.model.Colaborador
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.model.TipoRegistro
import br.com.healthwork.utils.Formatos
import kotlinx.coroutines.launch
import java.io.File

/** Etapas do fluxo "Bater ponto": localização -> endereço -> foto -> salvar. */
enum class EtapaPonto {
    OCIOSO,
    OBTENDO_LOCALIZACAO,
    AGUARDANDO_FOTO,
    CAMERA_ABERTA,
    SALVANDO
}

class BaterPontoViewModel(
    private val colaboradorRepository: ColaboradorRepository,
    private val registroRepository: RegistroPontoRepository
) : ViewModel() {

    var colaboradores by mutableStateOf<List<Colaborador>>(emptyList())
        private set

    var selecionado by mutableStateOf<Colaborador?>(null)
        private set

    var proximoTipo by mutableStateOf<TipoRegistro?>(null)
        private set

    var etapa by mutableStateOf(EtapaPonto.OCIOSO)
        private set

    var mensagem by mutableStateOf<String?>(null)
        private set

    var ultimoRegistro by mutableStateOf<RegistroPonto?>(null)
        private set

    // Dados guardados entre a localização e a foto.
    private var codigoPendente: Long = 0
    private var enderecoPendente: String = ""
    var fotoPendente: File? = null
        private set

    /** Chamado quando a tela abre. */
    fun carregar() {
        viewModelScope.launch {
            colaboradores = colaboradorRepository.listar()
            // Mantém a seleção só se o colaborador ainda existir (pode ter sido editado ou excluído).
            selecionado = colaboradores.find { it.codigo == selecionado?.codigo }
            atualizarProximoTipo()
        }
    }

    fun selecionar(colaborador: Colaborador) {
        selecionado = colaborador
        mensagem = null
        viewModelScope.launch { atualizarProximoTipo() }
    }

    private suspend fun atualizarProximoTipo() {
        val colaborador = selecionado
        proximoTipo = if (colaborador == null) null
        else registroRepository.proximoTipo(colaborador.codigo)
    }

    /** Passos 2 e 3: captura a localização e consulta a API de geocodificação. */
    fun baterPonto() {
        val colaborador = selecionado
        if (colaborador == null) {
            mensagem = "Selecione um colaborador."
            return
        }
        if (etapa != EtapaPonto.OCIOSO) return

        etapa = EtapaPonto.OBTENDO_LOCALIZACAO
        mensagem = null
        viewModelScope.launch {
            try {
                codigoPendente = colaborador.codigo
                enderecoPendente = registroRepository.obterEnderecoAtual()
                fotoPendente = registroRepository.criarArquivoFoto()
                etapa = EtapaPonto.AGUARDANDO_FOTO
            } catch (e: Exception) {
                etapa = EtapaPonto.OCIOSO
                mensagem = e.message ?: "Não foi possível obter a localização."
            }
        }
    }

    fun permissaoNegada() {
        mensagem = "Permita o acesso à localização para bater o ponto."
    }

    /** Passo 4: a tela avisa que abriu a câmera. */
    fun cameraAberta() {
        if (etapa == EtapaPonto.AGUARDANDO_FOTO) {
            etapa = EtapaPonto.CAMERA_ABERTA
        }
    }

    /** Passo 5: com a foto tirada, o registro é salvo. */
    fun fotoCapturada(sucesso: Boolean) {
        if (etapa != EtapaPonto.CAMERA_ABERTA) return
        val foto = fotoPendente ?: return

        viewModelScope.launch {
            if (!sucesso || !foto.exists() || foto.length() == 0L) {
                registroRepository.descartarFoto(foto)
                fotoPendente = null
                etapa = EtapaPonto.OCIOSO
                mensagem = "A foto não foi tirada. O ponto não foi registrado."
                return@launch
            }

            etapa = EtapaPonto.SALVANDO
            val registro = registroRepository.bater(codigoPendente, enderecoPendente, foto)
            fotoPendente = null
            ultimoRegistro = registro
            mensagem = "${registro.tipo.descricao} registrada às ${Formatos.hora(registro.dataHora)}."
            atualizarProximoTipo()
            etapa = EtapaPonto.OCIOSO
        }
    }
}
