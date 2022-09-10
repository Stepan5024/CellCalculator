package bokarev.st.stretchceilingcalculator

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import bokarev.st.stretchceilingcalculator.models.ClientAndEstimateMidifation

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

object GeneratePdf {

    fun generate(bmp: Bitmap, receipt: Receipt): String {

        val tag = "GeneratePdf"
        val pdfDocument = PdfDocument()
        val pageWith = 1900
        val marginTop = 700
        val marginBottom = 1350

        // на будущее оптимизировать подбор высоты документа от кол-ва строк. Формула База + кол-во строк * высоту строки
        var pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                receipt.dataList.size * 30 + marginBottom + marginTop,
                1
            ).create()
        var pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = pdfPage.canvas
        var titlePaint = Paint()
        var myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${receipt.dataList[0].ClientName}")
        var scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val nameClient = receipt.dataList[0].ClientName
        canvas.drawText(
            "Клиент ${nameClient}, ${receipt.address}, ${receipt.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )

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
        var sum = 0.0
        val part1: MutableList<ClientAndEstimateMidifation> = arrayListOf()
        val part2: MutableList<ClientAndEstimateMidifation> = arrayListOf()
        val part3: MutableList<ClientAndEstimateMidifation> = arrayListOf()
        val part4: MutableList<ClientAndEstimateMidifation> = arrayListOf()
        val part5: MutableList<ClientAndEstimateMidifation> = arrayListOf()

        var ind = 0
        for (i in (0 until receipt.dataList.size / 5)) {
            part1.add(receipt.dataList[ind])
            ind++
        }


        for (i in (receipt.dataList.size / 5 until receipt.dataList.size * 2 / 5).withIndex()) {
            part2.add(receipt.dataList[ind])
            ind++
        }
        for (i in (receipt.dataList.size * 2 / 5 until receipt.dataList.size * 3 / 5).withIndex()) {
            part3.add(receipt.dataList[ind])
            ind++
        }
        for (i in (receipt.dataList.size * 3 / 5 until receipt.dataList.size*4/5).withIndex()) {
            part4.add(receipt.dataList[ind])
            ind++
        }

        for (i in (receipt.dataList.size * 4 / 5 until receipt.dataList.size).withIndex()) {
            part5.add(receipt.dataList[ind])
            ind++
        }

        val separator = 150
        for (i in part1) {
            canvas.drawText(i.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (i.CategoryName.split("").size > 33) {
                val substr1 = i.CategoryName.substring(0, 32)
                val substr2 = i.CategoryName.substring(32, i.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(i.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText("${i.Count}", 1050f, heightY + separator, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк
        val mun = 170
        canvas.drawLine(
            50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)


        pdfDocument.finishPage(pdfPage)

        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                receipt.dataList.size * 30 + marginBottom + marginTop,
                2
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${receipt.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "Клиент ${nameClient}, ${receipt.address}, ${receipt.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )

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

        heightY = 850f



        for (i in part2) {
            canvas.drawText(i.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (i.CategoryName.split("").size > 33) {
                val substr1 = i.CategoryName.substring(0, 32)
                val substr2 = i.CategoryName.substring(32, i.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(i.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText(i.Count.toString(), 1050f, heightY + separator, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк

        canvas.drawLine(
            50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)



        pdfDocument.finishPage(pdfPage)

        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                receipt.dataList.size * 30 + marginBottom + marginTop,
                2
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${receipt.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "Клиент ${nameClient}, ${receipt.address}, ${receipt.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )

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

        heightY = 850f



        for (i in part3) {
            canvas.drawText(i.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (i.CategoryName.split("").size > 33) {
                val substr1 = i.CategoryName.substring(0, 32)
                val substr2 = i.CategoryName.substring(32, i.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(i.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText(i.Count.toString(), 1050f, heightY + separator, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк

        canvas.drawLine(
            50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)




        pdfDocument.finishPage(pdfPage)
        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                receipt.dataList.size * 30 + marginBottom + marginTop,
                2
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${receipt.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "Клиент ${nameClient}, ${receipt.address}, ${receipt.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )

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

        heightY = 850f



        for (i in part4) {
            canvas.drawText(i.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (i.CategoryName.split("").size > 33) {
                val substr1 = i.CategoryName.substring(0, 32)
                val substr2 = i.CategoryName.substring(32, i.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(i.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText(i.Count.toString(), 1050f, heightY + separator, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк

        canvas.drawLine(
            50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)


        pdfDocument.finishPage(pdfPage)
        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                receipt.dataList.size * 30 + marginBottom + marginTop,
                2
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета", pageWith / 2f, 370f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "Клиент ${nameClient}, ${receipt.address}, ${receipt.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )

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

        heightY = 850f


        for (i in part5) {
            canvas.drawText(i.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (i.CategoryName.split("").size > 33) {
                val substr1 = i.CategoryName.substring(0, 32)
                val substr2 = i.CategoryName.substring(32, i.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(i.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText(i.Count.toString(), 1050f, heightY + separator, myPaint)
            canvas.drawText(i.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (i.Count * i.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += i.Count * i.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк

        canvas.drawLine(
            50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (receipt.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)

        canvas.drawText(
            "Сумма заказа: $sum",
            (pageWith - pageWith / 3).toFloat(),
            (receipt.dataList.size / 5 * mun + marginTop + 200).toFloat(),
            myPaint
        )


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "***  Конец сметы  ***",
            pageWith / 2f,
            (receipt.dataList.size / 5 * mun + marginTop + 500).toFloat(),
            titlePaint
        )


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