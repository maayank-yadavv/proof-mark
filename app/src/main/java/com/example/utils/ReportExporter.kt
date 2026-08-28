package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.entities.ComplianceCheckEntity
import com.example.data.local.entities.DeclarationEntity
import com.example.data.local.entities.InspectionEntity
import java.io.File
import java.io.FileOutputStream

object ReportExporter {

    /**
     * Generates a formal Legal Metrology PDF Report document and opens/shares it.
     */
    fun exportToPdf(
        context: Context,
        inspection: InspectionEntity,
        declarations: List<DeclarationEntity>,
        checks: List<ComplianceCheckEntity>
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            val headerPaint = Paint().apply {
                color = Color.rgb(24, 43, 73)
                textSize = 15f
                isFakeBoldText = true
            }
            val subHeaderPaint = Paint().apply {
                color = Color.rgb(70, 80, 95)
                textSize = 10f
            }
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
            }
            val boldPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                isFakeBoldText = true
            }

            var y = 40f

            // Header Banner
            paint.color = Color.rgb(238, 242, 246)
            canvas.drawRect(20f, 20f, 575f, 90f, paint)

            canvas.drawText("GOVERNMENT OF INDIA - DEPARTMENT OF LEGAL METROLOGY", 30f, 50f, headerPaint)
            canvas.drawText("OFFICIAL STATUTORY INSPECTION REPORT & COMPLIANCE MEMO", 30f, 70f, subHeaderPaint)
            y = 110f

            // Notice Summary
            canvas.drawText("INSPECTION METADATA", 30f, y, boldPaint)
            y += 18f
            canvas.drawText("Notice Ref No: ${inspection.noticeNumber ?: inspection.inspectionNumber}", 30f, y, textPaint)
            canvas.drawText("Timestamp: ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(inspection.timestamp))}", 300f, y, textPaint)
            y += 15f
            canvas.drawText("Commodity / Product: ${inspection.productName}", 30f, y, textPaint)
            canvas.drawText("Manufacturer / Brand: ${inspection.brand}", 300f, y, textPaint)
            y += 15f
            canvas.drawText("Status: ${inspection.status.displayName}", 30f, y, boldPaint)
            canvas.drawText("Compliance Score: ${inspection.complianceScore}% | Penalty: ₹${inspection.penaltyAmount}", 300f, y, boldPaint)
            y += 25f

            // Statutory Declarations Table
            canvas.drawText("EXTRACTED STATUTORY DECLARATIONS (PACKAGED COMMODITIES RULES, 2011)", 30f, y, boldPaint)
            y += 18f

            paint.color = Color.rgb(240, 240, 240)
            canvas.drawRect(30f, y - 12f, 565f, y + 6f, paint)
            canvas.drawText("Declaration Type", 35f, y, boldPaint)
            canvas.drawText("Captured Value", 220f, y, boldPaint)
            canvas.drawText("Confidence", 480f, y, boldPaint)
            y += 20f

            declarations.take(8).forEach { decl ->
                canvas.drawText(decl.fieldName, 35f, y, textPaint)
                val valText = if (decl.extractedValue.length > 35) decl.extractedValue.take(35) + "..." else decl.extractedValue
                canvas.drawText(valText, 220f, y, textPaint)
                canvas.drawText("${(decl.confidence * 100).toInt()}%", 480f, y, textPaint)
                y += 16f
            }

            y += 15f
            // Statutory Rules Audit Summary
            canvas.drawText("STATUTORY RULE EVALUATION SUMMARY", 30f, y, boldPaint)
            y += 18f

            checks.take(6).forEach { check ->
                val title = if (check.ruleTitle.length > 50) check.ruleTitle.take(50) + "..." else check.ruleTitle
                canvas.drawText("• $title: ${check.status.displayName}", 35f, y, textPaint)
                y += 15f
            }

            // Footer / Officer Signature
            y = 780f
            canvas.drawLine(30f, y, 565f, y, subHeaderPaint)
            y += 20f
            canvas.drawText("Inspecting Officer: ${inspection.officerName}", 30f, y, textPaint)
            canvas.drawText("Digitally Validated & Signed", 400f, y, boldPaint)

            pdfDocument.finishPage(page)

            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
            val file = File(dir, "Legal_Metrology_Inspection_${inspection.inspectionNumber}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            openOrShareFile(context, file, "application/pdf")
        } catch (e: Exception) {
            Toast.makeText(context, "PDF Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Exports inspection details to an editable CSV format.
     */
    fun exportToCsv(
        context: Context,
        inspection: InspectionEntity,
        declarations: List<DeclarationEntity>,
        checks: List<ComplianceCheckEntity>
    ) {
        try {
            val csvBuilder = java.lang.StringBuilder()
            csvBuilder.append("Legal Metrology Statutory Inspection Report\n")
            csvBuilder.append("Notice Ref,${inspection.noticeNumber ?: inspection.inspectionNumber}\n")
            csvBuilder.append("Product Name,${inspection.productName}\n")
            csvBuilder.append("Brand / Manufacturer,${inspection.brand}\n")
            csvBuilder.append("Compliance Status,${inspection.status.displayName}\n")
            csvBuilder.append("Compliance Score,${inspection.complianceScore}%\n")
            csvBuilder.append("Assessed Fine,₹${inspection.penaltyAmount}\n")
            csvBuilder.append("Inspector,${inspection.officerName}\n\n")

            csvBuilder.append("Declaration Type,Extracted Value,Confidence\n")
            declarations.forEach { decl ->
                csvBuilder.append("\"${decl.fieldName}\",\"${decl.extractedValue}\",${decl.confidence}\n")
            }

            csvBuilder.append("\nStatutory Rule,Section,Status,Observation\n")
            checks.forEach { check ->
                csvBuilder.append("\"${check.ruleTitle}\",\"${check.legalSection}\",\"${check.status.displayName}\",\"${check.findingMessage}\"\n")
            }

            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
            val file = File(dir, "Legal_Metrology_Inspection_${inspection.inspectionNumber}.csv")
            file.writeText(csvBuilder.toString())

            openOrShareFile(context, file, "text/csv")
        } catch (e: Exception) {
            Toast.makeText(context, "CSV Export failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openOrShareFile(context: Context, file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export / Share Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "File created at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
