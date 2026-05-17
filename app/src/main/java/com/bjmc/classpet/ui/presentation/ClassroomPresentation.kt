package com.bjmc.classpet.ui.presentation

import android.app.Presentation
import android.content.Context
import android.view.Display
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.ui.component.GrowthProgressBar
import com.bjmc.classpet.ui.component.PetAvatar
import com.bjmc.classpet.ui.theme.ClassPetTheme
import com.bjmc.classpet.util.GrowthCalculator

class ClassroomPresentation(
    private val outerContext: Context,
    display: Display
) : Presentation(outerContext, display) {

    var pet: PetEntity? = null
    var recentLogs: List<RewardLogEntity> = emptyList()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent()
    }

    fun update(pet: PetEntity?, logs: List<RewardLogEntity>) {
        this.pet = pet
        this.recentLogs = logs.take(5)
        if (isShowing) {
            setContent()
        }
    }

    private fun setContent() {
        setContentView(
            ComposeView(context).apply {
                setContent {
                    ClassPetTheme {
                        ClassroomContent(pet = pet, recentLogs = recentLogs)
                    }
                }
            }
        )
    }
}

@Composable
private fun ClassroomContent(
    pet: PetEntity?,
    recentLogs: List<RewardLogEntity>
) {
    val stage = pet?.let { GrowthCalculator.getStage(it.totalScore) }
        ?: com.bjmc.classpet.model.GrowthStage.EGG
    val type = pet?.let { PetType.valueOf(it.type) } ?: PetType.CAT
    val progress = pet?.let { GrowthCalculator.getStageProgress(it.totalScore) } ?: 0f
    val nextStage = GrowthCalculator.getNextStage(pet?.totalScore ?: 0)
    val scoreToNext = pet?.let { GrowthCalculator.getScoreToNextStage(it.totalScore) } ?: 50

    Row(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        // Left 60% — Pet display
        Column(
            modifier = Modifier.weight(0.6f).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PetAvatar(type = type, stage = stage, color = pet?.color ?: 0xFFFF9C9C, size = 240.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pet?.name ?: "未设置",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "${type.emoji} ${type.label} · ${stage.label}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Right 40% — Info panel
        Column(modifier = Modifier.weight(0.4f).fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "${pet?.totalScore ?: 0}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("班级总积分", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("成长进度", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    GrowthProgressBar(
                        currentStage = stage,
                        nextStage = nextStage,
                        progress = progress,
                        scoreToNext = scoreToNext
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("最近奖励", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(recentLogs) { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = log.label, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "+${log.score}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
