package br.com.healthwork.data.api

import br.com.healthwork.model.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Geocodificação reversa (coordenadas -> endereço) pelo Nominatim / OpenStreetMap. */
interface NominatimService {

    @GET("reverse")
    suspend fun buscarEndereco(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") formato: String = "jsonv2",
        @Query("accept-language") idioma: String = "pt-BR"
    ): NominatimResponse
}
