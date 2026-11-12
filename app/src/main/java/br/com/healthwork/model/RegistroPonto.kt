package br.com.healthwork.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_ponto",
    foreignKeys = [
        ForeignKey(
            entity = Colaborador::class,
            parentColumns = ["codigo"],
            childColumns = ["codigoColaborador"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("codigoColaborador")]
)
data class RegistroPonto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigoColaborador: Long,
    val dataHora: Long,
    val tipo: TipoRegistro,
    val endereco: String,
    val fotoPath: String,
    val justificativa: String? = null,
    val dataHoraOriginal: Long? = null,
    val tipoOriginal: TipoRegistro? = null
)

data class RegistroComNome(
    @Embedded val registro: RegistroPonto,
    val nomeColaborador: String
)
