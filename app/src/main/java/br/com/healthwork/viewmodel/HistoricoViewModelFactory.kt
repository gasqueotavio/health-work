package br.com.healthwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.healthwork.data.repository.RegistroPontoRepository

class HistoricoViewModelFactory(
    private val registroPontoRepository: RegistroPontoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(
                registroPontoRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecida")
    }
}
