package com.bjmc.classpet.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PresetRewardEntity

@Composable
fun PresetRewardEditor(
    presets: List<PresetRewardEntity>,
    onSave: (List<PresetRewardEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    val editablePresets = remember { mutableStateListOf<PresetRewardEntity>().also { it.addAll(presets) } }
    var showAddForm by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("") }
    var newScore by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "编辑快捷奖励", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(editablePresets) { index, preset ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${preset.icon} ${preset.label}  +${preset.score}")
                        IconButton(onClick = { editablePresets.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            }
        }

        if (editablePresets.size < 12 && showAddForm) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newIcon,
                onValueChange = { if (it.length <= 2) newIcon = it },
                label = { Text("图标 (emoji)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newLabel,
                onValueChange = { newLabel = it },
                label = { Text("名称") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newScore,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) newScore = it },
                label = { Text("分数") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(onClick = {
                    val score = newScore.toIntOrNull()
                    if (newLabel.isNotBlank() && newIcon.isNotBlank() && score != null && score > 0) {
                        editablePresets.add(
                            PresetRewardEntity(
                                label = newLabel, icon = newIcon, score = score,
                                color = 0xFF6366F1, sortOrder = editablePresets.size
                            )
                        )
                        newLabel = ""; newIcon = ""; newScore = ""
                        showAddForm = false
                    }
                }) { Text("添加") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddForm = false; newLabel = ""; newIcon = ""; newScore = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("取消") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (editablePresets.size < 12 && !showAddForm) {
                Button(onClick = { showAddForm = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("添加预设")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            Row {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("取消") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onSave(editablePresets.mapIndexed { index, p -> p.copy(sortOrder = index) })
                }) { Text("保存") }
            }
        }
    }
}
