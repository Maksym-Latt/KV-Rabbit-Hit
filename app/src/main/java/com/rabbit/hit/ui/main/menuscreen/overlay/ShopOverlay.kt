package com.rabbit.hit.ui.main.menuscreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.rabbit.hit.data.progress.RabbitSkin
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuCoinDisplay
import com.rabbit.hit.ui.main.component.MenuIconButton
import com.rabbit.hit.ui.main.component.SeymourFont

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
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuIconButton(iconVector = Icons.Default.ArrowBack, onClick = onClose)
                MenuCoinDisplay(amount = coins, onClick = {})
            }

            GradientOutlinedText(
                text = "SHOP",
                fontSize = 36.sp,
                strokeWidth = 10f,
                strokeColor = Color.White,
                gradientColors = listOf(Color(0xFFF49C47), Color(0xFFE86A17))
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(skins) { skin ->
                    val isOwned = owned.contains(skin)
                    val isSelected = selected == skin

                    ShopItemCard(
                        skin = skin,
                        isOwned = isOwned,
                        isSelected = isSelected,
                        onEquip = { onSelect(skin) },
                        onBuy = { onBuy(skin) }
                    )
                }
            }
        }
    }
}

enum class ShopButtonType {
    EQUIPPED,
    EQUIP,
    BUY
}

@Composable
private fun ShopItemCard(
    skin: RabbitSkin,
    isOwned: Boolean,
    isSelected: Boolean,
    onEquip: () -> Unit,
    onBuy: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.35f))
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientOutlinedText(
                    text = skin.title.uppercase(),
                    fontSize = 30.sp,
                    strokeWidth = 8f,
                    strokeColor = Color.White,
                    gradientColors = listOf(Color(0xFF7A3CC9), Color(0xFF5A2EA0))
                )

                Text(
                    text = skin.description,
                    color = Color(0xFF5B2E8F),
                    fontFamily = SeymourFont,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Box(modifier = Modifier.width(170.dp)) {
                    when {
                        isSelected ->
                            ShopActionButton(
                                text = "Equipped",
                                type = ShopButtonType.EQUIPPED,
                                onClick = {}
                            )

                        isOwned ->
                            ShopActionButton(
                                text = "Equip",
                                type = ShopButtonType.EQUIP,
                                onClick = onEquip
                            )

                        else ->
                            ShopActionButton(
                                text = "${skin.price}",
                                type = ShopButtonType.BUY,
                                onClick = onBuy,
                                iconRes = R.drawable.ic_coin
                            )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = skin.previewRes),
                contentDescription = null,
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ShopActionButton(
    text: String,
    type: ShopButtonType,
    onClick: () -> Unit,
    iconRes: Int? = null
) {
    val backgroundColor =
        when (type) {
            ShopButtonType.EQUIPPED ->
                Brush.verticalGradient(
                    listOf(Color(0xFFDADADA), Color(0xFFA9A9A9))
                )

            ShopButtonType.EQUIP ->
                Brush.verticalGradient(
                    listOf(Color(0xFFE67E22), Color(0xFFD35400))
                )

            ShopButtonType.BUY ->
                Brush.verticalGradient(
                    listOf(Color(0xFF8BC34A), Color(0xFF689F38))
                )
        }

    val shadowColor =
        when (type) {
            ShopButtonType.EQUIPPED -> Color(0xFF808080)
            ShopButtonType.EQUIP -> Color(0xFFB04500)
            ShopButtonType.BUY -> Color(0xFF386716)
        }

    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(shadowColor)
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable(enabled = type != ShopButtonType.EQUIPPED, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

            val outlineColor =
                if (type == ShopButtonType.EQUIPPED) Color.Gray else Color.Transparent
            val textColor = when (type) {
                ShopButtonType.BUY -> Color(0xFFFFD54F)
                else -> Color.White
            }
            val gradients =
                when (type) {
                    ShopButtonType.BUY -> listOf(Color(0xFFFFF59D), Color(0xFFFFB74D))
                    else -> listOf(textColor, textColor)
                }

            GradientOutlinedText(
                text = text,
                fontSize = if (type == ShopButtonType.BUY) 22.sp else 20.sp,
                strokeWidth = 6f,
                strokeColor = if (type == ShopButtonType.BUY) Color(0xFF8B5A00) else outlineColor,
                gradientColors = gradients
            )
        }
    }
}
