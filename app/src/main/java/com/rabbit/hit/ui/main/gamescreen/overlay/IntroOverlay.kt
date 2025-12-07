package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.StrokeGlowTitle
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.menuscreen.overlay.SettingsCard

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


            StrokeGlowTitle(
                caption = "Tap to throw carrots",
                textScale = 18.sp,
                outlineThickness = 6f,
                outlineTint =  Color(0xff873e02),
                glowPalette = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
            )
            StrokeGlowTitle(
                caption = "and avoid collisions!",
                textScale = 18.sp,
                outlineThickness = 6f,
                outlineTint =  Color(0xff873e02),
                glowPalette = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
            )

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
