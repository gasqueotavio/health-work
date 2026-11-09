package br.com.healthwork.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // A política de uso do Nominatim exige um User-Agent que identifique o app.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requisicao = chain.request().newBuilder()
                .header("User-Agent", "health-work/1.0 (Android)")
                .build()
            chain.proceed(requisicao)
        }
        .build()

    val api: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(cliente)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(NominatimService::class.java)
    }
}
