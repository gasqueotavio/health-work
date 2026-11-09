package br.com.healthwork.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.healthwork.data.dao.ColaboradorDao
import br.com.healthwork.data.dao.RegistroPontoDao
import br.com.healthwork.model.Colaborador
import br.com.healthwork.model.RegistroPonto

@Database(
    entities = [
        Colaborador::class,
        RegistroPonto::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colaboradorDao(): ColaboradorDao
    abstract fun registroPontoDao(): RegistroPontoDao
}
