package my.edu.aiu.app.tdminsight.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import my.edu.aiu.app.tdminsight.model.TdmInput
import my.edu.aiu.app.tdminsight.model.TdmResult
import org.json.JSONObject

class CalculationRepository(private val context: Context) {
    private val preferencesName = "tdm_calculations"
    private val maxRecords = 20

    private val _history = MutableStateFlow<List<CalculationRecord>>(emptyList())
    val history: StateFlow<List<CalculationRecord>> = _history.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val prefs = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val recordIds = prefs.getStringSet("record_ids", emptySet()) ?: emptySet()
        val records = recordIds.mapNotNull { id ->
            val json = prefs.getString("record_$id", null)
            json?.let { deserializeRecord(it) }
        }.sortedByDescending { it.timestamp }

        _history.value = records
    }

    suspend fun saveCalculation(input: TdmInput, result: TdmResult) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val record = CalculationRecord(
            workflow = input.workflow,
            patientAge = input.patient.ageYears,
            patientWeight = input.patient.weightKg,
            patientSex = input.patient.sex.name,
            dose = input.doseMg,
            interval = input.intervalHr,
            infusion = input.infusionHr,
            preConcentration = input.preConcentrationMgL,
            postConcentration = input.postConcentrationMgL,
            resultJson = serializeResult(result),
            timestamp = System.currentTimeMillis()
        )

        val serialized = serializeRecord(record)
        prefs.edit().apply {
            putString("record_${record.id}", serialized)
            val ids = (prefs.getStringSet("record_ids", emptySet()) ?: emptySet()).toMutableSet()
            ids.add(record.id)

            // Keep only the most recent records
            if (ids.size > maxRecords) {
                val sorted = ids.mapNotNull { id ->
                    prefs.getString("record_$id", null)?.let { deserializeRecord(it) }
                }.sortedByDescending { it.timestamp }

                ids.clear()
                sorted.take(maxRecords).forEach { ids.add(it.id) }

                // Remove old records
                val allIds = (prefs.getStringSet("record_ids", emptySet()) ?: emptySet())
                allIds.forEach { id ->
                    if (id !in ids) remove("record_$id")
                }
            }

            putStringSet("record_ids", ids)
            apply()
        }

        loadHistory()
    }

    suspend fun deleteRecord(id: String) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove("record_$id")
            val ids = (prefs.getStringSet("record_ids", emptySet()) ?: emptySet()).toMutableSet()
            ids.remove(id)
            putStringSet("record_ids", ids)
            apply()
        }
        loadHistory()
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        loadHistory()
    }

    private fun serializeRecord(record: CalculationRecord): String {
        return JSONObject().apply {
            put("id", record.id)
            put("workflow", record.workflow.name)
            put("patientAge", record.patientAge)
            put("patientWeight", record.patientWeight)
            put("patientSex", record.patientSex)
            put("dose", record.dose)
            put("interval", record.interval)
            put("infusion", record.infusion)
            put("preConcentration", record.preConcentration)
            put("postConcentration", record.postConcentration)
            put("timestamp", record.timestamp)
            put("resultJson", record.resultJson)
        }.toString()
    }

    private fun deserializeRecord(json: String): CalculationRecord? {
        return try {
            val obj = JSONObject(json)
            CalculationRecord(
                id = obj.getString("id"),
                workflow = my.edu.aiu.app.tdminsight.model.Workflow.valueOf(obj.getString("workflow")),
                patientAge = obj.getDouble("patientAge"),
                patientWeight = obj.getDouble("patientWeight"),
                patientSex = obj.getString("patientSex"),
                dose = obj.getDouble("dose"),
                interval = obj.getDouble("interval"),
                infusion = obj.getDouble("infusion"),
                preConcentration = if (obj.has("preConcentration") && obj.get("preConcentration") != JSONObject.NULL) obj.getDouble("preConcentration") else null,
                postConcentration = if (obj.has("postConcentration") && obj.get("postConcentration") != JSONObject.NULL) obj.getDouble("postConcentration") else null,
                timestamp = obj.getLong("timestamp"),
                resultJson = obj.getString("resultJson")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun serializeResult(result: TdmResult): String {
        return JSONObject().apply {
            put("workflow", result.workflow.name)
            put("modelNote", result.modelNote)
            put("timestamp", result.timestamp)
            // For simplicity, we're not serializing the full result structure
            // In production, you might use kotlinx.serialization or Gson
        }.toString()
    }
}
