package com.bjmc.classpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bjmc.classpet.data.PetRepository
import com.bjmc.classpet.data.entity.PetEntity
import com.bjmc.classpet.data.entity.PresetRewardEntity
import com.bjmc.classpet.data.entity.RewardLogEntity
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType
import com.bjmc.classpet.util.GrowthCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PetRepository) : ViewModel() {

    val pet: StateFlow<PetEntity?> = repository.pet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rewardLogs: StateFlow<List<RewardLogEntity>> = repository.rewardLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presetRewards: StateFlow<List<PresetRewardEntity>> = repository.presetRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rewardEvent = MutableSharedFlow<Int>(extraBufferCapacity = 10)
    val rewardEvent = _rewardEvent.asSharedFlow()

    private val _evolutionEvent = MutableSharedFlow<GrowthStage>(extraBufferCapacity = 10)
    val evolutionEvent = _evolutionEvent.asSharedFlow()

    val hasPet: Boolean get() = pet.value?.name?.isNotEmpty() == true
    val currentStage: GrowthStage get() = GrowthCalculator.getStage(pet.value?.totalScore ?: 0)
    val stageProgress: Float get() = GrowthCalculator.getStageProgress(pet.value?.totalScore ?: 0)
    val scoreToNext: Int get() = GrowthCalculator.getScoreToNextStage(pet.value?.totalScore ?: 0)
    val nextStage: GrowthStage? get() = GrowthCalculator.getNextStage(pet.value?.totalScore ?: 0)

    init {
        viewModelScope.launch {
            repository.initDefaultPresetsIfEmpty()
        }
    }

    fun savePet(name: String, type: PetType, color: Long) {
        viewModelScope.launch {
            repository.savePet(PetEntity(name = name, type = type.name, color = color))
        }
    }

    fun updatePetName(name: String) {
        viewModelScope.launch {
            val current = pet.value ?: return@launch
            repository.savePet(current.copy(name = name))
        }
    }

    fun updatePetAppearance(type: PetType, color: Long) {
        viewModelScope.launch {
            val current = pet.value ?: return@launch
            repository.savePet(current.copy(type = type.name, color = color))
        }
    }

    fun addReward(label: String, score: Int, note: String? = null) {
        viewModelScope.launch {
            val oldStage = currentStage
            repository.addReward(label, score, note)
            _rewardEvent.emit(score)
            val newScore = pet.value?.totalScore ?: 0
            val newStage = GrowthCalculator.getStage(newScore)
            if (newStage.ordinal > oldStage.ordinal) {
                _evolutionEvent.emit(newStage)
            }
        }
    }

    fun undoLastReward() {
        viewModelScope.launch {
            repository.undoLastReward()
        }
    }

    fun savePresets(presets: List<PresetRewardEntity>) {
        viewModelScope.launch {
            repository.savePresets(presets)
        }
    }

    class Factory(private val repository: PetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
