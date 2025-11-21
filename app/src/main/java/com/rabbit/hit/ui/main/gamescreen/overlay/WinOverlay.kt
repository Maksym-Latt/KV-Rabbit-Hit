package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFEC56B), Color(0xFFF27524))
                    )
                )
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientOutlinedText(
                    text = if (isWin) "Well Done!" else "You can do better!",
                    fontSize = 32.sp,
                    strokeWidth = 8f,
                    strokeColor = Color(0xFFFFF2D4),
                    gradientColors = listOf(Color(0xFFE86A17), Color(0xFFE86A17))
                )
                Text(
                    text = "Score: ${result.score}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    fontFamily = SeymourFont
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "+${result.coins}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = SeymourFont
                    )
                }
                Image(
                    painter = painterResource(id = if (isWin) R.drawable.rabbit_win else R.drawable.rabbit_lose),
                    contentDescription = null,
                    modifier = Modifier
                        .size(220.dp)
                        .padding(vertical = 6.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(4.dp))
                MenuActionButton(
                    text = "Try again",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    height = 72.dp,
                    fontSize = 30.sp,
                )
                MenuActionButton(
                    text = "Home",
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    height = 70.dp,
                    fontSize = 28.sp,
                    gradient = Brush.verticalGradient(listOf(Color(0xFFE8601A), Color(0xFFD32F10)))
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}
