package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_log")
data class RewardLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val score: Int,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
