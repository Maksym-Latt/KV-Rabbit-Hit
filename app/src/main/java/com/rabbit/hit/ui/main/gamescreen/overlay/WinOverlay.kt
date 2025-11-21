package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.OrangePrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryButton
import com.rabbit.hit.ui.main.component.PrimaryVariant
import com.rabbit.hit.ui.main.gamescreen.GameResult

// ----------------------- Win overlay -----------------------
@Composable
fun WinOverlay(
    result: GameResult,
    onNextLevel: () -> Unit,
    onHome: () -> Unit,
) {
    ResultOverlayContainer {

        Spacer(modifier = Modifier.weight(1f))
        // -------- верхній блок (YOU WIN!) --------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultTitle(text = "YOU WIN!", background = Color(0xFFFFDCC9))
        }

        Spacer(modifier = Modifier.weight(0.5f))
        // -------- середній блок (курча + монетки + нагорода) --------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WinChickenWithCoins(
                chickSize = 140.dp,
                coinSize = 26.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResultStat(
                label = "+${result.coins}",
                iconRes = R.drawable.coin,
                background = Color(0xFFFFE8D4),
                contentColor = Color(0xFFF28E3A)
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
        // -------- нижній блок (кнопки) --------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OrangePrimaryButton(
                text = "TRY AGAIN",
                onClick = onNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "MENU",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                variant = PrimaryVariant.PeachBack
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}


// ----------------------- Game over overlay -----------------------
@Composable
fun GameOverOverlay(
    result: GameResult,
    targetCoins: Int,
    onRetry: () -> Unit,
    onHome: () -> Unit,
) {
    ResultOverlayContainer {
        Spacer(modifier = Modifier.weight(1f))
        // -------- верхній блок (сердечко + YOU FELL!) --------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameOverHeader()
        }
        // -------- середній блок (Coins / Height + підказка) --------
        Spacer(modifier = Modifier.weight(0.5f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameOverStatsPanel(
                coins = result.coins,
                height = result.height
            )

            Spacer(modifier = Modifier.height(12.dp))

            GoalReminder(targetCoins = targetCoins)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        // -------- нижній блок (кнопки) --------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OrangePrimaryButton(
                text = "TRY AGAIN",
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "MENU",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                variant = PrimaryVariant.PeachBack
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

// ----------------------- Fullscreen container with background -----------------------
@Composable
private fun ResultOverlayContainer(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // фон гри на весь екран
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

// ----------------------- Common UI building blocks -----------------------

@Composable
private fun ResultTitle(text: String, background: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        GradientOutlinedText(
            text = text,
            fontSize = 28.sp,
            gradientColors = listOf(Color(0xFFFFB15C), Color(0xFFFFB15C))
        )
    }
}

@Composable
private fun ResultStat(
    label: String,
    iconRes: Int,
    background: Color,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            color = contentColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun GoalReminder(targetCoins: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFF2E6), Color(0xFFFFE0C8))
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Goal: $targetCoins coins",
            color = Color(0xFF7B4A2D),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

// ----------------------- Win: chicken with coins -----------------------

@Composable
private fun WinChickenWithCoins(
    chickSize: Dp,
    coinSize: Dp,
) {
    Box(
        modifier = Modifier
            .size(chickSize * 1.4f),
        contentAlignment = Alignment.Center
    ) {
        val coinPainter = painterResource(id = R.drawable.coin)

        // верх
        Image(
            painter = coinPainter,
            contentDescription = null,
            modifier = Modifier
                .size(coinSize)
                .align(Alignment.TopCenter)
                .offset(y = 4.dp),
            contentScale = ContentScale.Fit
        )

        // ліво-верх
        Image(
            painter = coinPainter,
            contentDescription = null,
            modifier = Modifier
                .size(coinSize)
                .align(Alignment.TopStart)
                .offset(x = 10.dp, y = 18.dp),
            contentScale = ContentScale.Fit
        )

        // право-верх
        Image(
            painter = coinPainter,
            contentDescription = null,
            modifier = Modifier
                .size(coinSize)
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = 18.dp),
            contentScale = ContentScale.Fit
        )

        // ліво-низ
        Image(
            painter = coinPainter,
            contentDescription = null,
            modifier = Modifier
                .size(coinSize)
                .align(Alignment.CenterStart)
                .offset(x = 4.dp, y = 18.dp),
            contentScale = ContentScale.Fit
        )

        // право-низ
        Image(
            painter = coinPainter,
            contentDescription = null,
            modifier = Modifier
                .size(coinSize)
                .align(Alignment.CenterEnd)
                .offset(x = (-4).dp, y = 18.dp),
            contentScale = ContentScale.Fit
        )

        // курча по центру
        Image(
            painter = painterResource(id = R.drawable.title_chicken),
            contentDescription = null,
            modifier = Modifier.size(chickSize),
            contentScale = ContentScale.Fit
        )
    }
}

// ----------------------- Game over header (heart + title) -----------------------

@Composable
private fun GameOverHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "💔",
            fontSize = 40.sp
        )

        ResultTitle(text = "YOU FELL!", background = Color(0xFFFFD6CF))
    }
}

// ----------------------- Game over stats panel (two columns) -----------------------

@Composable
private fun GameOverStatsPanel(
    coins: Int,
    height: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFFEBDC))
            .padding(vertical = 14.dp, horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),   // ← важливо: висота = по контенту
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Coins",
                        color = Color(0xFFB0642D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = coins.toString(),
                    color = Color(0xFFF28E3A),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxHeight()          // ← тепер це висота Row, а не всього екрана
                    .width(1.dp),
                color = Color(0xFFFFD0B4).copy(alpha = 0.8f)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.platform),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Height",
                        color = Color(0xFF7B4A2D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$height m",
                    color = Color(0xFF7B4A2D),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ----------------------- PREVIEWS -----------------------

@Preview(
    name = "WIN OVERLAY",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)

@Preview(
    name = "WIN OVERLAY",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun PreviewWinOverlay() {
    WinOverlay(
        result = GameResult(
            coins = 250,
            height = 0,
            level = 3,
            targetCoins = 200,
            hasWon = true,
            finished = true
        ),
        onNextLevel = {},
        onHome = {}
    )
}

@Preview(
    name = "GAME OVER",
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun PreviewGameOverOverlay() {
    GameOverOverlay(
        result = GameResult(
            coins = 201,
            height = 155,
            level = 3,
            targetCoins = 300,
            hasWon = false,
            finished = true
        ),
        targetCoins = 300,
        onRetry = {},
        onHome = {}
    )
}