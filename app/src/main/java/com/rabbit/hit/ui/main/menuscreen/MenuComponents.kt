package com.rabbit.hit.ui.main.menuscreen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText

@Composable
fun MenuTitle(modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        GradientOutlinedText(
                text = "RABBIT",
                fontSize = 64.sp,
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
                fontSize = 64.sp,
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
                    modifier.clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFD35400))
                            .padding(bottom = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE67E22))
                            .clickable(onClick = onClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                    painter =
                            painterResource(
                                    id = R.drawable.coin_placeholder
                            ),

                    contentDescription = "Coins",
                    modifier = Modifier.size(24.dp)
            )
            Text(
                    text = "$amount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
            )
        }
    }
}

@Composable
fun MenuIconButton(
        iconRes: Int? = null,
        iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(
            modifier =
                    modifier.size(56.dp)
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
            Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.White,
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
fun MenuPlayButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
            modifier =
                    modifier.fillMaxWidth(0.8f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFB04500))
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                    Brush.verticalGradient(
                                            listOf(Color(0xFFE67E22), Color(0xFFD35400))
                                    )
                            )
                            .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
    ) {
        Text(
                text = "Play",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,

                )
    }
}
