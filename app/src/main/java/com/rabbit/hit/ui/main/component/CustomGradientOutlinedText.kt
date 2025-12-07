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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.rabbit.hit.R
import kotlin.collections.map

@Composable
fun StrokeGlowTitle(
    caption: String,
    modifier: Modifier = Modifier,
    textScale: TextUnit = 44.sp,
    outlineThickness: Float = 5f,
    outlineTint: Color = Color.White,
    glowPalette: List<Color> = listOf(Color(0xFFF49C47), Color(0xFFF49C47))
) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val pxTextSize = with(density) { textScale.toPx() }

    val fontAsset = remember {
        ResourcesCompat.getFont(ctx, R.font.seymour_one_regular) ?: Typeface.DEFAULT_BOLD
    }

    Canvas(modifier = modifier.fillMaxWidth().height((textScale.value * 1.3).dp)) {
        val paint =
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = pxTextSize
                    this.typeface = fontAsset
                }

        val textWidth = paint.measureText(caption)
        val x = (size.width - textWidth) / 2f
        val fm = paint.fontMetrics
        val y = size.height / 2f - (fm.ascent + fm.descent) / 2f

        paint.style = android.graphics.Paint.Style.STROKE
        paint.strokeWidth = outlineThickness
        paint.color = outlineTint.toArgb()
        paint.strokeJoin = android.graphics.Paint.Join.ROUND
        drawContext.canvas.nativeCanvas.drawText(caption, x, y, paint)

        paint.style = android.graphics.Paint.Style.FILL
        paint.shader =
                android.graphics.LinearGradient(
                        0f,
                        y + fm.ascent,
                        0f,
                        y + fm.descent,
                        glowPalette.map { it.toArgb() }.toIntArray(),
                        null,
                        android.graphics.Shader.TileMode.CLAMP
                )
        drawContext.canvas.nativeCanvas.drawText(caption, x, y, paint)
    }
}