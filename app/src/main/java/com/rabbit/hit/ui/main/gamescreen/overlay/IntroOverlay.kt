package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.StartPrimaryButton

@Composable
fun IntroOverlay(
    level: Int,
    targetCoins: Int,
    onStart: () -> Unit,
) {
    val panelShape = RoundedCornerShape(22.dp)
    val panelGrad = Brush.verticalGradient(
        listOf(
            Color(0xff78318a),
            Color(0xffb02d87),
            Color(0xffd57aa1)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .widthIn(max = 360.dp)
                .clip(panelShape)
                .background(panelGrad)
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GradientOutlinedText(
                    text = "READY TO JUMP?",
                    fontSize = 26.sp,
                    gradientColors = listOf(Color.White, Color.White)
                )

                Image(
                    painter = painterResource(id = R.drawable.egg_2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(92.dp)
                        .padding(top = 2.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Level $level goal: collect $targetCoins coins.\nBounce between clouds and don't fall!",
                    color = Color(0xFFFFF4E7),
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                StartPrimaryButton(
                    text = "PLAY",
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}
