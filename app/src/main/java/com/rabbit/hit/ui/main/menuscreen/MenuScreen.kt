package com.rabbit.hit.ui.main.menuscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rabbit.hit.R

@Composable
fun MenuScreen(
        state: MainViewModel.UiState,
        onStartGame: () -> Unit,
        onOpenSettings: () -> Unit,
        onOpenShop: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
        )

        Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
        ) {
            // Top Bar
            Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                MenuCoinDisplay(amount = state.coins, onClick = onOpenShop)
                MenuIconButton(iconVector = Icons.Default.Settings, onClick = onOpenSettings)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            MenuTitle()

            Spacer(modifier = Modifier.weight(1f))

            // Rabbit Image (Centered in available space)
            Image(
                    painter = painterResource(id = state.selectedSkin.previewRes),
                    contentDescription = null,
                    modifier =
                            Modifier.fillMaxWidth(0.8f)
                                    .weight(
                                            3f
                                    ), // Give it weight to take up space but not push buttons off
                    contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Buttons
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
            ) {
                // Skins Button (Hanger Icon)
                MenuIconButton(
                        iconRes = R.drawable.ic_launcher_foreground,
                        iconVector = androidx.compose.material.icons.Icons.Default.Face,
                        onClick = onOpenShop
                )

                MenuPlayButton(onClick = onStartGame)
            }
        }
    }
}
