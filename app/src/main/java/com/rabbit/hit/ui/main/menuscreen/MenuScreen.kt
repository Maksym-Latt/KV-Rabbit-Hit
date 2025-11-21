package com.rabbit.hit.ui.main.menuscreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.PrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryVariant
import com.rabbit.hit.ui.main.component.SecondaryIconButton

@Composable
fun MenuScreen(
    state: MainViewModel.UiState,
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenShop: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "rabbit_float")
    val floatOffset = transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinBadge(amount = state.coins, onClick = onOpenShop)
                SecondaryIconButton(onClick = onOpenSettings, modifier = Modifier.size(52.dp)) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Image(
                painter = painterResource(id = state.selectedSkin.previewRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = floatOffset.value.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = R.drawable.central_ellipse),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 4.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Play",
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(58.dp),
                variant = PrimaryVariant.Orange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Best: ${state.bestScore}",
                color = Color(0xFF4A2C17),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CoinBadge(amount: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFE9F3D), Color(0xFFFFC45F))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.coin_placeholder),
            contentDescription = null,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = "$amount",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
    }
}
