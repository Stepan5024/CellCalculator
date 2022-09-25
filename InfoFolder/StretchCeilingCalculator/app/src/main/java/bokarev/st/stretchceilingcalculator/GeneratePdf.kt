package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.TextUtils.substring
import android.util.Log
import bokarev.st.stretchceilingcalculator.entities.PdfToDisplay
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

object GeneratePdf {

    @SuppressLint("SimpleDateFormat")
    fun generate(bmp: Bitmap, pdfToDisplay: PdfToDisplay): String {

        val tag = "GeneratePdf"
        val pdfDocument = PdfDocument()
        val pageWith = 2000f
        val marginTop = 700f
        val marginBottom = 1400f
        val centerHeight = 2000f
        var numberPDFPage = 1

        var sum = 0.0
        val numMar = 150f
        val posMar = 1050f
        val kolMar = 1350f
        val sumMar = 1650f
        var marginElement = 250f
        val indent = 60f
        val startX = 50f
        val deltaHeight = 80f
        val part1: MutableList<ClientAndEstimateModification> = arrayListOf()


        for ((ind, _) in (0 until pdfToDisplay.dataList.size * 1 / 1).withIndex().withIndex()) {
            part1.add(pdfToDisplay.dataList[ind])
        }


        var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(
            pageWith.toInt(),
            (centerHeight + marginTop + marginBottom).toInt(),
            numberPDFPage
        ).create()

        var pdfPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = pdfPage.canvas
        var titlePaint = Paint()
        var myPaint = Paint()

        var previousNameTypeOfWork = ""
        val paragraphIndentation = 20f
        var count = 1
        val countOnOnePage = 28
        for (k in part1) {

            if (count % countOnOnePage == 0) {
                pageInfo =
                    PdfDocument.PageInfo.Builder(
                        pageWith.toInt(),
                        (centerHeight + marginTop + marginBottom).toInt(),
                        numberPDFPage
                    ).create()
                pdfPage = pdfDocument.startPage(pageInfo)
                canvas = pdfPage.canvas
                titlePaint = Paint()
                myPaint = Paint()
                myPaint.textSize = 40.0f
                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            } else if (count == 1) {
                // на будущее оптимизировать подбор высоты документа от кол-ва строк. Формула База + кол-во строк * высоту строки
                //pdfDocument.finishPage(pdfPage)

                val scaledBmp = Bitmap.createScaledBitmap(bmp, 1200, 280, false)
                canvas.drawBitmap(scaledBmp, 0.0f, 0.0f, Paint())

                titlePaint.textAlign = Paint.Align.CENTER
                titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                titlePaint.textSize = 70.0f
                canvas.drawText("Смета", pageWith / 2f, 100f, titlePaint)

                titlePaint.textAlign = Paint.Align.LEFT
                titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                titlePaint.textSize = 50.0f

                val nameClient = pdfToDisplay.dataList[0].ClientName
                val addressClient = pdfToDisplay.address
                val telClient = pdfToDisplay.tel



                drawTextOnCanvas(canvas, "ФИО: $nameClient", startX, marginElement, titlePaint)
                marginElement += indent
                if("Адрес: $addressClient".length > 72) {
                    drawTextOnCanvas(canvas, "Адрес: ${addressClient.substring(0, 65)}", startX, marginElement, titlePaint)
                    drawTextOnCanvas(canvas,
                        addressClient.substring(65, addressClient.length), startX, marginElement, titlePaint)
                }
                else
                drawTextOnCanvas(canvas, "Адрес: $addressClient", startX, marginElement, titlePaint)
                marginElement += indent
                drawTextOnCanvas(
                    canvas,
                    "Номер телефона: $telClient",
                    startX,
                    marginElement,
                    titlePaint
                )
                marginElement += indent
                drawTextOnCanvas(
                    canvas,
                    "Дата составления сметы: ${SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())}",
                    startX,
                    marginElement,
                    titlePaint
                )
                marginElement += 140

                myPaint.style = Paint.Style.STROKE
                myPaint.strokeWidth = 4f
                myPaint.color = Color.rgb(0, 168, 106)

                canvas.drawRect(
                    startX,
                    marginElement,
                    pageWith - startX,
                    marginElement + deltaHeight,
                    myPaint
                )


                canvas.drawLine(numMar, marginElement, numMar, marginElement + deltaHeight, myPaint)
                canvas.drawLine(posMar, marginElement, posMar, marginElement + deltaHeight, myPaint)
                canvas.drawLine(kolMar, marginElement, kolMar, marginElement + deltaHeight, myPaint)
                canvas.drawLine(sumMar, marginElement, sumMar, marginElement + deltaHeight, myPaint)

                myPaint.color = Color.BLACK

                myPaint.textAlign = Paint.Align.CENTER
                myPaint.style = Paint.Style.FILL
                myPaint.textSize = 60.0f
                canvas.drawText(
                    "№",
                    startX + (numMar - startX) / 2f,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )
                canvas.drawText(
                    "Позиция",
                    numMar + (posMar - numMar) / 2f,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )
                canvas.drawText(
                    "Кол-во",
                    posMar + (kolMar - posMar) / 2f,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )
                canvas.drawText(
                    "Цена, ₽",
                    kolMar + (sumMar - kolMar) / 2f,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )
                canvas.drawText(
                    "Сумма, ₽",
                    sumMar + (pageWith - startX - sumMar) / 2f,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )


                marginElement += deltaHeight

                myPaint.textSize = 40.0f
                myPaint.textAlign = Paint.Align.LEFT

            }



            if (previousNameTypeOfWork != k.NameTypeOfWork) {
                // название позиции не совпадает, выводим с новым названием
                marginElement += indent
                myPaint.textSize = 60.0f
                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(k.NameTypeOfWork, startX, marginElement, myPaint)
                myPaint.textSize = 40.0f
                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                marginElement += indent / 1.8f
                previousNameTypeOfWork = k.NameTypeOfWork
            }


            canvas.drawText(
                "$count",
                startX + paragraphIndentation,
                marginElement + deltaHeight / 1.3f,
                myPaint
            )
            var miniVal = 0

            if (k.CategoryName.split("").size > 39) {
                val substr1 = k.CategoryName.substring(0, 32)
                val substr2 = k.CategoryName.substring(32, k.CategoryName.length)
                canvas.drawText(
                    substr1,
                    numMar + paragraphIndentation,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )
                canvas.drawText(
                    substr2,
                    numMar + paragraphIndentation,
                    marginElement + deltaHeight / 1.3f + 40,
                    myPaint
                )
                miniVal = 40

            } else
                canvas.drawText(
                    k.CategoryName,
                    numMar + paragraphIndentation,
                    marginElement + deltaHeight / 1.3f,
                    myPaint
                )



            canvas.drawText(
                "${k.Count}, ${k.UnitsOfMeasurement}",
                posMar + paragraphIndentation,
                marginElement + deltaHeight / 1.3f,
                myPaint
            )
            canvas.drawText(
                k.Price.toString(),
                kolMar + paragraphIndentation,
                marginElement + deltaHeight / 1.3f,
                myPaint
            )
            canvas.drawText(
                (k.Count * k.Price).toInt().toString(),
                sumMar + paragraphIndentation,
                marginElement + deltaHeight / 1.3f,
                myPaint
            )

            myPaint.style = Paint.Style.STROKE
            myPaint.strokeWidth = 4f
            myPaint.color = Color.rgb(0, 168, 106)

            canvas.drawRect(
                startX,
                marginElement,
                pageWith - startX,
                marginElement + deltaHeight + miniVal,
                myPaint
            )
            canvas.drawLine(
                numMar,
                marginElement,
                numMar,
                marginElement + deltaHeight + miniVal,
                myPaint
            )
            canvas.drawLine(
                posMar,
                marginElement,
                posMar,
                marginElement + deltaHeight + miniVal,
                myPaint
            )
            canvas.drawLine(
                kolMar,
                marginElement,
                kolMar,
                marginElement + deltaHeight + miniVal,
                myPaint
            )
            canvas.drawLine(
                sumMar,
                marginElement,
                sumMar,
                marginElement + deltaHeight + miniVal,
                myPaint
            )
            myPaint.style = Paint.Style.FILL
            myPaint.textAlign = Paint.Align.LEFT
            myPaint.color = Color.BLACK
            sum += k.Count * k.Price
            marginElement += deltaHeight + miniVal
            count++
            if (count % countOnOnePage == 0) {
                // делаем переход на новую страницу
                // myPaint.textSize = 40.0f
                marginElement += deltaHeight * 2
                drawTextOnCanvas(
                    canvas,
                    "Страница: $numberPDFPage",
                    pageWith - startX * 8 - "Страница: $numberPDFPage".length,
                    marginElement,
                    myPaint
                )
                marginElement = 150f
                myPaint.textSize = 40.0f
                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                numberPDFPage++

                pdfDocument.finishPage(pdfPage)

            }

            if (count - 1 == part1.size) {
                //линию поправить чтобы она была в конце таблицы
                // см База + кол-во строк * высоту строк
                myPaint.textSize = 40.0f
                marginElement += deltaHeight * 2
                drawTextOnCanvas(
                    canvas,
                    "Страница: $numberPDFPage",
                    pageWith - startX * 8 - "Страница: $numberPDFPage".length,
                    centerHeight + marginTop + marginBottom - 150f,
                    myPaint
                )

                canvas.drawLine(
                    startX,
                    centerHeight + marginTop + marginBottom - 200f,
                    pageWith - startX,
                    centerHeight + marginTop + marginBottom - 200f,
                    myPaint
                )
                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                myPaint.color = Color.rgb(13, 42, 58)

                canvas.drawText(
                    "Сумма заказа: ${sum.toInt()} руб.",
                    startX + numMar,
                    centerHeight + marginTop + marginBottom - 150f,
                    myPaint
                )

                myPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                myPaint.textAlign = Paint.Align.CENTER

                canvas.drawText(
                    "***  Конец сметы  ***",
                    pageWith / 2f,
                    centerHeight + marginTop + marginBottom - 50f,
                    myPaint
                )

            }
        }


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

    private fun drawTextOnCanvas(
        canvas: Canvas,
        stringToPrint: String,
        startX: Float,
        marginElement: Float,
        titlePaint: Paint
    ) {
        canvas.drawText(stringToPrint, startX, marginElement, titlePaint)
    }

}