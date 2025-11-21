package com.rabbit.hit.ui.main.menuscreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.SecondaryBackButton
import com.rabbit.hit.ui.main.settings.SettingsViewModel

@Composable
fun SettingsOverlay(
    onClose: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    val panelShape = RoundedCornerShape(26.dp)

    val panelGradient = Brush.verticalGradient(
        listOf(
            Color(0xff78318a),
            Color(0xffb02d87),
            Color(0xffd57aa1)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99aa9393))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() }
    ) {
        // ---------- BACK BUTTON ---------- //
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(start = 20.dp, top = 24.dp)
                .size(58.dp)
        )

        // ---------- CENTER PANEL ---------- //
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.78f)
                .clip(panelShape)
                .background(panelGradient)
                .padding(horizontal = 28.dp, vertical = 24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                GradientOutlinedText(
                    text = "SETTINGS",
                    fontSize = 30.sp,
                    gradientColors = listOf(Color.White, Color.White)
                )

                ToggleRow(
                    title = "MUSIC",
                    checked = ui.musicVolume > 0,
                    onCheckedChange = viewModel::toggleMusic
                )
                ToggleRow(
                    title = "SOUNDS",
                    checked = ui.soundVolume > 0,
                    onCheckedChange = viewModel::toggleSound
                )
                ToggleRow(
                    title = "VIBRATION",
                    checked = ui.vibrationEnabled,
                    onCheckedChange = viewModel::toggleVibration
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFF4CA),
                checkedTrackColor = Color(0xFFFF9DD0),
                uncheckedThumbColor = Color(0xFFE8E0FF),
                uncheckedTrackColor = Color(0xFF9D75C5)
            )
        )
    }
}
