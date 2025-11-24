package com.rabbit.hit.ui.main.menuscreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rabbit.hit.R
import com.rabbit.hit.ui.main.component.GradientOutlinedText
import com.rabbit.hit.ui.main.component.MenuIconButton
import com.rabbit.hit.ui.main.component.SeymourFont
import com.rabbit.hit.ui.main.menuscreen.overlay.ShopButtonType.EQUIP
import com.rabbit.hit.ui.main.settings.SettingsViewModel

// ================= SETTINGS WINDOW WITH FLOATING TITLE =================

@Composable
fun SettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidthPx = 3.dp.toPx()
                    val halfStroke = strokeWidthPx / 2

                    drawRoundRect(
                        color = Color(0xff552a00),
                        topLeft = Offset(halfStroke, halfStroke),
                        size = Size(
                            size.width - strokeWidthPx,
                            size.height - strokeWidthPx
                        ),
                        cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                        style = Stroke(width = strokeWidthPx)
                    )
                }
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFF49C47).copy(alpha = 0.90f))
                .padding(
                    top = 55.dp,
                    bottom = 32.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )

        // ---- Заголовок строго по линии ----
        GradientOutlinedText(
            text = title,
            fontSize = 42.sp,
            strokeWidth = 40f,
            strokeColor = Color(0xffffffff),
            gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFF9800)),
            modifier = Modifier
                .offset(y = (-22).dp)
        )
    }
}



@Composable
fun SettingsOverlay(
    onClose: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                MenuIconButton(
                    iconVector = Icons.Default.ArrowBack,
                    onClick = onClose
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                SettingsCard(
                    title = "Settings",
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    SettingToggleRow(
                        title = "Sound",
                        checked = ui.soundVolume > 0,
                        onToggle = viewModel::toggleSound,
                        iconOn = Icons.Default.VolumeUp,
                        iconOff = Icons.Default.VolumeOff
                    )

                    SettingToggleRow(
                        title = "Music",
                        checked = ui.musicVolume > 0,
                        onToggle = viewModel::toggleMusic,
                        iconOn = Icons.Default.VolumeUp,
                        iconOff = Icons.Default.VolumeOff
                    )
                }
            }
        }
    }
}


@Composable
public fun SettingToggleRow(
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    iconOn: androidx.compose.ui.graphics.vector.ImageVector,
    iconOff: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggle(!checked) }
           ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color(0xffffffff),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SeymourFont
        )

        MenuIconButton(
            iconVector = if (checked) iconOn else iconOff,
            onClick = { onToggle(!checked) },
            modifier = Modifier.graphicsLayer(alpha = if (checked) 1f else 0.85f)
        )
    }
}