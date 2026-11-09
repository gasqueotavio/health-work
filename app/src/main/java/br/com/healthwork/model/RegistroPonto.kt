package br.com.healthwork.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registro de ponto. Relacionado ao colaborador pelo código (codigoColaborador).
 * Ao excluir o colaborador, os registros dele são excluídos em cascata.
 *
 * Quando o registro é ajustado, a data/hora e o tipo originais ficam guardados
 * em dataHoraOriginal e tipoOriginal, junto com a justificativa.
 */
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
    /** Data e hora do registro em milissegundos (epoch). */
    val dataHora: Long,
    val tipo: TipoRegistro,
    val endereco: String,
    val fotoPath: String,
    val justificativa: String? = null,
    val dataHoraOriginal: Long? = null,
    val tipoOriginal: TipoRegistro? = null
)

/** Registro junto com o nome do colaborador, usado no histórico. */
data class RegistroComNome(
    @Embedded val registro: RegistroPonto,
    val nomeColaborador: String
)
