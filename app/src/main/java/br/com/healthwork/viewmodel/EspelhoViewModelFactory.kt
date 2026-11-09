package br.com.healthwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.data.repository.RegistroPontoRepository

class EspelhoViewModelFactory(
    private val colaboradorRepository: ColaboradorRepository,
    private val registroPontoRepository: RegistroPontoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(EspelhoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EspelhoViewModel(
                colaboradorRepository,
                registroPontoRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecida")
    }
}
