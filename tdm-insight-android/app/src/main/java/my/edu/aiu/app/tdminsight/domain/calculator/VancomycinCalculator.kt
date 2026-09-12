package my.edu.aiu.app.tdminsight.domain.calculator

import kotlin.math.exp
import kotlin.math.ln
import my.edu.aiu.app.tdminsight.domain.validation.TdmValidation
import my.edu.aiu.app.tdminsight.model.CalculationStep
import my.edu.aiu.app.tdminsight.model.ResultMetric
import my.edu.aiu.app.tdminsight.model.TdmInput
import my.edu.aiu.app.tdminsight.model.TdmResult
import my.edu.aiu.app.tdminsight.model.Workflow

class VancomycinCalculator {
    fun calculate(input: TdmInput): Result<TdmResult> {
        val errors = TdmValidation.validate(input)
        if (errors.isNotEmpty()) return Result.failure(IllegalArgumentException(errors.first()))

        val patient = input.patient
        val crcl = ((140 - patient.ageYears) * patient.weightKg / (72 * patient.serumCreatinineMgDl)) *
            if (patient.sex.name == "FEMALE") 0.85 else 1.0
        if (!crcl.isFinite() || crcl <= 0) return Result.failure(IllegalArgumentException("Renal-function estimate is invalid."))

        val vdInitial = 0.7 * patient.weightKg
        var ke = 0.00083 * crcl + 0.0044
        var keSource = "screening estimate from Cockcroft-Gault CrCl"
        var vd = vdInitial
        var postAtEnd: Double

        if (input.workflow == Workflow.PRE_POST) {
            ke = ln((input.postConcentrationMgL ?: 0.0) / (input.preConcentrationMgL ?: 1.0)) / (input.hoursBetweenPostAndPreHr ?: 1.0)
            keSource = "log-linear estimate from the two measured concentrations"
        }
        if (!ke.isFinite() || ke <= 0) return Result.failure(IllegalArgumentException("Elimination rate is invalid."))

        if (input.workflow == Workflow.PRE) {
            val clearance = ke * vd
            postAtEnd = (input.doseMg / (clearance * input.infusionHr)) * (1 - exp(-ke * input.infusionHr))
        } else {
            val post = input.postConcentrationMgL ?: 1.0
            postAtEnd = post * exp(ke * (input.postSampleAfterEndHr ?: 0.0))
            vd = (input.doseMg * (1 - exp(-ke * input.infusionHr))) / (ke * input.infusionHr * postAtEnd)
        }

        val halfLife = 0.693 / ke
        val clearance = ke * vd
        val predictedTrough = postAtEnd * exp(-ke * (input.intervalHr - input.infusionHr))
        val auc24 = (input.doseMg * 24) / (clearance * input.intervalHr)
        val measuredPre = input.preConcentrationMgL
        val troughComparison = measuredPre?.let { (it / predictedTrough) * 100 }

        val warnings = mutableListOf(
            "Review equations, units, and sampling assumptions from authoritative pharmacokinetic sources.",
            "Do not use this output to prescribe, adjust, or monitor real patient treatment.",
        )
        if (crcl < 30) warnings += "Estimated CrCl is below 30 mL/min; specialist review is recommended."
        if (troughComparison != null && (troughComparison < 70 || troughComparison > 130)) warnings += "Measured and modelled trough values differ materially; verify timing and units."

        fun f(value: Double, decimals: Int = 2) = "%1.${decimals}f".format(value)
        val metrics = mutableListOf(
            ResultMetric("Elimination rate (Ke)", f(ke, 4), "hr⁻¹"),
            ResultMetric("Half-life", f(halfLife), "hr"),
            ResultMetric("Volume of distribution", f(vd), "L"),
            ResultMetric("Clearance", f(clearance), "L/hr"),
            ResultMetric("Predicted trough", f(predictedTrough), "mg/L"),
            ResultMetric("Estimated AUC24", f(auc24, 1), "mg·hr/L"),
        )
        if (measuredPre != null) metrics.add(5, ResultMetric("Measured pre-dose", f(measuredPre), "mg/L"))

        val steps = mutableListOf(
            CalculationStep(
                title    = "1. Renal-function estimate",
                detail   = "Cockcroft-Gault CrCl ≈ ${f(crcl, 1)} mL/min using age, weight, and serum creatinine.",
                equation = "CrCl = [(140 − Age) × Weight] / (72 × SCr)  ×  0.85 if female"
            ),
            CalculationStep(
                title    = "2. Elimination rate",
                detail   = "Ke uses the $keSource; Ke ≈ ${f(ke, 4)} hr⁻¹, giving a half-life of ${f(halfLife)} hr.",
                equation = if (input.workflow == Workflow.PRE_POST)
                    "Ke = ln(C_post / C_pre) / t_between"
                else
                    "Ke = 0.00083 × CrCl + 0.0044    t½ = 0.693 / Ke"
            ),
            CalculationStep(
                title    = "3. Concentration model",
                detail   = "Estimated concentration at infusion end ≈ ${f(postAtEnd)} mg/L, followed by first-order decline over the dosing interval.",
                equation = "C(t) = C_peak × e^(−Ke × t)    Vd = ${f(vd, 1)} L  (${f(vd / patient.weightKg, 2)} L/kg)"
            ),
            CalculationStep(
                title    = "4. Exposure estimate",
                detail   = "Clearance = Ke × Vd ≈ ${f(clearance)} L/hr; dose and interval produce estimated AUC24 ${f(auc24, 1)} mg·hr/L.",
                equation = "CL = Ke × Vd = ${f(clearance)} L/hr    AUC₂₄ = (Dose × 24) / (CL × τ)"
            ),
        )
        if (measuredPre != null && troughComparison != null) steps += CalculationStep(
            title    = "5. Pre-dose review",
            detail   = "Measured pre-dose is ${f(measuredPre)} mg/L, approximately ${f(troughComparison, 0)}% of the modelled trough. This is a review signal, not a dosing recommendation.",
            equation = "Trough ratio = C_measured / C_predicted × 100 = ${f(troughComparison, 0)}%"
        )

        return Result.success(TdmResult(input.workflow, metrics, steps, warnings, "Simplified one-compartment, first-order approximation; not a clinically validated decision-support tool."))
    }
}
