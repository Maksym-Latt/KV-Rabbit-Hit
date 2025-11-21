package com.rabbit.hit.ui.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PrimaryVariant { Orange, Yellow, PeachBack }

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, variant: PrimaryVariant = PrimaryVariant.Orange) {
    val colors = when (variant) {
        PrimaryVariant.Orange -> listOf(Color(0xFFFF9A3B), Color(0xFFFF7A1A))
        PrimaryVariant.Yellow -> listOf(Color(0xFFFFD25E), Color(0xFFFFB347))
        PrimaryVariant.PeachBack -> listOf(Color(0xFFFF8D5A), Color(0xFFEF6C3E))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(colors))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
    }
}

@Composable
fun OrangePrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryButton(text = text, onClick = onClick, modifier = modifier, variant = PrimaryVariant.Orange)
}

@Composable
fun StartPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryButton(text = text, onClick = onClick, modifier = modifier, variant = PrimaryVariant.Yellow)
}

@Composable
fun SecondaryIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x88000000))
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun SecondaryBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SecondaryIconButton(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        }
    }
}
