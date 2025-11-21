package com.rabbit.hit.ui.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R

val SeymourFont = FontFamily(Font(R.font.seymour_one_regular))

@Composable
fun MenuTitle(modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        GradientOutlinedText(
            text = "RABBIT",
            fontSize = 54.sp,
            strokeWidth = 12f,
            gradientColors =
                listOf(
                    Color(0xFFE86A17),
                    Color(0xFFE86A17)
                ),
            modifier = Modifier.offset(y = 10.dp)
        )
        GradientOutlinedText(
            text = "HIT",
            fontSize = 54.sp,
            strokeWidth = 12f,
            gradientColors = listOf(Color(0xFFE86A17), Color(0xFFE86A17)),
            modifier = Modifier.offset(y = (-20).dp)
        )
    }
}

@Composable
fun MenuCoinDisplay(amount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD35400))
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE67E22))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_coin),
                contentDescription = "Coins",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "$amount",
                color = Color.White,
                fontFamily = SeymourFont,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun MenuIconButton(
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFB04500))
                .padding(bottom = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE67E22), Color(0xFFD35400))
                    )
                )
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (iconRes != null) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun MenuStoreButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 100.dp, height = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFB04500))
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE67E22), Color(0xFFD35400))
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_store),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun MenuPlayButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MenuActionButton(
        text = "Play",
        onClick = onClick,
        modifier = modifier.fillMaxWidth(0.8f),
        height = 80.dp,
        fontSize = 36.sp
    )
}

@Composable
fun MenuActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 72.dp,
    fontSize: TextUnit = 30.sp,
    cornerRadius: Dp = 20.dp,
    gradient: Brush =
        Brush.verticalGradient(
            listOf(Color(0xFFE67E22), Color(0xFFD35400))
        ),
) {
    Box(
        modifier =
            modifier
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFFB04500))
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(gradient)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontFamily = SeymourFont,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SecondaryBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF612785))
                .padding(bottom = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFB367E0), Color(0xFF8D3CB2))
                    )
                )
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}
