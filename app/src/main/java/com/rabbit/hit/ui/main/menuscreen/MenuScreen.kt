package com.rabbit.hit.ui.main.menuscreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.PrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryVariant
import com.rabbit.hit.ui.main.component.SecondaryIconButton
import kotlin.math.min

@Composable
fun MenuScreen(
    state: MainViewModel.UiState,
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenShop: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "chicken_float")
    val floatOffset = transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .windowInsetsPadding(WindowInsets.displayCutout),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // ---------- верхний ряд ---------- //
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShopBadge(onOpenShop = onOpenShop, coins = state.coins)
                SecondaryIconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open settings",
                        tint = Color(0xFFF69533),
                        modifier = Modifier.fillMaxSize(0.82f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.weight(0.3f))

            // ---------- блок титула ---------- //
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingChickenCanvas(
                    offsetNorm = floatOffset.value / 8f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.title_text),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    text = "Play",
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(64.dp),
                    variant = PrimaryVariant.Orange
                )

                if (state.lastHeight > 0) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Record: ${state.lastHeight} m",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF7B4A2D),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.weight(1.1f))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FloatingChickenCanvas(
    offsetNorm: Float,
    modifier: Modifier = Modifier
) {
    val chickenBitmap = ImageBitmap.imageResource(id = R.drawable.title_chicken)

    Canvas(modifier = modifier) {
        val canvasW = size.width
        val canvasH = size.height

        if (canvasW <= 0f || canvasH <= 0f) return@Canvas

        val imgW = chickenBitmap.width.toFloat()
        val imgH = chickenBitmap.height.toFloat()

        val scale = min(
            canvasW * 0.5f / imgW,
            canvasH * 0.8f / imgH
        )

        val dstW = imgW * scale
        val dstH = imgH * scale

        val centerX = canvasW / 2f
        val centerY = canvasH / 2f

        val margin = 8.dp.toPx()

        val maxShift = (canvasH / 2f) - (dstH / 2f) - margin
        val clampedOffset = offsetNorm.coerceIn(-1f, 1f)

        val currentY = centerY + maxShift * clampedOffset

        val left = centerX - dstW / 2f
        val top = currentY - dstH / 2f

        drawImage(
            image = chickenBitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(chickenBitmap.width, chickenBitmap.height),
            dstOffset = IntOffset(left.toInt(), top.toInt()),
            dstSize = IntSize(dstW.toInt(), dstH.toInt())
        )
    }
}


@Composable
private fun ShopBadge(onOpenShop: () -> Unit, coins: Int) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x40b68e6c),
                        Color(0x40b68e6c)
                    )
                )
            )
            .clickable(onClick = onOpenShop),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.egg_2),
                contentDescription = "New Skin",
                modifier = Modifier.size(50.dp)
            )

            Text(
                text = "NEW SKIN",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.92f),
                letterSpacing = 1.sp
            )
        }
    }
}