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
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuActionButton
import com.rabbit.hit.ui.main.component.MenuIconButton
import com.rabbit.hit.ui.main.component.SeymourFont
import com.rabbit.hit.ui.main.menuscreen.overlay.SettingToggleRow
import com.rabbit.hit.ui.main.menuscreen.overlay.SettingsCard
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
        SettingsCard(
            title = "Pause",
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {

            SettingToggleRow(
                title = "Sound",
                checked = ui.soundVolume > 0,
                onToggle = viewModel::toggleSound,
                iconOnRes = R.drawable.volume,
                iconOffRes = R.drawable.muted
            )

            SettingToggleRow(
                title = "Music",
                checked = ui.musicVolume > 0,
                onToggle = viewModel::toggleMusic,
                iconOnRes = R.drawable.volume,
                iconOffRes = R.drawable.muted
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