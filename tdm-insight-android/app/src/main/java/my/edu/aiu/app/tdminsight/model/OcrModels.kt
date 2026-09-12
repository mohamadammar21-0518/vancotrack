package my.edu.aiu.app.tdminsight.model

/**
 * A single numeric value extracted by OCR from the lab report image.
 * [value]   – the raw numeric string, e.g. "12.4"
 * [unit]    – adjacent unit text if detected, e.g. "mg/L"
 * [context] – short surrounding text for user reference, e.g. "Vanc trough 12.4"
 */
data class OcrCandidate(
    val value: String,
    val unit: String = "",
    val context: String = "",
)
