package br.com.healthwork.model

import com.google.gson.annotations.SerializedName

/** Resposta da API Nominatim (geocodificação reversa). Só usamos o endereço completo. */
data class NominatimResponse(
    @SerializedName("display_name")
    val displayName: String?
)
