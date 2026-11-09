package br.com.healthwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.room.Room
import br.com.healthwork.data.database.AppDatabase
import br.com.healthwork.data.location.LocalizacaoService
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.data.repository.RegistroPontoRepository
import br.com.healthwork.view.AppNavegacao

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "health_work.db"
        ).fallbackToDestructiveMigration()
            .build()

        val colaboradorRepository = ColaboradorRepository(
            database.colaboradorDao(),
            database.registroPontoDao()
        )

        val registroPontoRepository = RegistroPontoRepository(
            database.registroPontoDao(),
            LocalizacaoService(applicationContext),
            filesDir,
            cacheDir
        )

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                AppNavegacao(colaboradorRepository, registroPontoRepository)
            }
        }
    }
}
