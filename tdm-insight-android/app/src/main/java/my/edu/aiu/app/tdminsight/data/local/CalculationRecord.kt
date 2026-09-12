package my.edu.aiu.app.tdminsight.data.local

import my.edu.aiu.app.tdminsight.model.TdmResult
import my.edu.aiu.app.tdminsight.model.Workflow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculationRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val workflow: Workflow = Workflow.PRE_POST,
    val patientAge: Double = 0.0,
    val patientWeight: Double = 0.0,
    val patientSex: String = "MALE",
    val dose: Double = 0.0,
    val interval: Double = 0.0,
    val infusion: Double = 0.0,
    val preConcentration: Double? = null,
    val postConcentration: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val resultJson: String = "", // Store serialized TdmResult
) {
    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getWorkflowLabel(): String = workflow.label

    fun getSummary(): String {
        return "Age: ${patientAge.toInt()}y, Weight: ${patientWeight}kg, Dose: ${dose.toInt()}mg Q${interval.toInt()}h"
    }
}
