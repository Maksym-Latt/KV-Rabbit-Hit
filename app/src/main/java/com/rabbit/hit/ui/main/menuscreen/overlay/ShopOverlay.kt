package com.rabbit.hit.ui.main.menuscreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Home
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
import com.rabbit.hit.ui.main.menuscreen.MenuCoinDisplay
import com.rabbit.hit.ui.main.menuscreen.MenuIconButton
import com.rabbit.hit.ui.main.menuscreen.SeymourFont

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
    Box(
    ){
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Left Content
            Column(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    GradientOutlinedText(
                            text = skin.title,
                            fontSize = 32.sp,
                            strokeWidth = 8f,
                            strokeColor = Color.White,
                            gradientColors = listOf(Color.White, Color.White),
                            modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                            text = skin.description,
                            color = Color.White,
                            fontFamily = SeymourFont,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold
                    )
                }

                // Action Button
                Box(modifier = Modifier.width(160.dp)) {
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

            // Right Content (Rabbit Image)
            Box(
                    modifier = Modifier.fillMaxSize().weight(0.8f),
                    contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                        painter = painterResource(id = skin.previewRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.BottomEnd
                )
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
                                listOf(Color(0xFFD3D3D3), Color(0xFFA9A9A9))
                        ) // Silver/Grey
                ShopButtonType.EQUIP ->
                        Brush.verticalGradient(
                                listOf(Color(0xFFE67E22), Color(0xFFD35400))
                        ) // Orange
                ShopButtonType.BUY ->
                        Brush.verticalGradient(
                                listOf(Color(0xFF76FF03), Color(0xFF64DD17))
                        ) // Green
            }

    val shadowColor =
            when (type) {
                ShopButtonType.EQUIPPED -> Color(0xFF808080)
                ShopButtonType.EQUIP -> Color(0xFFB04500)
                ShopButtonType.BUY -> Color(0xFF33691E)
            }

    val textColor = if (type == ShopButtonType.EQUIPPED) Color.White else Color.White
    val outlineColor =
            if (type == ShopButtonType.EQUIPPED) Color.Gray
            else Color.Transparent // Maybe outline for equipped text?

    Box(
            modifier =
                    Modifier.height(48.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(shadowColor)
                            .padding(bottom = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .clickable(enabled = type != ShopButtonType.EQUIPPED, onClick = onClick)
                            .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (iconRes != null) {
                Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                )
            }

            if (type == ShopButtonType.EQUIPPED) {
                GradientOutlinedText(
                        text = text,
                        fontSize = 20.sp,
                        strokeWidth = 6f,
                        strokeColor = Color.Gray,
                        gradientColors = listOf(Color.White, Color.White)
                )
            } else {
                GradientOutlinedText(
                        text = text,
                        fontSize = 24.sp,
                        strokeWidth = 6f,
                        strokeColor =
                                if (type == ShopButtonType.BUY) Color(0xFF33691E)
                                else Color(0xFF8D4004), // Darker outline for contrast
                        gradientColors = listOf(Color.White, Color.White)
                )
            }
        }
    }
}
