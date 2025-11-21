package com.rabbit.hit.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.component.MenuIconButton
import com.rabbit.hit.ui.main.component.SeymourFont
import com.rabbit.hit.ui.main.settings.SettingsViewModel

@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFEC56B), Color(0xFFF27524))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GradientOutlinedText(
                text = "Pause",
                fontSize = 32.sp,
                strokeWidth = 8f,
                strokeColor = Color(0xFFFFF2D4),
                gradientColors = listOf(Color(0xFFE86A17), Color(0xFFE86A17))
            )

            PauseToggleRow(
                title = "Sound",
                checked = ui.soundVolume > 0,
                onToggle = viewModel::toggleSound,
                iconOn = Icons.Default.VolumeUp,
                iconOff = Icons.Default.VolumeOff
            )

            PauseToggleRow(
                title = "Music",
                checked = ui.musicVolume > 0,
                onToggle = viewModel::toggleMusic,
                iconOn = Icons.Default.MusicNote,
                iconOff = Icons.Default.MusicOff
            )

            Spacer(modifier = Modifier.height(6.dp))

            MenuActionButton(
                text = "Resume",
                onClick = onResume,
                modifier = Modifier.fillMaxWidth(),
                height = 72.dp,
                fontSize = 28.sp
            )
            MenuActionButton(
                text = "Home",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                height = 68.dp,
                fontSize = 26.sp,
                gradient = Brush.verticalGradient(listOf(Color(0xFFE8601A), Color(0xFFD32F10)))
            )
            MenuActionButton(
                text = "Restart",
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                height = 64.dp,
                fontSize = 24.sp,
                gradient = Brush.verticalGradient(listOf(Color(0xFFB367E0), Color(0xFF8D3CB2)))
            )
        }
    }
}

@Composable
private fun PauseToggleRow(
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    iconOn: androidx.compose.ui.graphics.vector.ImageVector,
    iconOff: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier =
            Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable { onToggle(!checked) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = SeymourFont
        )
        MenuIconButton(
            iconVector = if (checked) iconOn else iconOff,
            onClick = { onToggle(!checked) },
            modifier = Modifier.size(56.dp)
        )
    }
}
