package my.edu.aiu.app.tdminsight.data.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import my.edu.aiu.app.tdminsight.model.TdmInput
import my.edu.aiu.app.tdminsight.model.TdmResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates a PDF report from a TDM calculation result.
 *
 * Layout (A4, portrait):
 *   Page 1 — Header + patient summary + PK metrics table
 *   Page 2 — Calculation trail (steps) + clinical warnings + footer
 *
 * Uses only the Android framework PdfDocument API — no third-party library required.
 */
object PdfExporter {

    // ── Page geometry ────────────────────────────────────────────────────────
    private const val PAGE_W   = 595   // A4 width  in points (72 dpi)
    private const val PAGE_H   = 842   // A4 height in points
    private const val MARGIN   = 48f
    private const val CONTENT_W = PAGE_W - MARGIN * 2

    // ── Colours (ARGB) ───────────────────────────────────────────────────────
    private val C_NAVY    = Color.rgb(0x0F, 0x2A, 0x4A)
    private val C_TEAL    = Color.rgb(0x0C, 0x9B, 0x8A)
    private val C_INK     = Color.rgb(0x12, 0x23, 0x3F)
    private val C_MUTED   = Color.rgb(0x50, 0x62, 0x7C)
    private val C_BORDER  = Color.rgb(0xDC, 0xE5, 0xF0)
    private val C_PAGE_BG = Color.rgb(0xF4, 0xF7, 0xFB)
    private val C_WHITE   = Color.WHITE
    private val C_WARNING = Color.rgb(0x7A, 0x4F, 0x00)
    private val C_WARN_BG = Color.rgb(0xFF, 0xF8, 0xE6)
    private val C_SUCCESS = Color.rgb(0x0D, 0x6E, 0x3A)

    // ── Entry point ──────────────────────────────────────────────────────────
    fun export(context: Context, result: TdmResult, input: TdmInput): Uri {
        val doc    = PdfDocument()
        var pageNo = 1

        // ── Page 1 ────────────────────────────────────────────────────────
        val p1info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo++).create()
        val page1  = doc.startPage(p1info)
        val c1     = page1.canvas
        var y      = drawHeader(c1, result, input)
        y          = drawPatientSummary(c1, y, input)
        y          = drawMetricsTable(c1, y, result)
        drawPageFooter(c1, 1, result)
        doc.finishPage(page1)

        // ── Page 2 ────────────────────────────────────────────────────────
        val p2info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create()
        val page2  = doc.startPage(p2info)
        val c2     = page2.canvas
        var y2     = MARGIN + 24f
        y2         = drawCalculationTrail(c2, y2, result)
        drawWarnings(c2, y2, result)
        drawPageFooter(c2, 2, result)
        doc.finishPage(page2)

        // ── Write to cache ────────────────────────────────────────────────
        val dir  = File(context.cacheDir, "reports").also { it.mkdirs() }
        val ts   = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "TDM_Report_$ts.pdf")
        file.outputStream().use { doc.writeTo(it) }
        doc.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // ── Header band ──────────────────────────────────────────────────────────
    private fun drawHeader(c: Canvas, result: TdmResult, input: TdmInput): Float {
        // Navy background band
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = C_NAVY
        c.drawRect(0f, 0f, PAGE_W.toFloat(), 120f, paint)

        // App name
        paint.color     = C_WHITE
        paint.typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize  = 22f
        c.drawText("VancoTrack", MARGIN, 44f, paint)

        // Subtitle
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 11f
        paint.color    = Color.rgb(0xB4, 0xC4, 0xD8)
        c.drawText("Vancomycin Therapeutic Drug Monitoring — Calculation Report", MARGIN, 62f, paint)

        // Teal accent line
        paint.color = C_TEAL
        c.drawRect(MARGIN, 74f, MARGIN + 40f, 77f, paint)

        // Workflow chip
        val wfText = result.workflow.label
        paint.color    = C_TEAL
        paint.textSize = 10f
        val chipW = paint.measureText(wfText) + 20f
        val chipRect = RectF(MARGIN, 84f, MARGIN + chipW, 100f)
        paint.style = Paint.Style.FILL
        c.drawRoundRect(chipRect, 6f, 6f, paint)
        paint.color    = C_WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        c.drawText(wfText, MARGIN + 10f, 95f, paint)
        paint.style = Paint.Style.FILL

        // Date/time stamp (right-aligned)
        val ts = SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.US).format(Date(result.timestamp))
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        paint.color    = Color.rgb(0xB4, 0xC4, 0xD8)
        val tsW = paint.measureText(ts)
        c.drawText(ts, PAGE_W - MARGIN - tsW, 44f, paint)

        return 132f
    }

    // ── Patient summary row ───────────────────────────────────────────────────
    private fun drawPatientSummary(c: Canvas, startY: Float, input: TdmInput): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Section title
        sectionTitle(c, "Patient & Dosing Summary", startY)
        var y = startY + 22f

        // Light background card
        paint.color = C_PAGE_BG
        c.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + 56f), 8f, 8f, paint)

        // Two columns of key-value pairs
        val p = input.patient
        val leftPairs = listOf(
            "Age"        to "${p.ageYears.toInt()} years",
            "Weight"     to "${p.weightKg} kg",
            "Sex"        to p.sex.name.lowercase().replaceFirstChar { it.uppercase() },
        )
        val rightPairs = listOf(
            "Creatinine" to "${p.serumCreatinineMgDl} mg/dL",
            "Dose"       to "${input.doseMg.toInt()} mg  Q${input.intervalHr.toInt()}H",
            "Infusion"   to "${input.infusionHr} hr",
        )

        paint.textSize = 9f
        var lx = MARGIN + 12f
        var rx = MARGIN + CONTENT_W / 2 + 12f
        var ky = y + 16f

        leftPairs.forEachIndexed { i, (k, v) ->
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color    = C_MUTED
            c.drawText(k, lx, ky, paint)
            paint.typeface = Typeface.DEFAULT
            paint.color    = C_INK
            c.drawText(v, lx + 68f, ky, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color    = C_MUTED
            val (rk, rv) = rightPairs[i]
            c.drawText(rk, rx, ky, paint)
            paint.typeface = Typeface.DEFAULT
            paint.color    = C_INK
            c.drawText(rv, rx + 72f, ky, paint)

            ky += 16f
        }

        return y + 68f
    }

    // ── PK Metrics table ──────────────────────────────────────────────────────
    private fun drawMetricsTable(c: Canvas, startY: Float, result: TdmResult): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        sectionTitle(c, "Pharmacokinetic Results", startY + 8f)
        var y = startY + 32f

        val colW  = CONTENT_W / 3f
        val rowH  = 38f
        val cols  = 3

        result.metrics.chunked(cols).forEach { row ->
            row.forEachIndexed { col, metric ->
                val x = MARGIN + col * colW
                val isHighlight = metric.label in setOf("Predicted trough", "Estimated AUC24")

                // Card bg
                paint.color = if (isHighlight) C_NAVY else C_WHITE
                c.drawRoundRect(RectF(x + 2f, y + 2f, x + colW - 2f, y + rowH - 2f), 6f, 6f, paint)

                // Label
                paint.textSize = 8f
                paint.color    = if (isHighlight) Color.rgb(0xB4, 0xC4, 0xD8) else C_MUTED
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                c.drawText(metric.label, x + 8f, y + 14f, paint)

                // Value
                paint.textSize = 13f
                paint.color    = if (isHighlight) C_WHITE else C_INK
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                c.drawText(metric.value, x + 8f, y + 28f, paint)

                // Unit
                paint.textSize = 8f
                paint.color    = if (isHighlight) Color.rgb(0xB4, 0xC4, 0xD8) else C_MUTED
                paint.typeface = Typeface.DEFAULT
                val vw = Paint().also {
                    it.textSize = 13f
                    it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }.measureText(metric.value)
                c.drawText(metric.unit, x + 8f + vw + 4f, y + 28f, paint)
            }
            y += rowH
        }

        return y + 12f
    }

    // ── Calculation trail (page 2) ────────────────────────────────────────────
    private fun drawCalculationTrail(c: Canvas, startY: Float, result: TdmResult): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        sectionTitle(c, "Calculation Trail", startY)
        var y = startY + 24f

        result.steps.forEachIndexed { i, step ->
            // Step badge circle
            paint.color  = C_TEAL
            paint.style  = Paint.Style.FILL
            c.drawCircle(MARGIN + 10f, y + 10f, 10f, paint)
            paint.color    = C_WHITE
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            c.drawText("${i + 1}", MARGIN + 10f, y + 14f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Connector line (except last)
            if (i < result.steps.lastIndex) {
                paint.color     = C_BORDER
                paint.strokeWidth = 1.5f
                paint.style     = Paint.Style.STROKE
                c.drawLine(MARGIN + 10f, y + 20f, MARGIN + 10f, y + 52f, paint)
                paint.style = Paint.Style.FILL
            }

            // Title
            paint.color    = C_INK
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            c.drawText(step.title, MARGIN + 26f, y + 12f, paint)

            // Detail — wrap long text
            val detailLines = wrapText(step.detail, Paint().also { it.textSize = 9f }, CONTENT_W - 26f)
            paint.textSize = 9f
            paint.typeface = Typeface.DEFAULT
            paint.color    = C_MUTED
            var dy = y + 24f
            detailLines.forEach { line ->
                c.drawText(line, MARGIN + 26f, dy, paint)
                dy += 13f
            }

            // Equation box if present
            if (step.equation.isNotBlank()) {
                paint.color = C_PAGE_BG
                paint.style = Paint.Style.FILL
                val eqRect = RectF(MARGIN + 26f, dy, PAGE_W - MARGIN, dy + 16f)
                c.drawRoundRect(eqRect, 4f, 4f, paint)
                paint.color    = C_TEAL
                paint.textSize = 8f
                paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                c.drawText(step.equation.take(80), MARGIN + 32f, dy + 11f, paint)
                paint.typeface = Typeface.DEFAULT
                dy += 20f
            }

            y = dy + 6f

            // Overflow guard — stop drawing if near bottom
            if (y > PAGE_H - 80f) return y
        }

        return y + 8f
    }

    // ── Warnings ──────────────────────────────────────────────────────────────
    private fun drawWarnings(c: Canvas, startY: Float, result: TdmResult) {
        if (result.warnings.isEmpty()) return
        if (startY > PAGE_H - 80f) return

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        sectionTitle(c, "Clinical Review Notes", startY)
        var y = startY + 22f

        result.warnings.forEach { warning ->
            val lines = wrapText(warning, Paint().also { it.textSize = 8.5f }, CONTENT_W - 20f)
            val boxH  = lines.size * 13f + 16f

            if (y + boxH > PAGE_H - 60f) return

            paint.color = C_WARN_BG
            c.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + boxH), 6f, 6f, paint)

            // Warning indicator stripe
            paint.color = C_WARNING
            c.drawRoundRect(RectF(MARGIN, y, MARGIN + 4f, y + boxH), 3f, 3f, paint)

            paint.color    = C_WARNING
            paint.textSize = 8.5f
            paint.typeface = Typeface.DEFAULT
            var ty = y + 13f
            lines.forEach { line ->
                c.drawText(line, MARGIN + 12f, ty, paint)
                ty += 13f
            }
            y += boxH + 8f
        }

        // Disclaimer line
        if (y < PAGE_H - 50f) {
            paint.color    = C_MUTED
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            c.drawText(
                "This report is for clinical reference only. Do not use for prescribing or treatment decisions.",
                MARGIN, y + 10f, paint,
            )
        }
    }

    // ── Page footer ───────────────────────────────────────────────────────────
    private fun drawPageFooter(c: Canvas, page: Int, result: TdmResult) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Divider line
        paint.color       = C_BORDER
        paint.strokeWidth = 1f
        paint.style       = Paint.Style.STROKE
        c.drawLine(MARGIN, PAGE_H - 40f, PAGE_W - MARGIN, PAGE_H - 40f, paint)
        paint.style = Paint.Style.FILL

        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        paint.color    = C_MUTED
        c.drawText("VancoTrack  •  Vancomycin TDM Calculator", MARGIN, PAGE_H - 24f, paint)

        // Page number right-aligned
        val pageStr = "Page $page"
        val pw = paint.measureText(pageStr)
        c.drawText(pageStr, PAGE_W - MARGIN - pw, PAGE_H - 24f, paint)
    }

    // ── Section title ──────────────────────────────────────────────────────────
    private fun sectionTitle(c: Canvas, title: String, y: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color    = C_TEAL
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        c.drawText(title.uppercase(), MARGIN, y, paint)

        // Underline
        paint.color       = C_TEAL
        paint.strokeWidth = 1.5f
        paint.style       = Paint.Style.STROKE
        val tw = paint.measureText(title.uppercase())
        c.drawLine(MARGIN, y + 3f, MARGIN + tw, y + 3f, paint)
        paint.style = Paint.Style.FILL
    }

    // ── Text wrap helper ───────────────────────────────────────────────────────
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words  = text.split(" ")
        val lines  = mutableListOf<String>()
        var current = ""
        words.forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotEmpty()) lines.add(current)
                current = word
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }
}
