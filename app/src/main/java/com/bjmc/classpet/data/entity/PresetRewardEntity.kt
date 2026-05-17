package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preset_reward")
data class PresetRewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val icon: String,
    val score: Int,
    val color: Long,
    val sortOrder: Int
)
