package br.com.healthwork.data.repository

import br.com.healthwork.data.api.RetrofitInstance
import br.com.healthwork.data.dao.RegistroPontoDao
import br.com.healthwork.data.location.LocalizacaoService
import br.com.healthwork.model.Colaborador
import br.com.healthwork.model.RegistroPonto
import br.com.healthwork.model.TipoRegistro
import br.com.healthwork.utils.Formatos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class RegistroPontoRepository(
    private val dao: RegistroPontoDao,
    private val localizacao: LocalizacaoService,
    private val pastaArquivos: File,
    private val pastaCache: File
) {

    suspend fun listar() = dao.listarComNome()

    suspend fun registrosDoMes(codigoColaborador: Long, mes: YearMonth): List<RegistroPonto> {
        val (inicio, fim) = Formatos.intervaloDoMes(mes)
        return dao.listarPorPeriodo(codigoColaborador, inicio, fim)
    }

    /**
     * Tipo alternado automaticamente: se o último registro do dia foi entrada,
     * o próximo é saída; caso contrário (ou sem registro no dia), é entrada.
     */
    suspend fun proximoTipo(codigoColaborador: Long): TipoRegistro {
        val (inicio, fim) = Formatos.intervaloDoDia(LocalDate.now())
        val ultimo = dao.ultimoNoPeriodo(codigoColaborador, inicio, fim)
        return if (ultimo?.tipo == TipoRegistro.ENTRADA) TipoRegistro.SAIDA else TipoRegistro.ENTRADA
    }

    /**
     * Captura a localização e consulta a API de geocodificação para obter o endereço.
     * Se a API falhar (ex.: sem internet), devolve as coordenadas no lugar do endereço.
     */
    suspend fun obterEnderecoAtual(): String {
        val local = localizacao.obterLocalizacaoAtual()
        return try {
            RetrofitInstance.api.buscarEndereco(local.latitude, local.longitude).displayName
                ?: coordenadas(local.latitude, local.longitude)
        } catch (e: Exception) {
            coordenadas(local.latitude, local.longitude)
        }
    }

    private fun coordenadas(latitude: Double, longitude: Double): String =
        String.format(Locale.US, "%.6f, %.6f", latitude, longitude)

    /** Cria o arquivo onde a câmera vai gravar a foto do comprovante. */
    suspend fun criarArquivoFoto(): File = withContext(Dispatchers.IO) {
        val pasta = File(pastaArquivos, "fotos").apply { mkdirs() }
        File(pasta, "ponto_${System.currentTimeMillis()}.jpg")
    }

    suspend fun descartarFoto(foto: File) {
        withContext(Dispatchers.IO) { foto.delete() }
    }

    suspend fun bater(codigoColaborador: Long, endereco: String, foto: File): RegistroPonto {
        val registro = RegistroPonto(
            codigoColaborador = codigoColaborador,
            dataHora = System.currentTimeMillis(),
            tipo = proximoTipo(codigoColaborador),
            endereco = endereco,
            fotoPath = foto.absolutePath
        )
        val id = dao.inserir(registro)
        return registro.copy(id = id)
    }

    /**
     * Ajusta data/hora e tipo com justificativa. Na primeira correção, os valores
     * originais são guardados; nas seguintes, o original é mantido.
     */
    suspend fun ajustar(
        registro: RegistroPonto,
        novaDataHora: Long,
        novoTipo: TipoRegistro,
        justificativa: String
    ) {
        dao.atualizar(
            registro.copy(
                dataHora = novaDataHora,
                tipo = novoTipo,
                justificativa = justificativa,
                dataHoraOriginal = registro.dataHoraOriginal ?: registro.dataHora,
                tipoOriginal = registro.tipoOriginal ?: registro.tipo
            )
        )
    }

    suspend fun excluir(registro: RegistroPonto) {
        dao.excluir(registro)
        withContext(Dispatchers.IO) { File(registro.fotoPath).delete() }
    }

    /** Gera o espelho de ponto do mês em CSV (separado por ponto e vírgula). */
    suspend fun gerarEspelhoCsv(colaborador: Colaborador, mes: YearMonth): File {
        val registros = registrosDoMes(colaborador.codigo, mes)
        return withContext(Dispatchers.IO) {
            val pasta = File(pastaCache, "espelhos").apply { mkdirs() }
            val arquivo = File(pasta, "espelho_${colaborador.codigo}_$mes.csv")

            val linhas = buildList {
                add(
                    listOf(
                        "Código", "Colaborador", "Data", "Hora", "Tipo", "Endereço",
                        "Ajustado", "Justificativa", "Data/hora original", "Tipo original"
                    )
                )
                registros.forEach { r ->
                    add(
                        listOf(
                            colaborador.codigo.toString(),
                            colaborador.nome,
                            Formatos.data(r.dataHora),
                            Formatos.hora(r.dataHora),
                            r.tipo.descricao,
                            r.endereco,
                            if (r.justificativa != null) "Sim" else "Não",
                            r.justificativa.orEmpty(),
                            r.dataHoraOriginal?.let { Formatos.dataHora(it) }.orEmpty(),
                            r.tipoOriginal?.descricao.orEmpty()
                        )
                    )
                }
            }

            // BOM UTF-8 para o Excel abrir os acentos corretamente.
            val conteudo = "﻿" + linhas.joinToString("\r\n") { linha ->
                linha.joinToString(";") { campo -> "\"" + campo.replace("\"", "\"\"") + "\"" }
            }
            arquivo.writeText(conteudo, Charsets.UTF_8)
            arquivo
        }
    }
}
