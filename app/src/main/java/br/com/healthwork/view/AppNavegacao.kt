package br.com.healthwork.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.healthwork.data.repository.ColaboradorRepository
import br.com.healthwork.data.repository.RegistroPontoRepository
import br.com.healthwork.viewmodel.BaterPontoViewModel
import br.com.healthwork.viewmodel.BaterPontoViewModelFactory
import br.com.healthwork.viewmodel.ColaboradoresViewModel
import br.com.healthwork.viewmodel.ColaboradoresViewModelFactory
import br.com.healthwork.viewmodel.EspelhoViewModel
import br.com.healthwork.viewmodel.EspelhoViewModelFactory
import br.com.healthwork.viewmodel.HistoricoViewModel
import br.com.healthwork.viewmodel.HistoricoViewModelFactory

/** As quatro telas descritas na proposta. */
enum class Tela(val rota: String, val titulo: String, val icone: ImageVector) {
    COLABORADORES("colaboradores", "Colaboradores", Icons.Default.Person),
    BATER_PONTO("bater_ponto", "Bater ponto", Icons.Default.LocationOn),
    HISTORICO("historico", "Histórico", Icons.AutoMirrored.Filled.List),
    ESPELHO("espelho", "Espelho mensal", Icons.Default.Share)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacao(
    colaboradorRepository: ColaboradorRepository,
    registroPontoRepository: RegistroPontoRepository
) {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val telaAtual = Tela.entries.find { it.rota == rotaAtual } ?: Tela.BATER_PONTO

    Scaffold(
        topBar = { TopAppBar(title = { Text(telaAtual.titulo) }) },
        bottomBar = {
            NavigationBar {
                Tela.entries.forEach { tela ->
                    NavigationBarItem(
                        selected = rotaAtual == tela.rota,
                        onClick = {
                            navController.navigate(tela.rota) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tela.icone, contentDescription = null) },
                        label = { Text(tela.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tela.BATER_PONTO.rota,
            modifier = Modifier.padding(padding)
        ) {
            composable(Tela.COLABORADORES.rota) {
                val viewModel: ColaboradoresViewModel = viewModel(
                    factory = ColaboradoresViewModelFactory(colaboradorRepository)
                )
                ColaboradoresScreen(viewModel)
            }
            composable(Tela.BATER_PONTO.rota) {
                val viewModel: BaterPontoViewModel = viewModel(
                    factory = BaterPontoViewModelFactory(colaboradorRepository, registroPontoRepository)
                )
                BaterPontoScreen(viewModel)
            }
            composable(Tela.HISTORICO.rota) {
                val viewModel: HistoricoViewModel = viewModel(
                    factory = HistoricoViewModelFactory(registroPontoRepository)
                )
                HistoricoScreen(viewModel)
            }
            composable(Tela.ESPELHO.rota) {
                val viewModel: EspelhoViewModel = viewModel(
                    factory = EspelhoViewModelFactory(colaboradorRepository, registroPontoRepository)
                )
                EspelhoScreen(viewModel)
            }
        }
    }
}
