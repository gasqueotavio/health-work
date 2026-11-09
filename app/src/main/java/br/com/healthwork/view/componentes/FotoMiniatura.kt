package br.com.healthwork.view.componentes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Exibe a foto de comprovante salva no aparelho, reduzida para miniatura. */
@Composable
fun FotoMiniatura(caminho: String, modifier: Modifier = Modifier) {
    val imagem by produceState<ImageBitmap?>(initialValue = null, caminho) {
        value = withContext(Dispatchers.IO) { carregar(caminho, tamanhoMaximo = 300) }
    }

    val bitmap = imagem
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Foto do registro",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

private fun carregar(caminho: String, tamanhoMaximo: Int): ImageBitmap? {
    if (!File(caminho).exists()) return null

    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(caminho, limites)
    var amostra = 1
    while (limites.outWidth / (amostra * 2) >= tamanhoMaximo &&
        limites.outHeight / (amostra * 2) >= tamanhoMaximo
    ) {
        amostra *= 2
    }

    val bitmap = BitmapFactory.decodeFile(
        caminho,
        BitmapFactory.Options().apply { inSampleSize = amostra }
    ) ?: return null

    return girarConformeExif(caminho, bitmap).asImageBitmap()
}

private fun girarConformeExif(caminho: String, bitmap: Bitmap): Bitmap {
    val graus = when (
        ExifInterface(caminho).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    ) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> return bitmap
    }
    val matriz = Matrix().apply { postRotate(graus) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
}
