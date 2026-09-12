package my.edu.aiu.app.tdminsight.model

enum class Workflow(val label: String) {
    PRE("Vancomycin Pre"),
    POST("Vancomycin Post"),
    PRE_POST("Vancomycin Pre + Post"),
}

data class TdmInput(
    val workflow: Workflow = Workflow.PRE_POST,
    val patient: Patient = Patient(Double.NaN, Sex.MALE, Double.NaN, Double.NaN),
    val doseMg: Double = Double.NaN,
    val intervalHr: Double = Double.NaN,
    val infusionHr: Double = Double.NaN,
    val preConcentrationMgL: Double? = null,
    val postConcentrationMgL: Double? = null,
    val postSampleAfterEndHr: Double? = null,
    val hoursBetweenPostAndPreHr: Double? = null,
)
