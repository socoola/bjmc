package com.bjmc.classpet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bjmc.classpet.model.GrowthStage
import com.bjmc.classpet.model.PetType

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val type: String = PetType.CAT.name,
    val color: Long = 0xFFFF9C9C,
    val totalScore: Int = 0,
    val stage: String = GrowthStage.EGG.name
)
