package br.com.healthwork.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colaboradores")
data class Colaborador(
    @PrimaryKey(autoGenerate = true)
    val codigo: Long = 0,
    val nome: String
)
