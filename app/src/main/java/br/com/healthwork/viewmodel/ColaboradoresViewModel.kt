package br.com.healthwork.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.model.Colaborador
import kotlinx.coroutines.launch

class ColaboradoresViewModel(
    private val repository: ColaboradorRepository
) : ViewModel() {

    var colaboradores by mutableStateOf<List<Colaborador>>(emptyList())
        private set

    /** Chamado quando a tela abre. */
    fun carregar() {
        viewModelScope.launch {
            colaboradores = repository.listar()
        }
    }

    /**
     * Cadastra (colaborador == null) ou edita o colaborador.
     * Retorna a mensagem de erro, ou null se deu certo.
     */
    fun salvar(colaborador: Colaborador?, nome: String): String? {
        val nomeLimpo = nome.trim()
        if (nomeLimpo.isEmpty()) return "Informe o nome do colaborador."

        viewModelScope.launch {
            if (colaborador == null) {
                repository.cadastrar(nomeLimpo)
            } else {
                repository.editar(colaborador.copy(nome = nomeLimpo))
            }
            colaboradores = repository.listar()
        }
        return null
    }

    fun excluir(colaborador: Colaborador) {
        viewModelScope.launch {
            repository.excluir(colaborador)
            colaboradores = repository.listar()
        }
    }
}
