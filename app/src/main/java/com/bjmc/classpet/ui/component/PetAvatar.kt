package com.bjmc.classpet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType

@Composable
fun PetAvatar(
    type: PetType,
    stage: GrowthStage,
    color: Long,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val stageScale = when (stage) {
        GrowthStage.EGG -> 0.6f
        GrowthStage.BABY -> 0.75f
        GrowthStage.TEEN -> 0.85f
        GrowthStage.ADULT -> 0.95f
        GrowthStage.MATURE -> 1.1f
    }

    Box(
        modifier = modifier
            .size(size * stageScale)
            .clip(CircleShape)
            .background(Color(color).copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.emoji,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
