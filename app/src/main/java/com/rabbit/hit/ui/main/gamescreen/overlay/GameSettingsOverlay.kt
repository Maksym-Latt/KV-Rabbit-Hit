package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.OrangePrimaryButton
import com.rabbit.hit.ui.main.component.SecondaryBackButton

@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRetry: () -> Unit,   // оставляем в сигнатуре, но больше не используем
    onHome: () -> Unit,
) {
    // ---------- форма и фон карточки ----------
    val cardShape = RoundedCornerShape(26.dp)
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
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onResume()
            }
    ) {
        // ---------- кнопка "назад" сверху слева ----------
        SecondaryBackButton(
            onClick = onResume,
            modifier = Modifier
                .padding(start = 20.dp, top = 24.dp)
                .size(60.dp)
        )

        // ---------- центральная панель паузы ----------
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f)
                .clip(cardShape)
                .background(panelGrad)
                .padding(vertical = 22.dp, horizontal = 20.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ---------- заголовок ----------
                GradientOutlinedText(
                    text = "Pause",
                    fontSize = 32.sp,
                    gradientColors = listOf(Color.White, Color.White)
                )

                // ---------- кнопка Resume (оранжевая) ----------
                OrangePrimaryButton(
                    text = "Resume",
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                )

                // ---------- кнопка Menu (персиковая) ----------
                OrangePrimaryButton(
                    text = "Menu",
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                )
            }
        }
    }
}
