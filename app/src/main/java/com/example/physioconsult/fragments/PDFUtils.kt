package com.example.physioconsult.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PDFUtils{

    fun generatePDF(uriList: MutableList<Uri?>, context: Context, frontResult: Map<String, Double?>, backResult: Map<String, Double?>, sideResult: Map<String, Double?>) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val titleFontSize = 18f
        val contentFontSize = 14f
        val padding = 20
        val imageMaxWidth = 150
        val imageSpacing = 20
        val maxY = pageHeight - padding
        var pageNumber = 1
        var currentY = 0f

        fun startNewPage(): Pair<Canvas, Page> {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            pageNumber++

            val paint = Paint().apply { isAntiAlias = true; textSize = titleFontSize }
            canvas.drawText(
                "Physiotherapy Assessment Report",
                padding.toFloat(),
                padding.toFloat() + titleFontSize,
                paint
            )

            currentY = padding + titleFontSize + 20
            return Pair(canvas, page)
        }

        var (canvas, page) = startNewPage()
        val paint = Paint().apply { isAntiAlias = true }

        for ((index, uri) in uriList.withIndex()) {
            val bitmap = uri?.let { ImageUtils().rotateImageIfRequired(context, it) }
            if (bitmap != null) {
                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val scaledWidth: Int
                val scaledHeight: Int

                if (aspectRatio > 1) {
                    scaledWidth = imageMaxWidth
                    scaledHeight = (imageMaxWidth / aspectRatio).toInt()
                } else {
                    scaledWidth = (imageMaxWidth * aspectRatio).toInt()
                    scaledHeight = imageMaxWidth
                }

                val requiredHeight = scaledHeight + imageSpacing + (contentFontSize * 2 * 6) // Space for angles and lengths
                if (currentY + requiredHeight > maxY) {
                    pdfDocument.finishPage(page)

                    val result = startNewPage()
                    canvas = result.first
                    page = result.second
                }

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
                canvas.drawBitmap(scaledBitmap, padding.toFloat(), currentY, paint)
                val textX = padding + scaledWidth + 20
                var textY = currentY + contentFontSize

                val results = if (index == 0) frontResult else if (index == 1) backResult else sideResult

                // ANGLES
                paint.textSize = contentFontSize
                canvas.drawText("ANGLES", textX.toFloat(), textY, paint)
                textY += contentFontSize + 5
                val angleResults = results.filterKeys { it.contains("Hips") || it.contains("Shoulders") }
                for ((key, value) in angleResults) {
                    canvas.drawText("$key: ${formatToTwoDecimalPlaces(value)}", textX.toFloat(), textY, paint)
                    textY += contentFontSize + 5
                }

                // LENGTHS
                textY += 10
                canvas.drawText("LENGTHS", textX.toFloat(), textY, paint)
                textY += contentFontSize
                val lengthResults = results.filterKeys { !it.contains("Hips") && !it.contains("Shoulders") }
                for ((key, value) in lengthResults) {
                    canvas.drawText("$key: ${formatToTwoDecimalPlaces(value)}", textX.toFloat(), textY, paint)
                    textY += contentFontSize + 5
                }

                currentY += scaledHeight + (textY - currentY) + imageSpacing
            }
        }

        pdfDocument.finishPage(page)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val physioConsultDir = File(downloadsDir, "PhysioConsult")

        if (!physioConsultDir.exists()) {
            physioConsultDir.mkdirs()
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AssessmentReport_$timestamp.pdf"
        val file = File(physioConsultDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Log.i("PDFGeneration", "PDF saved at ${file.absolutePath}")
            Toast.makeText(context, "PDF saved at ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("PDFGeneration", "Error saving PDF", e)
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

//     fun saveToLocalDevice(pdfDocument: PdfDocument, context: Context){
//        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val physioConsultDir = File(downloadsDir, "PhysioConsult")
//
//        if (!physioConsultDir.exists()) {
//            physioConsultDir.mkdirs()
//        }
//
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val fileName = "AssessmentReport_$timestamp.pdf"
//        val file = File(physioConsultDir, fileName)
//
//        try {
//            pdfDocument.writeTo(FileOutputStream(file))
//            Log.i("PDFGeneration", "PDF saved at ${file.absolutePath}")
//            Toast.makeText(context, "PDF saved at ${file.absolutePath}", Toast.LENGTH_LONG).show()
//        } catch (e: Exception) {
//            Log.e("PDFGeneration", "Error saving PDF", e)
//            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
//        } finally {
//            pdfDocument.close()
//        }
//    }

    fun formatToTwoDecimalPlaces(value: Double?): String {
        return value?.let { String.format("%.2f", it) } ?: "err"
    }
}