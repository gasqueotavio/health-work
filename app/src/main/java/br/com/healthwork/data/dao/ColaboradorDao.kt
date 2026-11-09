package br.com.healthwork.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.healthwork.model.Colaborador

@Dao
interface ColaboradorDao {

    @Insert
    suspend fun inserir(colaborador: Colaborador): Long

    @Query("SELECT * FROM colaboradores ORDER BY nome")
    suspend fun listar(): List<Colaborador>

    @Update
    suspend fun atualizar(colaborador: Colaborador)

    @Delete
    suspend fun excluir(colaborador: Colaborador)
}
