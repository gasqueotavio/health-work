package br.com.healthwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import br.com.healthwork.data.database.AppDatabase
import br.com.healthwork.data.location.LocalizacaoService
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.data.repository.RegistroPontoRepository
import br.com.healthwork.view.AppNavegacao
import br.com.healthwork.view.theme.HealthWorkTheme

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

        // Ícones da barra de status em branco, por cima da barra azul do topo.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            HealthWorkTheme {
                AppNavegacao(colaboradorRepository, registroPontoRepository)
            }
        }
    }
}
