package com.example.data.export

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.FinancialSummary
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.security.CryptoManager
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExportManager {

    fun exportToCsv(
        context: Context,
        transactions: List<TransactionEntity>,
        dateRangeLabel: String
    ): Uri {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(exportDir, "FinVault_Ledger_$timeStamp.csv")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        FileWriter(file).use { writer ->
            // Metadata header
            writer.append("# FinVault Encrypted Ledger Export\n")
            writer.append("# Date Range: $dateRangeLabel\n")
            writer.append("# Exported On: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
            writer.append("# Total Records: ${transactions.size}\n\n")

            // CSV Columns
            writer.append("\"ID\",\"Date\",\"Time\",\"Type\",\"Title\",\"Category\",\"Amount ($)\",\"Tags\",\"Calendar Sync Status\",\"Decrypted Note\"\n")

            for (tx in transactions) {
                val dateStr = dateFormat.format(Date(tx.timestamp))
                val timeStr = timeFormat.format(Date(tx.timestamp))
                val typeStr = if (tx.type == TransactionType.INCOME) "EARNING / INCOME" else "EXPENSE"
                val cleanTitle = tx.title.replace("\"", "\"\"")
                val cleanCat = tx.category.replace("\"", "\"\"")
                val tagsStr = tx.tags.joinToString(";") { "#$it" }.replace("\"", "\"\"")
                val syncStatus = if (tx.calendarEventId != null) "Synced (ID: ${tx.calendarEventId})" else "Local Only"
                val noteDecrypted = if (tx.encryptedNote.isNotBlank()) {
                    CryptoManager.decrypt(tx.encryptedNote).replace("\"", "\"\"")
                } else ""

                writer.append("\"${tx.id}\",\"$dateStr\",\"$timeStr\",\"$typeStr\",\"$cleanTitle\",\"$cleanCat\",\"${String.format(Locale.US, "%.2f", tx.amount)}\",\"$tagsStr\",\"$syncStatus\",\"$noteDecrypted\"\n")
            }
            writer.flush()
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun exportToPdf(
        context: Context,
        transactions: List<TransactionEntity>,
        dateRangeLabel: String,
        summary: FinancialSummary
    ): Uri {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(exportDir, "FinVault_Report_$timeStamp.pdf")

        val document = PdfDocument()
        val pageWidth = 595 // A4 standard width (pt)
        val pageHeight = 842 // A4 standard height (pt)

        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42) // Dark obsidian
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 10f
            isAntiAlias = true
        }

        val headerBoxPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val rowTextPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 8.5f
            isAntiAlias = true
        }

        val incomeTextPaint = Paint().apply {
            color = Color.rgb(16, 185, 129) // Emerald
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val expenseTextPaint = Paint().apply {
            color = Color.rgb(244, 63, 94) // Rose
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 0.8f
        }

        val cardBgPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val cardBorderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Calculate pages
        val itemsPerPageFirst = 14
        val itemsPerPageOther = 22
        val totalTransactions = transactions.size
        val totalPages = if (totalTransactions <= itemsPerPageFirst) {
            1
        } else {
            1 + Math.ceil((totalTransactions - itemsPerPageFirst) / itemsPerPageOther.toDouble()).toInt()
        }

        var currentIndex = 0
        for (pageIndex in 1..totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            var y = 40f

            if (pageIndex == 1) {
                // Draw Header
                canvas.drawText("FINVAULT • FINANCIAL INTELLIGENCE REPORT", 36f, y, titlePaint)
                y += 16f
                canvas.drawText("Date Range: $dateRangeLabel  |  Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}  |  100% Offline Encrypted", 36f, y, subtitlePaint)
                y += 20f

                // Draw Summary Box
                val boxRect = RectF(36f, y, pageWidth - 36f, y + 68f)
                headerBoxPaint.color = Color.rgb(248, 250, 252)
                canvas.drawRoundRect(boxRect, 8f, 8f, headerBoxPaint)
                cardBorderPaint.color = Color.rgb(203, 213, 225)
                canvas.drawRoundRect(boxRect, 8f, 8f, cardBorderPaint)

                // 3 summary pillars: Total Income, Total Expenses, Net Savings
                val colW = (pageWidth - 72f) / 3f

                // Income pillar
                var colX = 46f
                val labelPaint = Paint().apply { color = Color.rgb(100, 116, 139); textSize = 9f; isAntiAlias = true }
                canvas.drawText("TOTAL EARNINGS", colX, y + 22f, labelPaint)
                val bigIncPaint = Paint().apply { color = Color.rgb(16, 185, 129); textSize = 15f; isFakeBoldText = true; isAntiAlias = true }
                canvas.drawText("+\$${String.format(Locale.US, "%.2f", summary.totalIncome)}", colX, y + 44f, bigIncPaint)

                // Expense pillar
                colX += colW
                canvas.drawText("TOTAL EXPENSES", colX, y + 22f, labelPaint)
                val bigExpPaint = Paint().apply { color = Color.rgb(244, 63, 94); textSize = 15f; isFakeBoldText = true; isAntiAlias = true }
                canvas.drawText("-\$${String.format(Locale.US, "%.2f", summary.totalExpense)}", colX, y + 44f, bigExpPaint)

                // Net Savings pillar
                colX += colW
                canvas.drawText("NET SAVINGS (${String.format(Locale.US, "%.0f", summary.savingsRate)}%)", colX, y + 22f, labelPaint)
                val netColor = if (summary.netSavings >= 0) Color.rgb(16, 185, 129) else Color.rgb(244, 63, 94)
                val bigNetPaint = Paint().apply { color = netColor; textSize = 15f; isFakeBoldText = true; isAntiAlias = true }
                val netPrefix = if (summary.netSavings >= 0) "+" else ""
                canvas.drawText("$netPrefix\$${String.format(Locale.US, "%.2f", summary.netSavings)}", colX, y + 44f, bigNetPaint)

                y += 86f
            } else {
                // Secondary Page Header
                canvas.drawText("FinVault Financial Report • $dateRangeLabel (Cont.)", 36f, y, subtitlePaint)
                y += 24f
            }

            // Table Header Bar
            val thBg = RectF(36f, y, pageWidth - 36f, y + 22f)
            cardBgPaint.color = Color.rgb(226, 232, 240)
            canvas.drawRoundRect(thBg, 4f, 4f, cardBgPaint)

            val ty = y + 15f
            canvas.drawText("DATE", 46f, ty, tableHeaderPaint)
            canvas.drawText("TYPE", 112f, ty, tableHeaderPaint)
            canvas.drawText("DESCRIPTION / TITLE", 165f, ty, tableHeaderPaint)
            canvas.drawText("CATEGORY & TAGS", 345f, ty, tableHeaderPaint)
            canvas.drawText("AMOUNT", 495f, ty, tableHeaderPaint)

            y += 28f

            val limitThisPage = if (pageIndex == 1) itemsPerPageFirst else itemsPerPageOther
            var pageItemCount = 0

            while (currentIndex < transactions.size && pageItemCount < limitThisPage) {
                val tx = transactions[currentIndex]
                val dateStr = dateFormat.format(Date(tx.timestamp))
                val isIncome = tx.type == TransactionType.INCOME

                // Alternating row subtle bg
                if (pageItemCount % 2 == 1) {
                    val rowRect = RectF(36f, y - 10f, pageWidth - 36f, y + 14f)
                    cardBgPaint.color = Color.rgb(248, 250, 252)
                    canvas.drawRect(rowRect, cardBgPaint)
                }

                // Date
                canvas.drawText(dateStr, 46f, y + 2f, rowTextPaint)

                // Type Badge
                val typeStr = if (isIncome) "INCOME" else "EXPENSE"
                val tPaint = if (isIncome) incomeTextPaint else expenseTextPaint
                canvas.drawText(typeStr, 112f, y + 2f, tPaint)

                // Title (Truncate if too long)
                val cleanTitle = if (tx.title.length > 28) tx.title.take(26) + "..." else tx.title
                canvas.drawText(cleanTitle, 165f, y + 2f, rowTextPaint)

                // Category & Tags
                val tagsPreview = if (tx.tags.isNotEmpty()) " (${tx.tags.joinToString { "#$it" }})" else ""
                val catAndTags = tx.category + tagsPreview
                val cleanCat = if (catAndTags.length > 26) catAndTags.take(24) + "..." else catAndTags
                canvas.drawText(cleanCat, 345f, y + 2f, subtitlePaint)

                // Amount
                val amtStr = if (isIncome) "+$" + String.format(Locale.US, "%.2f", tx.amount) else "-$" + String.format(Locale.US, "%.2f", tx.amount)
                canvas.drawText(amtStr, 495f, y + 2f, tPaint)

                // Divider line
                canvas.drawLine(36f, y + 14f, pageWidth - 36f, y + 14f, linePaint)

                y += 24f
                currentIndex++
                pageItemCount++
            }

            // Footer
            val footerY = pageHeight - 30f
            canvas.drawLine(36f, footerY - 10f, pageWidth - 36f, footerY - 10f, linePaint)
            canvas.drawText("FinVault On-Device Private Ledger  •  End-to-End Encrypted  •  Zero Telemetry", 36f, footerY, subtitlePaint)
            val pageNumStr = "Page $pageIndex of $totalPages"
            canvas.drawText(pageNumStr, pageWidth - 36f - subtitlePaint.measureText(pageNumStr), footerY, subtitlePaint)

            document.finishPage(page)
        }

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun shareExport(context: Context, uri: Uri, format: com.example.data.model.ExportFormat) {
        val mimeType = if (format == com.example.data.model.ExportFormat.PDF) "application/pdf" else "text/csv"
        shareExportFile(context, uri, mimeType, "Export FinVault ${format.name}")
    }

    fun shareExportFile(context: Context, uri: Uri, mimeType: String, chooserTitle: String = "Export Financial Records") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
