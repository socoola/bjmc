package com.bjmc.classpet.model

data class PetColor(val name: String, val hex: Long)

val presetColors = listOf(
    PetColor("粉红", 0xFFFF9C9C),
    PetColor("暖黄", 0xFFFFD27F),
    PetColor("草绿", 0xFFA0E7A0),
    PetColor("天蓝", 0xFF9CC9FF),
    PetColor("淡紫", 0xFFD4A0FF),
    PetColor("柔粉", 0xFFFFB5D0)
)
