package com.bjmc.classpet.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PresetRewardEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RewardScreen(
    presets: List<PresetRewardEntity>,
    onAddReward: (String, Int, String?) -> Unit,
    onEditPresets: () -> Unit,
    modifier: Modifier = Modifier
) {
    var customScore by remember { mutableStateOf("") }
    var customNote by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("快捷奖励", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Button(
                    onClick = { onAddReward(preset.label, preset.score, null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(preset.color))
                ) {
                    Text("${preset.icon} ${preset.label} +${preset.score}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onEditPresets) { Text("编辑预设") }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("自定义奖励", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = customScore,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 3) customScore = it
                        },
                        label = { Text("分数") },
                        singleLine = true,
                        modifier = Modifier.width(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = customNote,
                        onValueChange = { customNote = it },
                        label = { Text("备注（可选）") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val score = customScore.toIntOrNull()
                            if (score != null && score > 0) {
                                onAddReward("自定义", score, customNote.ifBlank { null })
                                customScore = ""
                                customNote = ""
                            }
                        },
                        enabled = customScore.isNotBlank()
                    ) { Text("发放") }
                }
            }
        }
    }
}
