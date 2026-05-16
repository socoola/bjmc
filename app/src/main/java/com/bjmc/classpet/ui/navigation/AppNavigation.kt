package com.bjmc.classpet.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.ui.screen.PetHomeScreen
import com.bjmc.classpet.ui.screen.RewardHistoryScreen
import com.bjmc.classpet.ui.screen.RewardScreen

data class TabItem(val label: String, val icon: ImageVector)

val tabs = listOf(
    TabItem("萌宠", Icons.Default.Pets),
    TabItem("奖励", Icons.Default.Stars),
    TabItem("记录", Icons.Default.History)
)

@Composable
fun AppNavigation(
    pet: PetEntity?,
    presetRewards: List<PresetRewardEntity>,
    rewardLogs: List<RewardLogEntity>,
    onUpdateName: (String) -> Unit,
    onUpdateAppearance: (PetType, Long) -> Unit,
    onAddReward: (String, Int, String?) -> Unit,
    onUndoLastReward: () -> Unit,
    onEditPresets: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> PetHomeScreen(
                pet = pet,
                onUpdateName = onUpdateName,
                onUpdateAppearance = onUpdateAppearance,
                modifier = Modifier.padding(padding)
            )
            1 -> RewardScreen(
                presets = presetRewards,
                onAddReward = onAddReward,
                onEditPresets = onEditPresets,
                modifier = Modifier.padding(padding)
            )
            2 -> RewardHistoryScreen(
                logs = rewardLogs,
                onUndo = onUndoLastReward,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
