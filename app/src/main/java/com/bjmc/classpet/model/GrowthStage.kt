package com.bjmc.classpet.model

enum class GrowthStage(val label: String, val threshold: Int) {
    EGG("蛋", 0),
    BABY("幼崽", 50),
    TEEN("少年", 150),
    ADULT("成年", 300),
    MATURE("成熟", 500);

    companion object {
        fun fromScore(score: Int): GrowthStage =
            entries.lastOrNull { score >= it.threshold } ?: EGG
    }
}
