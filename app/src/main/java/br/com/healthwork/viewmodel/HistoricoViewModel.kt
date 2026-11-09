package br.com.healthwork.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.healthwork.data.repository.RegistroPontoRepository
import br.com.healthwork.model.RegistroComNome
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.model.TipoRegistro
import br.com.healthwork.utils.Formatos
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: RegistroPontoRepository
) : ViewModel() {

    var registros by mutableStateOf<List<RegistroComNome>>(emptyList())
        private set

    /** Chamado quando a tela abre. */
    fun carregar() {
        viewModelScope.launch {
            registros = repository.listar()
        }
    }

    /**
     * Corrige data, hora e tipo do registro com justificativa.
     * Retorna a mensagem de erro, ou null se deu certo.
     */
    fun ajustar(
        registro: RegistroPonto,
        dataTexto: String,
        horaTexto: String,
        tipo: TipoRegistro,
        justificativa: String
    ): String? {
        val novaDataHora = Formatos.lerDataHora(dataTexto, horaTexto)
            ?: return "Data ou hora inválida. Use dd/mm/aaaa e hh:mm."
        if (justificativa.isBlank()) return "Informe a justificativa."

        viewModelScope.launch {
            repository.ajustar(registro, novaDataHora, tipo, justificativa.trim())
            registros = repository.listar()
        }
        return null
    }

    fun excluir(registro: RegistroPonto) {
        viewModelScope.launch {
            repository.excluir(registro)
            registros = repository.listar()
        }
    }
}
