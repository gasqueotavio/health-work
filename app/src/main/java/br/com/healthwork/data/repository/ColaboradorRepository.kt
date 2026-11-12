package br.com.healthwork.data.repository

import br.com.healthwork.data.dao.ColaboradorDao
import br.com.healthwork.data.dao.RegistroPontoDao
import br.com.healthwork.model.Colaborador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ColaboradorRepository(
    private val colaboradorDao: ColaboradorDao,
    private val registroPontoDao: RegistroPontoDao
) {

    suspend fun listar() = colaboradorDao.listar()

    suspend fun cadastrar(nome: String) =
        colaboradorDao.inserir(Colaborador(nome = nome))

    suspend fun editar(colaborador: Colaborador) =
        colaboradorDao.atualizar(colaborador)

    suspend fun excluir(colaborador: Colaborador) {
        val fotos = registroPontoDao.fotosDoColaborador(colaborador.codigo)
        colaboradorDao.excluir(colaborador)
        withContext(Dispatchers.IO) {
            fotos.forEach { File(it).delete() }
        }
    }
}
