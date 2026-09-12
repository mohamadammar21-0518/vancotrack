package my.edu.aiu.app.tdminsight.domain.validation

import my.edu.aiu.app.tdminsight.model.TdmInput
import my.edu.aiu.app.tdminsight.model.Workflow

object TdmValidation {
    fun validate(input: TdmInput): List<String> {
        val errors = mutableListOf<String>()

        // Patient validation
        if (!input.patient.ageYears.isFinite() || input.patient.ageYears <= 0) {
            errors += "Age must be a positive number (typically 18–100 years)."
        } else if (input.patient.ageYears > 120) {
            errors += "Age must be 120 years or less."
        } else if (input.patient.ageYears < 18) {
            errors += "This tool is designed for adult patients (age ≥ 18)."
        }

        if (!input.patient.weightKg.isFinite() || input.patient.weightKg <= 0) {
            errors += "Weight must be a positive number in kilograms."
        } else if (input.patient.weightKg > 300) {
            errors += "Weight must be 300 kg or less."
        }

        if (!input.patient.serumCreatinineMgDl.isFinite() || input.patient.serumCreatinineMgDl <= 0) {
            errors += "Serum creatinine must be a positive number in mg/dL."
        } else if (input.patient.serumCreatinineMgDl > 15) {
            errors += "Serum creatinine must be 15 mg/dL or less; check units."
        }

        // Medication validation
        if (!input.doseMg.isFinite() || input.doseMg <= 0) {
            errors += "Dose must be a positive number in milligrams."
        } else if (input.doseMg < 250 || input.doseMg > 2000) {
            errors += "Vancomycin dose is typically 250–2000 mg; verify this value."
        }

        if (!input.intervalHr.isFinite() || input.intervalHr <= 0) {
            errors += "Dosing interval must be a positive number in hours."
        } else if (input.intervalHr !in 6.0..72.0) {
            errors += "Dosing interval is typically 6–72 hours (Q6H to Q72H); verify."
        }

        if (!input.infusionHr.isFinite() || input.infusionHr <= 0) {
            errors += "Infusion duration must be a positive number in hours."
        } else if (input.infusionHr !in 0.5..4.0) {
            errors += "Vancomycin infusion is typically 0.5–4 hours; verify."
        }

        if (input.infusionHr >= input.intervalHr) {
            errors += "Infusion duration must be shorter than the dosing interval."
        }

        // Workflow-specific validation
        when (input.workflow) {
            Workflow.PRE -> {
                val pre = input.preConcentrationMgL ?: Double.NaN
                if (!pre.isFinite() || pre <= 0) {
                    errors += "Pre-dose concentration must be a positive number in mg/L."
                } else if (pre > 30) {
                    errors += "Pre-dose concentration above 30 mg/L is unusual; verify units (mg/L not μg/mL)."
                }
            }

            Workflow.POST -> {
                val post = input.postConcentrationMgL ?: Double.NaN
                val postAfterEnd = input.postSampleAfterEndHr ?: Double.NaN
                if (!post.isFinite() || post <= 0) {
                    errors += "Post-dose concentration must be a positive number in mg/L."
                } else if (post > 50) {
                    errors += "Post-dose concentration above 50 mg/L is unusual; verify units."
                }
                if (!postAfterEnd.isFinite() || postAfterEnd < 0) {
                    errors += "Post-sample time must be 0 hours or greater after infusion end."
                } else if (postAfterEnd >= input.intervalHr - input.infusionHr) {
                    errors += "Post sample must be taken before the next scheduled dose."
                }
            }

            Workflow.PRE_POST -> {
                val pre = input.preConcentrationMgL ?: Double.NaN
                val post = input.postConcentrationMgL ?: Double.NaN
                val postAfterEnd = input.postSampleAfterEndHr ?: Double.NaN
                val between = input.hoursBetweenPostAndPreHr ?: Double.NaN

                if (!pre.isFinite() || pre <= 0) {
                    errors += "Pre-dose concentration must be a positive number in mg/L."
                } else if (pre > 30) {
                    errors += "Pre-dose concentration above 30 mg/L is unusual; verify units."
                }

                if (!post.isFinite() || post <= 0) {
                    errors += "Post-dose concentration must be a positive number in mg/L."
                } else if (post > 50) {
                    errors += "Post-dose concentration above 50 mg/L is unusual; verify units."
                }

                if (!postAfterEnd.isFinite() || postAfterEnd < 0) {
                    errors += "Post-sample time must be 0 hours or greater after infusion end."
                }

                if (!between.isFinite() || between <= 0) {
                    errors += "Time between post and pre samples must be a positive number."
                }

                if (post <= pre) {
                    errors += "Post-dose concentration must be higher than pre-dose (log-linear decay assumed)."
                }

                if (postAfterEnd + between >= input.intervalHr - input.infusionHr) {
                    errors += "Post-to-pre sampling window must complete before the next dose."
                }
            }
        }

        return errors
    }
}
