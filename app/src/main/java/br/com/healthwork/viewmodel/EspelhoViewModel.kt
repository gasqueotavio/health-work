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
import kotlinx.coroutines.launch
import java.io.File
import java.time.YearMonth

class EspelhoViewModel(
    private val colaboradorRepository: ColaboradorRepository,
    private val registroRepository: RegistroPontoRepository
) : ViewModel() {

    var colaboradores by mutableStateOf<List<Colaborador>>(emptyList())
        private set

    var selecionado by mutableStateOf<Colaborador?>(null)
        private set

    var mes by mutableStateOf(YearMonth.now())
        private set

    var registrosDoMes by mutableStateOf<List<RegistroPonto>>(emptyList())
        private set

    var arquivoParaCompartilhar by mutableStateOf<File?>(null)
        private set

    fun carregar() {
        viewModelScope.launch {
            colaboradores = colaboradorRepository.listar()
            selecionado = colaboradores.find { it.codigo == selecionado?.codigo }
            carregarRegistrosDoMes()
        }
    }

    fun selecionar(colaborador: Colaborador) {
        selecionado = colaborador
        viewModelScope.launch { carregarRegistrosDoMes() }
    }

    fun mesAnterior() {
        mes = mes.minusMonths(1)
        viewModelScope.launch { carregarRegistrosDoMes() }
    }

    fun proximoMes() {
        mes = mes.plusMonths(1)
        viewModelScope.launch { carregarRegistrosDoMes() }
    }

    private suspend fun carregarRegistrosDoMes() {
        val colaborador = selecionado
        registrosDoMes = if (colaborador == null) emptyList()
        else registroRepository.registrosDoMes(colaborador.codigo, mes)
    }

    fun gerarEspelho() {
        val colaborador = selecionado ?: return
        viewModelScope.launch {
            arquivoParaCompartilhar = registroRepository.gerarEspelhoCsv(colaborador, mes)
        }
    }

    fun compartilhamentoIniciado() {
        arquivoParaCompartilhar = null
    }
}
