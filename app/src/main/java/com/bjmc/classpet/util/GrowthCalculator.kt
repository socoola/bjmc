package com.bjmc.classpet.util

import com.bjmc.classpet.model.GrowthStage

object GrowthCalculator {

    fun getStage(score: Int): GrowthStage = GrowthStage.fromScore(score)

    fun getNextStage(score: Int): GrowthStage? {
        val current = getStage(score)
        return GrowthStage.entries.getOrNull(current.ordinal + 1)
    }

    fun getStageProgress(score: Int): Float {
        val current = getStage(score)
        val next = getNextStage(score) ?: return 1f
        val range = next.threshold - current.threshold
        val progress = score - current.threshold
        return (progress.toFloat() / range).coerceIn(0f, 1f)
    }

    fun getScoreToNextStage(score: Int): Int {
        val next = getNextStage(score) ?: return 0
        return next.threshold - score
    }
}
