package my.edu.aiu.app.tdminsight.model

data class ResultMetric(
    val label: String,
    val value: String,
    val unit: String,
    val description: String = "",
    val isWarning: Boolean = false,
)

data class CalculationStep(
    val title: String,
    val detail: String,
    val equation: String = "",
)

data class TdmResult(
    val workflow: Workflow,
    val metrics: List<ResultMetric>,
    val steps: List<CalculationStep>,
    val warnings: List<String>,
    val modelNote: String,
    val timestamp: Long = System.currentTimeMillis(),
)
