package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.model.presetColors
import com.bjmc.classpet.ui.component.GrowthProgressBar
import com.bjmc.classpet.ui.component.PetAvatar
import com.bjmc.classpet.util.GrowthCalculator

@Composable
fun PetHomeScreen(
    pet: PetEntity?,
    onUpdateName: (String) -> Unit,
    onUpdateAppearance: (PetType, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameEdit by remember { mutableStateOf(false) }
    var showAppearanceEdit by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(pet?.name ?: "") }

    val stage = pet?.let { GrowthCalculator.getStage(it.totalScore) } ?: GrowthStage.EGG
    val type = pet?.let { PetType.valueOf(it.type) } ?: PetType.CAT
    val progress = pet?.let { GrowthCalculator.getStageProgress(it.totalScore) } ?: 0f
    val nextStage = GrowthCalculator.getNextStage(pet?.totalScore ?: 0)
    val scoreToNext = pet?.let { GrowthCalculator.getScoreToNextStage(it.totalScore) } ?: 50

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PetAvatar(type = type, stage = stage, color = pet?.color ?: 0xFFFF9C9C, size = 160.dp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = pet?.name ?: "未设置", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "${type.emoji} ${type.label} · ${stage.label}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { editName = pet?.name ?: ""; showNameEdit = true }) {
                Text("改名")
            }
            Button(onClick = { showAppearanceEdit = true }) {
                Text("换外观")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${pet?.totalScore ?: 0} 分",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                GrowthProgressBar(
                    currentStage = stage,
                    nextStage = nextStage,
                    progress = progress,
                    scoreToNext = scoreToNext
                )
            }
        }
    }

    // Name edit dialog
    if (showNameEdit) {
        AlertDialog(
            onDismissRequest = { showNameEdit = false },
            title = { Text("改名字") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { if (it.length <= 6) editName = it },
                    label = { Text("萌宠名字（最多6个字）") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (editName.isNotBlank()) { onUpdateName(editName); showNameEdit = false }
                }) { Text("确定") }
            },
            dismissButton = {
                Button(onClick = { showNameEdit = false }) { Text("取消") }
            }
        )
    }

    // Appearance edit dialog
    if (showAppearanceEdit) {
        var selType by remember { mutableStateOf(type) }
        var selColor by remember { mutableStateOf(pet?.color ?: 0xFFFF9C9C) }

        AlertDialog(
            onDismissRequest = { showAppearanceEdit = false },
            title = { Text("换外观") },
            text = {
                Column {
                    Text("选择类型", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PetType.entries.forEach { t ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        2.dp,
                                        if (t == selType) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        MaterialTheme.shapes.small
                                    )
                                    .clickable { selType = t }
                                    .padding(8.dp)
                            ) {
                                Text(text = t.emoji, style = MaterialTheme.typography.headlineMedium)
                                Text(text = t.label, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("选择颜色", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        presetColors.forEach { pc ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(pc.hex))
                                    .then(
                                        if (pc.hex == selColor) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { selColor = pc.hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onUpdateAppearance(selType, selColor); showAppearanceEdit = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                Button(onClick = { showAppearanceEdit = false }) { Text("取消") }
            }
        )
    }
}
