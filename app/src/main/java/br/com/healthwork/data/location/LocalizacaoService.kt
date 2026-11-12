package br.com.healthwork.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocalizacaoService(private val context: Context) {

    private val gerenciador =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    suspend fun obterLocalizacaoAtual(): Location = suspendCancellableCoroutine { cont ->
        val erroGps = IllegalStateException(
            "Não foi possível obter a localização. Verifique se o GPS está ligado."
        )

        val provedor = when {
            gerenciador.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            gerenciador.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }
        if (provedor == null) {
            cont.resumeWithException(erroGps)
            return@suspendCancellableCoroutine
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cancelamento = CancellationSignal()
            gerenciador.getCurrentLocation(
                provedor,
                cancelamento,
                ContextCompat.getMainExecutor(context)
            ) { local ->
                if (local != null) cont.resume(local) else cont.resumeWithException(erroGps)
            }
            cont.invokeOnCancellation { cancelamento.cancel() }
        } else {
            val ouvinte = object : LocationListener {
                override fun onLocationChanged(local: Location) {
                    cont.resume(local)
                }

                @Deprecated("Exigido em versões antigas do Android")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            @Suppress("DEPRECATION")
            gerenciador.requestSingleUpdate(provedor, ouvinte, Looper.getMainLooper())
            cont.invokeOnCancellation { gerenciador.removeUpdates(ouvinte) }
        }
    }
}
