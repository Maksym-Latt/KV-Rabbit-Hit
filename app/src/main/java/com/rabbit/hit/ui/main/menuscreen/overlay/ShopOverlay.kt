package com.rabbit.hit.ui.main.menuscreen.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.data.progress.RabbitSkin
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.OrangePrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryVariant
import com.rabbit.hit.ui.main.component.SecondaryBackButton
import com.rabbit.hit.ui.main.component.StartPrimaryButton

@Composable
fun ShopOverlay(
    skins: List<RabbitSkin>,
    owned: Set<RabbitSkin>,
    selected: RabbitSkin,
    coins: Int,
    onClose: () -> Unit,
    onSelect: (RabbitSkin) -> Unit,
    onBuy: (RabbitSkin) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = skins.indexOf(selected).coerceAtLeast(0)) { skins.size }
    LaunchedEffect(selected) {
        val index = skins.indexOf(selected)
        if (index >= 0) pagerState.scrollToPage(index)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3C6AC8), Color(0xFF6BC1FF), Color(0xFFFFC986))
                )
            ),
    ) {
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(start = 18.dp, top = 18.dp)
                .size(52.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GradientOutlinedText(text = "Shop", fontSize = 30.sp, gradientColors = listOf(Color.White, Color.White))

            ShopCoinsBadge(coins = coins, modifier = Modifier.align(Alignment.End))

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp,
                modifier = Modifier.weight(1f)
            ) { page ->
                val skin = skins[page]
                ShopItemCard(
                    skin = skin,
                    owned = owned.contains(skin),
                    selected = selected == skin,
                    coins = coins,
                    onBuy = { onBuy(skin) },
                    onSelect = { onSelect(skin) }
                )
            }

            PagerIndicator(count = skins.size, current = pagerState.currentPage)

            PrimaryButton(
                text = "Back",
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(54.dp),
                variant = PrimaryVariant.PeachBack
            )
        }
    }
}

@Composable
private fun ShopItemCard(
    skin: RabbitSkin,
    owned: Boolean,
    selected: Boolean,
    coins: Int,
    onBuy: () -> Unit,
    onSelect: () -> Unit,
) {
    var showNotEnough by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0x44FFFFFF), Color(0x22FFFFFF))))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = skin.previewRes),
            contentDescription = null,
            modifier = Modifier.size(140.dp),
            contentScale = ContentScale.Fit
        )
        Text(text = skin.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            text = skin.description,
            color = Color(0xFFEFF6FF),
            fontSize = 13.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.coin_placeholder), contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "${skin.price}", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        val action: () -> Unit = {
            when {
                selected -> showNotEnough = false
                owned -> {
                    showNotEnough = false
                    onSelect()
                }
                coins >= skin.price -> {
                    showNotEnough = false
                    onBuy()
                }
                else -> showNotEnough = true
            }
        }

        when {
            selected -> StartPrimaryButton(text = "Selected", onClick = action, modifier = Modifier.fillMaxWidth())
            owned -> OrangePrimaryButton(text = "Equip", onClick = action, modifier = Modifier.fillMaxWidth())
            else -> OrangePrimaryButton(text = "Buy", onClick = action, modifier = Modifier.fillMaxWidth())
        }

        AnimatedVisibility(visible = showNotEnough) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCCB23B3B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Need ${skin.price - coins} more coins",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PagerIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(count) { index ->
            val isActive = current == index
            Box(
                modifier = Modifier
                    .size(if (isActive) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.White else Color(0x55FFFFFF))
            )
        }
    }
}

@Composable
private fun ShopCoinsBadge(coins: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.coin_placeholder), contentDescription = null, modifier = Modifier.size(18.dp))
        Text(text = "$coins", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
