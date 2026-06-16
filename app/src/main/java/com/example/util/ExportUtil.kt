package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtil {
    fun generateAndSharePDF(context: Context, tasksCompleted: Int, activeProjects: Int, score: Int) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size roughly
        val page = pdfDocument.startPage(pageInfo)
        
        val canvas: Canvas = page.canvas
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 36f
            isFakeBoldText = true
            isAntiAlias = true
        }
        
        val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateString = df.format(Date())

        canvas.drawText("DevPulse Productivity Report", 50f, 100f, titlePaint)
        
        paint.textSize = 16f
        paint.color = Color.DKGRAY
        canvas.drawText("Generated on: $dateString", 50f, 140f, paint)

        // Draw Stats
        paint.color = Color.BLACK
        paint.textSize = 20f
        canvas.drawText("Summary statistics:", 50f, 200f, paint)
        
        paint.textSize = 18f
        canvas.drawText("- Total Completed Tasks: $tasksCompleted", 80f, 240f, paint)
        canvas.drawText("- Active Projects: $activeProjects", 80f, 280f, paint)
        canvas.drawText("- Developer Score: $score", 80f, 320f, paint)

        // Footer
        paint.color = Color.GRAY
        paint.textSize = 14f
        canvas.drawText("Keep up the great work! Generated via DevPulse App.", 50f, 800f, paint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "devpulse_report.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        shareFile(context, file)
    }

    private fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }
}
