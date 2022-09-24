package bokarev.st.stretchceilingcalculator

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import bokarev.st.stretchceilingcalculator.entities.PdfToDisplay
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.math.roundToInt

object GeneratePdf {

    fun generate(bmp: Bitmap, pdfToDisplay: PdfToDisplay): String {

        val tag = "GeneratePdf"
        val pdfDocument = PdfDocument()
        val pageWith = 2000
        val marginTop = 700
        val marginBottom = 1400


        var sum = 0.0
        var ind = 0
        var globalIndexPage = 1


        if (pdfToDisplay.dataList.size < 30) {
            globalIndexPage = 1
        } else if (pdfToDisplay.dataList.size < 50) {
            globalIndexPage = 2
        } else if (pdfToDisplay.dataList.size < 80) {
            globalIndexPage = 3
        } else if (pdfToDisplay.dataList.size < 110) {
            globalIndexPage = 4
        } else if (pdfToDisplay.dataList.size < 141) {
            globalIndexPage = 5
        } else if (pdfToDisplay.dataList.size < 171) {
            globalIndexPage = 6
        } else if (pdfToDisplay.dataList.size < 191) {
            globalIndexPage = 7
        } else if (pdfToDisplay.dataList.size >= 191) {
            globalIndexPage = 8
        }
        val separator = 150
        var heightY = 850f

        // на будущее оптимизировать подбор высоты документа от кол-ва строк. Формула База + кол-во строк * высоту строки
        var pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                pdfToDisplay.dataList.size  * 30 + marginTop + marginBottom,
                1
            ).create()
        var pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = pdfPage.canvas
        var titlePaint = Paint()
        var myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${pdfToDisplay.dataList[0].ClientName}")
        var scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета: 1ая страница из 5ти страниц", pageWith / 2f, 100f, titlePaint)


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val nameClient = pdfToDisplay.dataList[0].ClientName
        if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                550f,
                titlePaint
            )
            if (pdfToDisplay.address.length > 30) {
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
                canvas.drawText(
                    pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                    pageWith / 2f,
                    655f,
                    titlePaint
                )
            } else
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address}",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
        } else
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                600f,
                titlePaint
            )


        val part1: MutableList<ClientAndEstimateModification> = arrayListOf()
        val part2: MutableList<ClientAndEstimateModification> = arrayListOf()
        val part3: MutableList<ClientAndEstimateModification> = arrayListOf()
        val part4: MutableList<ClientAndEstimateModification> = arrayListOf()
        val part5: MutableList<ClientAndEstimateModification> = arrayListOf()

        for (i in (0 until pdfToDisplay.dataList.size * 1 / 5).withIndex()) {
            part1.add(pdfToDisplay.dataList[ind])
            ind++
        }

        /* var counter = 1
         for (i in 1..globalIndexPage) {
             val part: MutableList<ClientAndEstimateModification> = arrayListOf()
             part.clear()

             // одна страница примерно 40 записей
             // (pdfToDisplay.dataList.size / globalIndexPage) = кол-во страниц в документе
             Log.d("mytag", "end granitcha = ${(pdfToDisplay.dataList.size / (globalIndexPage + 1)) * i}")
             for (j in (ind until (pdfToDisplay.dataList.size / (globalIndexPage + 1)) * i)) {
                 part.add(pdfToDisplay.dataList[ind])
                 Log.d("mytag", "${pdfToDisplay.dataList[ind].CategoryName} = name category")
                 ind++
             }


             parts.add(part)

             counter++

             val separator = 150
             var heightY = 850f

             // на будущее оптимизировать подбор высоты документа от кол-ва строк. Формула База + кол-во строк * высоту строки
             var pageInfo: PdfDocument.PageInfo =
                 PdfDocument.PageInfo.Builder(
                     pageWith,
                     (pdfToDisplay.dataList.size / globalIndexPage) * i * 100 + marginBottom + marginTop,
                     i
                 ).create()
             var pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
             var canvas: Canvas = pdfPage.canvas
             var titlePaint = Paint()
             var myPaint = Paint()

             Log.d("mytag", "REST STEPAN ${pdfToDisplay.dataList[0].ClientName}")
             var scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
             canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

             titlePaint.textAlign = Paint.Align.CENTER
             titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
             titlePaint.textSize = 70.0f
             canvas.drawText("Смета", pageWith / 2f, 300f, titlePaint)

             titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
             val nameClient = pdfToDisplay.dataList[0].ClientName
             if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
                 canvas.drawText(
                     "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                     pageWith / 2f,
                     550f,
                     titlePaint
                 )
                 if (pdfToDisplay.address.length > 30) {
                     canvas.drawText(
                         "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                         pageWith / 2f,
                         600f,
                         titlePaint
                     )
                     canvas.drawText(
                         pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                         pageWith / 2f,
                         655f,
                         titlePaint
                     )
                 } else
                 canvas.drawText(
                     "Адрес клиента ${pdfToDisplay.address}",
                     pageWith / 2f,
                     600f,
                     titlePaint
                 )
             } else
                 canvas.drawText(
                     "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
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


             for (k in part) {
                 canvas.drawText(k.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
                 if (k.CategoryName.split("").size > 33) {
                     val substr1 = k.CategoryName.substring(0, 32)
                     val substr2 = k.CategoryName.substring(32, k.CategoryName.length)
                     canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                     canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
                 } else
                     canvas.drawText(k.CategoryName, 880f, heightY + separator, myPaint)
                 canvas.drawText("${k.Count}", 1050f, heightY + separator, myPaint)
                 canvas.drawText(k.Price.toString(), 1350f, heightY + separator, myPaint)
                 canvas.drawText(
                     (k.Count * k.Price).toString(),
                     pageWith - 50f,
                     heightY + separator,
                     myPaint
                 )
                 heightY += separator
                 sum += k.Count * k.Price
             }


             //линию поправить чтобы она была в конце таблицы
             // см База + кол-во строк * высоту строк
             val mun = 180
             /*canvas.drawLine(
                 50f,
                 (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
                 pageWith - 50f,
                 (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
                 myPaint
             )*/

             myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
             myPaint.color = Color.rgb(247, 147, 30)




             titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

             if (i == globalIndexPage) {
                 //линию поправить чтобы она была в конце таблицы
                 // см База + кол-во строк * высоту строк

                 /*canvas.drawLine(
                     50f,
                     ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop).toFloat(),
                     pageWith - 50f,
                     ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop).toFloat(),
                     myPaint
                 )*/

                 myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                 myPaint.color = Color.rgb(247, 147, 30)

                 canvas.drawText(
                     "Сумма заказа: ${(sum * 100f).roundToInt() / 100f}",
                     (pageWith - pageWith / 3).toFloat(),
                     ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop).toFloat(),
                     myPaint
                 )


                 titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

                 canvas.drawText(
                     "***  Конец сметы  ***",
                     pageWith / 2f,
                     ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop +  200).toFloat(),
                     titlePaint
                 )


                 //pdfDocument.finishPage(pdfPage)

             }

             pdfDocument.finishPage(pdfPage)


         }
 */

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


        for (k in part1) {
            canvas.drawText(k.NameTypeOfWork, 880f, heightY + separator / 2 + 20, myPaint)
            if (k.CategoryName.split("").size > 33) {
                val substr1 = k.CategoryName.substring(0, 32)
                val substr2 = k.CategoryName.substring(32, k.CategoryName.length)
                canvas.drawText(substr1, 880f, heightY + separator, myPaint)
                canvas.drawText(substr2, 880f, heightY + separator + 40, myPaint)
            } else
                canvas.drawText(k.CategoryName, 880f, heightY + separator, myPaint)
            canvas.drawText("${k.Count}", 1050f, heightY + separator, myPaint)
            canvas.drawText(k.Price.toString(), 1350f, heightY + separator, myPaint)
            canvas.drawText(
                (k.Count * k.Price).toString(),
                pageWith - 50f,
                heightY + separator,
                myPaint
            )
            heightY += separator
            sum += k.Count * k.Price
        }


        //линию поправить чтобы она была в конце таблицы
        // см База + кол-во строк * высоту строк
        val mun = 180
        /*canvas.drawLine(
            50f,
            (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
            pageWith - 50f,
            (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
            myPaint
        )*/

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        pdfDocument.finishPage(pdfPage)



        for (i in (pdfToDisplay.dataList.size / 5 until pdfToDisplay.dataList.size * 2 / 5).withIndex()) {
            part2.add(pdfToDisplay.dataList[ind])
            ind++
        }
        for (i in (pdfToDisplay.dataList.size * 2 / 5 until pdfToDisplay.dataList.size * 3 / 5).withIndex()) {
            part3.add(pdfToDisplay.dataList[ind])
            ind++
        }
        for (i in (pdfToDisplay.dataList.size * 3 / 5 until pdfToDisplay.dataList.size * 4 / 5).withIndex()) {
            part4.add(pdfToDisplay.dataList[ind])
            ind++
        }

        for (i in (pdfToDisplay.dataList.size * 4 / 5 until pdfToDisplay.dataList.size).withIndex()) {
            part5.add(pdfToDisplay.dataList[ind])
            ind++
        }


        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                pdfToDisplay.dataList.size * 30 + marginBottom + marginTop,
                2
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${pdfToDisplay.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета: 2ая страница из 5ти", pageWith / 2f, 100f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                550f,
                titlePaint
            )
            if (pdfToDisplay.address.length > 30) {
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
                canvas.drawText(
                    pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                    pageWith / 2f,
                    655f,
                    titlePaint
                )
            } else
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address}",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
        } else
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                600f,
                titlePaint
            )

       // titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        /*canvas.drawText(
            "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )*/

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

        /* canvas.drawLine(
             50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             pageWith - 50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             myPaint
         )*/

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)



        pdfDocument.finishPage(pdfPage)

        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                pdfToDisplay.dataList.size * 30 + marginBottom + marginTop,
                3
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${pdfToDisplay.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета: 3я страница из 5ти", pageWith / 2f, 100f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                550f,
                titlePaint
            )
            if (pdfToDisplay.address.length > 30) {
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
                canvas.drawText(
                    pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                    pageWith / 2f,
                    655f,
                    titlePaint
                )
            } else
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address}",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
        } else
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                600f,
                titlePaint
            )

       // titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        /* canvas.drawText(
             "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
             pageWith / 2f,
             600f,
             titlePaint
         )*/

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

        /* canvas.drawLine(
             50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             pageWith - 50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             myPaint
         )*/

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)




        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)




        pdfDocument.finishPage(pdfPage)
        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                pdfToDisplay.dataList.size * 30 + marginBottom + marginTop,
                4
            ).create()
        pdfPage = pdfDocument.startPage(pageInfo)
        canvas = pdfPage.canvas
        titlePaint = Paint()
        myPaint = Paint()

        Log.d("mytag", "REST STEPAN ${pdfToDisplay.dataList[0].ClientName}")
        scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
        canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 70.0f
        canvas.drawText("Смета: 4ая страница из 5ти", pageWith / 2f, 100f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                550f,
                titlePaint
            )
            if (pdfToDisplay.address.length > 30) {
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
                canvas.drawText(
                    pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                    pageWith / 2f,
                    655f,
                    titlePaint
                )
            } else
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address}",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
        } else
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                600f,
                titlePaint
            )

       // titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        /*canvas.drawText(
            "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )*/

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

        /* canvas.drawLine(
             50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             pageWith - 50f,
             (pdfToDisplay.dataList.size / 5 * mun + marginTop).toFloat(),
             myPaint
         )*/

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)


        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)


        pdfDocument.finishPage(pdfPage)
        pageInfo =
            PdfDocument.PageInfo.Builder(
                pageWith,
                pdfToDisplay.dataList.size * 30 + marginBottom + marginTop,
                5
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
        canvas.drawText("Смета: 5ая страница из 5ти", pageWith / 2f, 100f, titlePaint)

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        if ("Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}".length > 30) {
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                550f,
                titlePaint
            )
            if (pdfToDisplay.address.length > 30) {
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address.substring(0, 30)}-",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
                canvas.drawText(
                    pdfToDisplay.address.substring(30, pdfToDisplay.address.length),
                    pageWith / 2f,
                    655f,
                    titlePaint
                )
            } else
                canvas.drawText(
                    "Адрес клиента ${pdfToDisplay.address}",
                    pageWith / 2f,
                    600f,
                    titlePaint
                )
        } else
            canvas.drawText(
                "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
                pageWith / 2f,
                600f,
                titlePaint
            )

       //        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        /*canvas.drawText(
            "Клиент ${nameClient}, ${pdfToDisplay.address}, ${pdfToDisplay.tel}",
            pageWith / 2f,
            600f,
            titlePaint
        )*/

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

        /*canvas.drawLine(
            50f,
            ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop).toFloat(),
            pageWith - 50f,
            ((pdfToDisplay.dataList.size / globalIndexPage) * i * mun + marginTop).toFloat(),
            myPaint
        )*/

        myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        myPaint.color = Color.rgb(247, 147, 30)

        canvas.drawText(
            "Сумма заказа: ${(sum * 100f).roundToInt() / 100f} руб.",
            (pageWith - pageWith / 3).toFloat(),
            250f,
            myPaint
        )
        // ((pdfToDisplay.dataList.size / globalIndexPage) * 5 * mun + marginTop).toFloat(),

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)

        canvas.drawText(
            "***  Конец сметы  ***",
            pageWith / 2f,
            ((pdfToDisplay.dataList.size / globalIndexPage) * 5 * mun + marginTop + 200).toFloat(),
            titlePaint
        )


        pdfDocument.finishPage(pdfPage)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Смета_${pdfToDisplay.dataList[0].ClientName}_" + LocalDateTime.now().month + "_" + LocalDateTime.now().dayOfMonth + "_" + LocalDateTime.now().hour + "_" + LocalDateTime.now().minute + ".pdf"
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