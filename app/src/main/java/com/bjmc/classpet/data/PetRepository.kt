package com.bjmc.classpet.data

import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.GrowthStage
import kotlinx.coroutines.flow.Flow

class PetRepository(private val db: AppDatabase) {

    private val petDao = db.petDao()
    private val rewardLogDao = db.rewardLogDao()
    private val presetRewardDao = db.presetRewardDao()

    val pet: Flow<PetEntity?> = petDao.observe()
    val rewardLogs: Flow<List<RewardLogEntity>> = rewardLogDao.observeAll()
    val presetRewards: Flow<List<PresetRewardEntity>> = presetRewardDao.observeAll()

    suspend fun getPet(): PetEntity? = petDao.get()

    suspend fun savePet(pet: PetEntity) {
        petDao.upsert(pet)
    }

    suspend fun addReward(label: String, score: Int, note: String? = null) {
        val log = RewardLogEntity(label = label, score = score, note = note)
        rewardLogDao.insert(log)
        val pet = getPet() ?: return
        val newScore = pet.totalScore + score
        val newStage = GrowthStage.fromScore(newScore)
        petDao.updateScoreAndStage(newScore, newStage.name)
    }

    suspend fun undoLastReward() {
        val latest = rewardLogDao.getLatest() ?: return
        rewardLogDao.deleteById(latest.id)
        val pet = getPet() ?: return
        val newScore = (pet.totalScore - latest.score).coerceAtLeast(0)
        val currentStage = GrowthStage.fromScore(newScore)
        val storedStage = GrowthStage.valueOf(pet.stage)
        val stageToKeep = if (storedStage.ordinal > currentStage.ordinal) {
            storedStage.name
        } else {
            currentStage.name
        }
        petDao.updateScoreAndStage(newScore, stageToKeep)
    }

    suspend fun deleteRewardLog(id: Long) {
        val log = rewardLogDao.getById(id) ?: return
        rewardLogDao.deleteById(id)
        val pet = getPet() ?: return
        val newScore = (pet.totalScore - log.score).coerceAtLeast(0)
        val currentStage = GrowthStage.fromScore(newScore)
        val storedStage = GrowthStage.valueOf(pet.stage)
        val stageToKeep = if (storedStage.ordinal > currentStage.ordinal) {
            storedStage.name
        } else {
            currentStage.name
        }
        petDao.updateScoreAndStage(newScore, stageToKeep)
    }

    suspend fun initDefaultPresetsIfEmpty() {
        if (presetRewardDao.count() == 0) {
            presetRewardDao.upsertAll(defaultPresets)
        }
    }

    suspend fun savePresets(presets: List<PresetRewardEntity>) {
        presetRewardDao.deleteAll()
        presetRewardDao.upsertAll(presets)
    }

    companion object {
        val defaultPresets = listOf(
            PresetRewardEntity(label = "举手回答", icon = "🙋", score = 2, color = 0xFF4ADE80, sortOrder = 0),
            PresetRewardEntity(label = "作业优秀", icon = "📝", score = 5, color = 0xFF60A5FA, sortOrder = 1),
            PresetRewardEntity(label = "帮助同学", icon = "🤝", score = 3, color = 0xFFFBBF24, sortOrder = 2),
            PresetRewardEntity(label = "卫生整洁", icon = "🧹", score = 2, color = 0xFFF472B6, sortOrder = 3),
            PresetRewardEntity(label = "阅读认真", icon = "📖", score = 3, color = 0xFFA78BFA, sortOrder = 4),
            PresetRewardEntity(label = "课堂专注", icon = "🎯", score = 4, color = 0xFF34D399, sortOrder = 5)
        )
    }
}
