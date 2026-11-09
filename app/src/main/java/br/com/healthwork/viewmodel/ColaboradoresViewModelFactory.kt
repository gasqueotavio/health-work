package br.com.healthwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.healthwork.data.repository.ColaboradorRepository

class ColaboradoresViewModelFactory(
    private val colaboradorRepository: ColaboradorRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ColaboradoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColaboradoresViewModel(
                colaboradorRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconhecida")
    }
}
