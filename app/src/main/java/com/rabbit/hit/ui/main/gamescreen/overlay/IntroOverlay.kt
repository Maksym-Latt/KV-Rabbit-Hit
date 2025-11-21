package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.component.SeymourFont

@Composable
fun IntroOverlay(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFE0A3), Color(0xFFFFB85C))
                    )
                )
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GradientOutlinedText(
                text = "Ready to throw?",
                fontSize = 30.sp,
                strokeWidth = 8f,
                strokeColor = Color(0xFFFFF2D4),
                gradientColors = listOf(Color(0xFFE86A17), Color(0xFFE86A17))
            )

            Image(
                painter = painterResource(id = R.drawable.rabbit_1),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Tap to throw carrots into empty slots. Avoid collisions and chase the multiplier!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = SeymourFont,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            MenuActionButton(
                text = "Start",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                height = 70.dp
            )
        }
    }
}
