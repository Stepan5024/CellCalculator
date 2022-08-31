package bokarev.st.myapplication

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

object GeneratePdf {

     fun generate(bmp: Bitmap, receipt: Receipt): String {

        val Tag = "GeneratePdf"
        val pdfDocument = PdfDocument()
        val pageWith = 1200

        val pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(pageWith, 2010, 1).create()
        val pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = pdfPage.canvas
        var titlePaint = Paint()
        var myPaint = Paint()

        val scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("VALIANCE", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Receipt", pageWith / 2f, 600f, titlePaint)

        myPaint.style = Paint.Style.STROKE
        myPaint.strokeWidth = 4f
        canvas.drawRect(20f, 770f, pageWith - 20f, 850f, myPaint)

        myPaint.textAlign = Paint.Align.LEFT
        myPaint.style = Paint.Style.FILL
        myPaint.textSize = 60.0f
        canvas.drawText("Item", 50f, 830f, myPaint)
        canvas.drawText("Count", 350f, 830f, myPaint)
        canvas.drawText("Price", 650f, 830f, myPaint)
        canvas.drawText("Total", 1000f, 830f, myPaint)

        canvas.drawLine(240f, 770f, 240f, 840f, myPaint)
        canvas.drawLine(560f, 770f, 560f, 840f, myPaint)
        canvas.drawLine(860f, 770f, 860f, 840f, myPaint)

        myPaint.textSize = 50.0f
        canvas.drawText("Product1", 50f, 950f, myPaint)
        canvas.drawText("Product2", 50f, 1050f, myPaint)
        canvas.drawText("Product3", 50f, 1150f, myPaint)
        myPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(receipt.quantityP1.toString(), 450f, 950f, myPaint)
        canvas.drawText(receipt.quantityP2.toString(), 450f, 1050f, myPaint)
        canvas.drawText(receipt.quantityP3.toString(), 450f, 1150f, myPaint)
        canvas.drawText(String.format("%.2f", receipt.priceP1), 800f, 950f, myPaint)
        canvas.drawText(String.format("%.2f", receipt.priceP2), 800f, 1050f, myPaint)
        canvas.drawText(String.format("%.2f", receipt.priceP2), 800f, 1150f, myPaint)
        canvas.drawText(
            String.format("%.2f", receipt.priceP1 * receipt.quantityP1),
            1150f,
            950f,
            myPaint
        )
        canvas.drawText(
            String.format("%.2f", receipt.priceP2 * receipt.quantityP2),
            1150f,
            1050f,
            myPaint
        )
        canvas.drawText(
            String.format("%.2f", receipt.priceP2 * receipt.quantityP3),
            1150f,
            1150f,
            myPaint
        )

        canvas.drawLine(50f, 1180f, pageWith - 20f, 1180f, myPaint)
        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)
        canvas.drawText(
            "Total : " + String.format(
                "%.2f",
                receipt.priceP1 + receipt.priceP3 + receipt.priceP3
            ), 1150f, 1280f, myPaint
        )


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText("***  Visit Again  ***", pageWith / 2f, 1600f, titlePaint)


        pdfDocument.finishPage(pdfPage)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Invoice_" + LocalDateTime.now().minute + "_" + LocalDateTime.now().second + ".pdf"
        )
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
        } catch (e: Exception) {
            pdfDocument.close()
            Log.e(Tag, e.printStackTrace().toString(), e)
            return ""
        }

        return file.name

    }

}