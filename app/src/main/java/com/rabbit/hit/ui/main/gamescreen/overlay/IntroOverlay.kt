package com.rabbit.hit.ui.main.gamescreen.overlay

import android.R.attr.strokeColor
import android.R.attr.strokeWidth
import android.R.attr.text
import android.R.attr.textColor
import android.R.attr.type
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.rabbit.hit.ui.main.menuscreen.overlay.SettingsCard
import com.rabbit.hit.ui.main.menuscreen.overlay.ShopButtonType

@Composable
fun IntroOverlay(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {

        SettingsCard(
            title = "Ready?",
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            // Иконка
            Image(
                painter = painterResource(id = R.drawable.rabbit_win),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )


            GradientOutlinedText(
                text = "Tap to throw carrots",
                fontSize = 18.sp,
                strokeWidth = 6f,
                strokeColor =  Color(0xff873e02),
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
            )
            GradientOutlinedText(
                text = "and avoid collisions!",
                fontSize = 18.sp,
                strokeWidth = 6f,
                strokeColor =  Color(0xff873e02),
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
            )

            // Кнопка
            MenuActionButton(
                text = "Start",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 26.sp,
                height = 64.dp
            )
        }
    }
}
