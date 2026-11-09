package br.com.healthwork.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.healthwork.model.RegistroComNome
import br.com.healthwork.model.RegistroPonto

@Dao
interface RegistroPontoDao {

    @Insert
    suspend fun inserir(registro: RegistroPonto): Long

    @Query(
        """
        SELECT r.*, c.nome AS nomeColaborador
        FROM registros_ponto r
        INNER JOIN colaboradores c ON c.codigo = r.codigoColaborador
        ORDER BY r.dataHora DESC
        """
    )
    suspend fun listarComNome(): List<RegistroComNome>

    @Query(
        """
        SELECT * FROM registros_ponto
        WHERE codigoColaborador = :codigo AND dataHora >= :inicio AND dataHora < :fim
        ORDER BY dataHora
        """
    )
    suspend fun listarPorPeriodo(codigo: Long, inicio: Long, fim: Long): List<RegistroPonto>

    @Query(
        """
        SELECT * FROM registros_ponto
        WHERE codigoColaborador = :codigo AND dataHora >= :inicio AND dataHora < :fim
        ORDER BY dataHora DESC
        LIMIT 1
        """
    )
    suspend fun ultimoNoPeriodo(codigo: Long, inicio: Long, fim: Long): RegistroPonto?

    @Query("SELECT fotoPath FROM registros_ponto WHERE codigoColaborador = :codigo")
    suspend fun fotosDoColaborador(codigo: Long): List<String>

    @Update
    suspend fun atualizar(registro: RegistroPonto)

    @Delete
    suspend fun excluir(registro: RegistroPonto)
}
