package com.rabbit.hit.ui.main.component

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.rabbit.hit.R
import kotlin.collections.map

@Composable
fun GradientOutlinedText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = 44.sp,
        strokeWidth: Float = 5f,
        gradientColors: List<Color> = listOf(Color(0xFFF49C47), Color(0xFFF49C47))
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val textSizePx = with(density) { fontSize.toPx() }

    // ███████ 1️⃣  Загружаем нужный Baloo-шрифт
    val typeface = remember {
        ResourcesCompat.getFont(context, R.font.seymour_one_regular) ?: Typeface.DEFAULT_BOLD
    }

    Canvas(modifier = modifier.fillMaxWidth().height((fontSize.value * 1.3).dp)) {
        val paint =
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = textSizePx
                    this.typeface = typeface
                }

        val textWidth = paint.measureText(text)
        val x = (size.width - textWidth) / 2f
        val fm = paint.fontMetrics
        val y = size.height / 2f - (fm.ascent + fm.descent) / 2f

        // ███████ 2️⃣ Контур
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = 0xffffffff.toInt()
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)

        // ███████ 3️⃣ Градиентная заливка
        paint.style = android.graphics.Paint.Style.FILL
        paint.shader =
                android.graphics.LinearGradient(
                        0f,
                        y + fm.ascent,
                        0f,
                        y + fm.descent,
                        gradientColors.map { it.toArgb() }.toIntArray(),
                        null,
                        android.graphics.Shader.TileMode.CLAMP
                )
        drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
    }
}

@Composable
fun GradientOutlinedTextShort(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = 44.sp,
        strokeWidth: Float = 5f,
        gradientColors: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFFF6F00)),
        textAlign: TextAlign = TextAlign.Start,
        horizontalPadding: Dp = 0.dp
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val textSizePx = with(density) { fontSize.toPx() }
    val padPx = with(density) { horizontalPadding.toPx() }

    val typeface = remember {
        ResourcesCompat.getFont(context, R.font.seymour_one_regular) ?: Typeface.DEFAULT_BOLD
    }

    Canvas(modifier = modifier.fillMaxWidth().height((fontSize.value * 1.3f).dp)) {
        val paint =
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = textSizePx
                    this.typeface = typeface
                }

        val textWidth = paint.measureText(text)

        val x =
                when (textAlign) {
                    TextAlign.Start, TextAlign.Left -> padPx
                    TextAlign.End, TextAlign.Right -> size.width - textWidth - padPx
                    else -> (size.width - textWidth) / 2f
                }

        val fm = paint.fontMetrics
        val y = size.height / 2f - (fm.ascent + fm.descent) / 2f

        // Контур
        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = 0xFF000000.toInt()
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)

        // Градиент
        paint.style = android.graphics.Paint.Style.FILL
        paint.shader =
                android.graphics.LinearGradient(
                        0f,
                        y + fm.ascent,
                        0f,
                        y + fm.descent,
                        gradientColors.map { it.toArgb() }.toIntArray(),
                        null,
                        android.graphics.Shader.TileMode.CLAMP
                )
        drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
    }
}
