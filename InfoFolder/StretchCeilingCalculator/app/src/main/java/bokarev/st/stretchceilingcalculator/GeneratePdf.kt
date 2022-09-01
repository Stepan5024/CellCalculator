package bokarev.st.stretchceilingcalculator

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

object GeneratePdf {

    fun generate(bmp: Bitmap, receipt: Receipt): String {

        val tag = "GeneratePdf"
        val pdfDocument = PdfDocument()
        val pageWith = 1700
        val marginTop = 700
        val marginBottom = 700

        // на будущее оптимизировать подбор высоты документа от кол-ва строк. Формула База + кол-во строк * высоту строки
        val pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(pageWith, receipt.dataList.size * 140 + marginBottom + marginTop, 1).create()
        val pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = pdfPage.canvas
        var titlePaint = Paint()
        var myPaint = Paint()

        val scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Клиент ${receipt.dataList[0].ClientName}, ${receipt.address}, ${receipt.tel}", pageWith / 2f, 600f, titlePaint)

        myPaint.style = Paint.Style.STROKE
        myPaint.strokeWidth = 4f
        canvas.drawRect(20f, 770f, pageWith - 20f, 850f, myPaint)

        myPaint.textAlign = Paint.Align.LEFT
        myPaint.style = Paint.Style.FILL
        myPaint.textSize = 60.0f
        canvas.drawText("Позиция", 240f, 830f, myPaint)
        canvas.drawText("Кол-во", 950f, 830f, myPaint)
        canvas.drawText("Цена", 1200f, 830f, myPaint)
        canvas.drawText("Сумма", 1450f, 830f, myPaint)

        canvas.drawLine(900f, 770f, 900f, 840f, myPaint)
        canvas.drawLine(1175f, 770f, 1175f, 840f, myPaint)
        canvas.drawLine(1400f, 770f, 1400f, 840f, myPaint)

        myPaint.textSize = 50.0f
        myPaint.textAlign = Paint.Align.RIGHT

        var heightY = 850f
        var sum = 0
        for (i in receipt.dataList) {
            canvas.drawText(i.CategoryName, 880f, heightY + 100, myPaint)
            canvas.drawText(i.Count.toString(), 1050f, heightY + 100, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + 100, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + 100,
                myPaint
            )
            heightY += 100
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк
        canvas.drawLine(50f, (receipt.dataList.size * 140 + marginTop).toFloat(), pageWith - 50f, (receipt.dataList.size * 140 + marginTop).toFloat(), myPaint)

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)

        canvas.drawText(
            "Сумма заказа: $sum", (pageWith - pageWith / 3).toFloat(), (receipt.dataList.size * 140 + marginTop + 200).toFloat(), myPaint
        )


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText("***  Конец сметы  ***", pageWith / 2f,  (receipt.dataList.size * 140 + marginTop + 500).toFloat(), titlePaint)


        pdfDocument.finishPage(pdfPage)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Смета_${receipt.dataList[0].ClientName}_" + LocalDateTime.now().month + "_" + LocalDateTime.now().dayOfMonth + "_" + LocalDateTime.now().hour + "_" + LocalDateTime.now().minute + ".pdf"
        )
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
        } catch (e: Exception) {
            pdfDocument.close()
            Log.e(tag, e.printStackTrace().toString(), e)
            return ""
        }

        return file.name

    }

}