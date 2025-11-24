package com.rabbit.hit.ui.main.gamescreen.overlay

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rabbit.hit.ui.main.component.AutoSizeGradientOutlinedText
import com.rabbit.hit.ui.main.gamescreen.GameResult
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.component.SeymourFont

@Composable
fun WinOverlay(
    result: GameResult,
    isWin: Boolean = false,
    onRetry: () -> Unit,
    onHome: () -> Unit,
) {
    // Тёмный фон
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

// ==== ЗАГОЛОВОК ====
            if (isWin) {
                GradientOutlinedText(
                    text = "Well Done!",
                    fontSize = 42.sp,
                    strokeWidth = 14f,
                    strokeColor = Color.White,
                    gradientColors = listOf(Color(0xFFE71414), Color(0xFFE71414))
                )
            } else {
                GradientOutlinedText(
                    text = "You can do",
                    fontSize = 42.sp,
                    strokeWidth = 14f,
                    strokeColor = Color.White,
                    gradientColors = listOf(Color(0xFFE71414), Color(0xFFE71414))
                )
                GradientOutlinedText(
                    text = "better!",
                    fontSize = 42.sp,
                    strokeWidth = 14f,
                    strokeColor = Color.White,
                    gradientColors = listOf(Color(0xFFE71414), Color(0xFFE71414))
                )
            }


            // ==== SCORE ====
            GradientOutlinedText(
                text = "Score: ${result.score}",
                fontSize = 26.sp,
                strokeWidth = 6f,
                strokeColor = Color.Red,
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
            )

            // ==== КОИНЫ ====
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_coin),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
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
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .padding(top = 4.dp, bottom = 12.dp),
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
