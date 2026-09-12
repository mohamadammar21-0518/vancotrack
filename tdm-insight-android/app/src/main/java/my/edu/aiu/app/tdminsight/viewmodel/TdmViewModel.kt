package my.edu.aiu.app.tdminsight.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.edu.aiu.app.tdminsight.data.export.PdfExporter
import my.edu.aiu.app.tdminsight.data.local.CalculationRepository
import my.edu.aiu.app.tdminsight.domain.calculator.VancomycinCalculator
import my.edu.aiu.app.tdminsight.domain.validation.TdmValidation
import my.edu.aiu.app.tdminsight.model.OcrCandidate
import my.edu.aiu.app.tdminsight.model.Patient
import my.edu.aiu.app.tdminsight.model.Sex
import my.edu.aiu.app.tdminsight.model.TdmInput
import my.edu.aiu.app.tdminsight.model.TdmResult
import my.edu.aiu.app.tdminsight.model.Workflow

// ── OCR state machine ────────────────────────────────────────────────────────
sealed class OcrState {
    object Idle    : OcrState()
    object Running : OcrState()
    data class Done(val candidates: List<OcrCandidate>) : OcrState()
    data class Error(val message: String) : OcrState()
}

// ── PDF export state machine ──────────────────────────────────────────────────
sealed class ExportState {
    object Idle       : ExportState()
    object Generating : ExportState()
    data class Ready(val uri: Uri)       : ExportState()
    data class Error(val message: String): ExportState()
}

// ── UI state ─────────────────────────────────────────────────────────────────
data class TdmUiState(
    // ── Form fields ──────────────────────────────────────────────────────────
    val workflow: Workflow = Workflow.PRE_POST,
    val age: String = "",
    val sex: Sex = Sex.MALE,
    val weight: String = "",
    val creatinine: String = "",
    val dose: String = "",
    val interval: String = "",
    val infusion: String = "",
    val pre: String = "",
    val post: String = "",
    val postAfterEnd: String = "",
    val postToPre: String = "",
    // ── Calculation state ─────────────────────────────────────────────────────
    val errors: List<String> = emptyList(),
    val result: TdmResult? = null,
    val showExplanation: Boolean = false,
    val isLoading: Boolean = false,
    val isCalculating: Boolean = false,
    // ── Camera / OCR state ────────────────────────────────────────────────────
    val capturedImageUri: Uri? = null,
    val cameraTargetField: String = "",    // "pre" | "post" | "creatinine"
    val ocrState: OcrState = OcrState.Idle,
    val selectedOcrValue: String = "",     // value user has selected or typed
    // ── PDF export state ──────────────────────────────────────────────────────
    val exportState: ExportState = ExportState.Idle,
)

// ── ViewModel ────────────────────────────────────────────────────────────────
class TdmViewModel(application: Application) : AndroidViewModel(application) {

    private val calculator = VancomycinCalculator()
    private val repository = CalculationRepository(application)
    private val _state     = MutableStateFlow(TdmUiState())
    val state: StateFlow<TdmUiState> = _state.asStateFlow()
    val history = repository.history

    // Keeps the last successful input so it can be included in the PDF
    private var lastInput: TdmInput? = null

    // ── Form updates ─────────────────────────────────────────────────────────
    fun setWorkflow(value: Workflow) =
        _state.update { it.copy(workflow = value, errors = emptyList(), result = null) }

    fun setSex(value: Sex) =
        _state.update { it.copy(sex = value, errors = emptyList(), result = null) }

    fun update(field: String, value: String) = _state.update {
        when (field) {
            "age"         -> it.copy(age = value, errors = emptyList(), result = null)
            "weight"      -> it.copy(weight = value, errors = emptyList(), result = null)
            "creatinine"  -> it.copy(creatinine = value, errors = emptyList(), result = null)
            "dose"        -> it.copy(dose = value, errors = emptyList(), result = null)
            "interval"    -> it.copy(interval = value, errors = emptyList(), result = null)
            "infusion"    -> it.copy(infusion = value, errors = emptyList(), result = null)
            "pre"         -> it.copy(pre = value, errors = emptyList(), result = null)
            "post"        -> it.copy(post = value, errors = emptyList(), result = null)
            "postAfterEnd"-> it.copy(postAfterEnd = value, errors = emptyList(), result = null)
            "postToPre"   -> it.copy(postToPre = value, errors = emptyList(), result = null)
            else          -> it
        }
    }

    fun loadSample() = _state.update {
        it.copy(
            age = "54", weight = "72", creatinine = "0.9",
            dose = "1000", interval = "12", infusion = "2",
            pre = "12.4", post = "28.6", postAfterEnd = "1", postToPre = "8",
            workflow = Workflow.PRE_POST, errors = emptyList(), result = null,
        )
    }

    // ── Calculate ─────────────────────────────────────────────────────────────
    fun calculate(onSuccess: () -> Unit = {}) {
        val input  = state.value.toInput()
        val errors = TdmValidation.validate(input)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors, result = null) }
            return
        }
        _state.update { it.copy(isCalculating = true, errors = emptyList()) }
        try {
            val result = calculator.calculate(input)
            _state.update {
                it.copy(
                    isCalculating = false,
                    errors = result.exceptionOrNull()?.let { e ->
                        listOf(e.message ?: "Unable to calculate")
                    } ?: emptyList(),
                    result = result.getOrNull(),
                    showExplanation = false,
                )
            }
            result.getOrNull()?.let { r ->
                lastInput = input                                   // ← store for PDF
                viewModelScope.launch { repository.saveCalculation(input, r) }
                onSuccess()
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isCalculating = false,
                    errors = listOf("Calculation error: ${e.message ?: "Unknown error"}"),
                )
            }
        }
    }

    fun showExplanation(value: Boolean) = _state.update { it.copy(showExplanation = value) }

    fun reset() { _state.value = TdmUiState() }

    fun deleteHistoryRecord(id: String) = viewModelScope.launch { repository.deleteRecord(id) }
    fun clearHistory()                  = viewModelScope.launch { repository.clearHistory() }

    // ── PDF Export ────────────────────────────────────────────────────────────
    fun exportPdf(context: Context) {
        val result = _state.value.result ?: return
        val input  = lastInput            ?: return
        _state.update { it.copy(exportState = ExportState.Generating) }
        viewModelScope.launch {
            try {
                val uri = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    PdfExporter.export(context, result, input)
                }
                _state.update { it.copy(exportState = ExportState.Ready(uri)) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(exportState = ExportState.Error(e.message ?: "PDF generation failed"))
                }
            }
        }
    }

    /** Reset export state after the share sheet has been launched or dismissed. */
    fun clearExportState() = _state.update { it.copy(exportState = ExportState.Idle) }

    // ── Camera / OCR ─────────────────────────────────────────────────────────

    /** Called when the user taps the camera icon next to a field. */
    fun startCamera(targetField: String) = _state.update {
        it.copy(
            cameraTargetField = targetField,
            capturedImageUri  = null,
            ocrState          = OcrState.Idle,
            selectedOcrValue  = "",
        )
    }

    /** Called when CameraScreen delivers a captured image URI. */
    fun onImageCaptured(uri: Uri) = _state.update {
        it.copy(capturedImageUri = uri, ocrState = OcrState.Idle)
    }

    /** Runs ML Kit OCR on the captured image and extracts numeric candidates. */
    fun runOcr(context: Context, uri: Uri) {
        _state.update { it.copy(ocrState = OcrState.Running) }
        viewModelScope.launch {
            try {
                val image      = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val visionText = recognizer.process(image).await()

                val candidates = extractCandidates(visionText.text)
                _state.update { it.copy(ocrState = OcrState.Done(candidates)) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(ocrState = OcrState.Error(e.message ?: "Recognition failed"))
                }
            }
        }
    }

    /** User tapped an OCR candidate row. */
    fun setSelectedOcrValue(value: String) = _state.update { it.copy(selectedOcrValue = value) }

    /**
     * User confirmed the value — inject it into the correct form field and
     * clear camera state so the review screen can be dismissed.
     */
    fun confirmOcrValue(field: String, value: String) {
        update(field, value)
        _state.update {
            it.copy(
                capturedImageUri  = null,
                ocrState          = OcrState.Idle,
                selectedOcrValue  = "",
                cameraTargetField = "",
            )
        }
    }

    // ── OCR candidate extraction ──────────────────────────────────────────────
    /**
     * Extracts numeric candidates from raw OCR text.
     *
     * Strategy:
     * 1. Split the full text into lines.
     * 2. For each line, find all decimal/integer tokens matching clinical ranges.
     * 3. Capture the surrounding line as context and any adjacent unit tokens.
     * 4. Deduplicate by value string.
     * 5. Sort: prefer values in typical vancomycin/creatinine ranges first.
     */
    private fun extractCandidates(rawText: String): List<OcrCandidate> {
        // Regex: one or more digits optionally followed by . and more digits
        val numberRegex = Regex("""(?<!\d)\d{1,4}(\.\d{1,3})?(?!\d)""")
        // Known units that might appear next to concentration values
        val unitPatterns = listOf("mg/l", "mg/dl", "mg/L", "mg/dL", "mmol", "µmol", "g/dl", "g/dL")

        val seen = mutableSetOf<String>()
        val results = mutableListOf<OcrCandidate>()

        rawText.lines().forEach { line ->
            val trimmed = line.trim()
            numberRegex.findAll(trimmed).forEach { match ->
                val numStr = match.value
                val num    = numStr.toDoubleOrNull() ?: return@forEach

                // Filter out implausible values (page numbers, years, etc.)
                if (num < 0.1 || num > 500) return@forEach
                if (seen.contains(numStr)) return@forEach
                seen.add(numStr)

                // Try to find a unit in the same line
                val unit = unitPatterns.firstOrNull { u ->
                    trimmed.contains(u, ignoreCase = true)
                }?.let { u ->
                    when {
                        u.contains("mg/l", ignoreCase = true) -> "mg/L"
                        u.contains("mg/dl", ignoreCase = true) -> "mg/dL"
                        else -> u
                    }
                } ?: ""

                // Context: the whole line, truncated
                val context = trimmed.take(60)

                results.add(OcrCandidate(value = numStr, unit = unit, context = context))
            }
        }

        // Sort: values in typical vancomycin trough range (5–30) or creatinine range (0.5–2.5) first
        return results.sortedWith(compareByDescending { cand ->
            val v = cand.value.toDoubleOrNull() ?: 0.0
            when {
                v in 5.0..30.0  -> 3   // likely vancomycin level
                v in 0.5..2.5   -> 2   // likely creatinine
                v in 1.0..100.0 -> 1   // plausible
                else            -> 0
            }
        })
    }

    // ── toInput ───────────────────────────────────────────────────────────────
    private fun TdmUiState.toInput() = TdmInput(
        workflow  = workflow,
        patient   = Patient(
            ageYears              = age.toDoubleOrNull() ?: Double.NaN,
            sex                   = sex,
            weightKg              = weight.toDoubleOrNull() ?: Double.NaN,
            serumCreatinineMgDl   = creatinine.toDoubleOrNull() ?: Double.NaN,
        ),
        doseMg                   = dose.toDoubleOrNull() ?: Double.NaN,
        intervalHr               = interval.toDoubleOrNull() ?: Double.NaN,
        infusionHr               = infusion.toDoubleOrNull() ?: Double.NaN,
        preConcentrationMgL      = pre.toDoubleOrNull(),
        postConcentrationMgL     = post.toDoubleOrNull(),
        postSampleAfterEndHr     = postAfterEnd.toDoubleOrNull(),
        hoursBetweenPostAndPreHr = postToPre.toDoubleOrNull(),
    )
}
