package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.gamescreen.GameResult
import com.rabbit.hit.ui.main.component.StrokeGlowTitle
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.component.SeymourFont

@Composable
fun WinOverlay(
    result: GameResult,
    isWin: Boolean = false,
    onRetry: () -> Unit,
    onHome: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val titleScale by
        animateFloatAsState(
            targetValue = if (visible) 1f else 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "title_scale"
        )

    val coinBounce by
        animateFloatAsState(
            targetValue = if (visible) 1f else 0.4f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "coin_scale"
        )

    val infiniteTransition = rememberInfiniteTransition(label = "win_overlay")
    val coinRotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1800, easing = LinearEasing)
                ),
            label = "coin_rotation"
        )

    val rabbitWiggle by
        infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(2600, easing = LinearEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                ),
            label = "rabbit_wiggle"
        )

    val rabbitAlpha by
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(2200, easing = LinearEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                ),
            label = "rabbit_alpha"
        )

    // Тёмный фон
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ==== ЗАГОЛОВОК ====
                Box(modifier = Modifier.graphicsLayer { scaleX = titleScale; scaleY = titleScale }) {
                    if (isWin) {
                        StrokeGlowTitle(
                            caption = "Well Done!",
                            textScale = 42.sp,
                            outlineThickness = 14f,
                            outlineTint = Color.White,
                            glowPalette = listOf(Color(0xFFE71414), Color(0xFFE71414))
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            StrokeGlowTitle(
                                caption = "You can do",
                                textScale = 42.sp,
                                outlineThickness = 14f,
                                outlineTint = Color.White,
                                glowPalette = listOf(Color(0xFFE71414), Color(0xFFE71414))
                            )
                            StrokeGlowTitle(
                                caption = "better!",
                                textScale = 42.sp,
                                outlineThickness = 14f,
                                outlineTint = Color.White,
                                glowPalette = listOf(Color(0xFFE71414), Color(0xFFE71414))
                            )
                        }
                    }
                }

                // ==== SCORE ====
                StrokeGlowTitle(
                    caption = "Score: ${result.score}",
                    textScale = 26.sp,
                    outlineThickness = 6f,
                    outlineTint = Color.Red,
                    glowPalette = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
                )

                // ==== КОИНЫ ====
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.graphicsLayer {
                            scaleX = coinBounce
                            scaleY = coinBounce
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(36.dp)
                                .graphicsLayer { rotationZ = coinRotation }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${result.coins}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        fontFamily = SeymourFont
                    )
                }

                // ==== КРОЛИК ====
                Image(
                    painter = painterResource(
                        id = if (isWin) R.drawable.rabbit_win else R.drawable.rabbit_lose
                    ),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .padding(top = 4.dp, bottom = 12.dp)
                            .graphicsLayer {
                                rotationZ = rabbitWiggle
                                alpha = rabbitAlpha
                            },
                    contentScale = ContentScale.Fit
                )

                // ==== КНОПКИ ====
                MenuActionButton(
                    text = "Try again",
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    height = 82.dp,
                    fontSize = 32.sp
                )

                MenuActionButton(
                    text = "Home",
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    height = 80.dp,
                    fontSize = 30.sp,
                    gradient = Brush.verticalGradient(
                        listOf(Color(0xFFE63A19), Color(0xFFC7140C))
                    )
                )
            }
        }
    }
}
