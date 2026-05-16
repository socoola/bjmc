package com.bjmc.classpet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.model.presetColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetSetupWizard(
    onComplete: (name: String, type: PetType, color: Long) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var selectedType by remember { mutableStateOf(PetType.CAT) }
    var selectedColor by remember { mutableStateOf(presetColors[0].hex) }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "创建班级萌宠", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (step) {
                0 -> "选择萌宠类型"
                1 -> "选择主色调"
                2 -> "给萌宠起个名字"
                else -> ""
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            0 -> {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PetType.entries.forEach { type ->
                        val selected = type == selectedType
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedType = type }
                                .padding(12.dp)
                        ) {
                            Text(text = type.emoji, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                            Text(text = type.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            1 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    presetColors.forEach { pc ->
                        val selected = pc.hex == selectedColor
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(pc.hex))
                                .then(
                                    if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColor = pc.hex }
                        )
                    }
                }
            }
            2 -> {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 6) name = it },
                    label = { Text("萌宠名字（最多6个字）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                Button(onClick = { step-- }) { Text("上一步") }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            Button(
                onClick = { if (step < 2) step++ else onComplete(name, selectedType, selectedColor) },
                enabled = step != 2 || name.isNotBlank()
            ) {
                Text(if (step < 2) "下一步" else "完成")
            }
        }
    }
}
