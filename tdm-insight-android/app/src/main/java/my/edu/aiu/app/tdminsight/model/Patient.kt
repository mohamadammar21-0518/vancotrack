package my.edu.aiu.app.tdminsight.model

data class Patient(
    val ageYears: Double,
    val sex: Sex,
    val weightKg: Double,
    val serumCreatinineMgDl: Double,
)

enum class Sex { MALE, FEMALE }
