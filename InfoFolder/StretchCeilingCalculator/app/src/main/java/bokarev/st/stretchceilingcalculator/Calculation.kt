package bokarev.st.stretchceilingcalculator

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import bokarev.st.stretchceilingcalculator.entities.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class Calculation : AppCompatActivity() {

    var previousActivity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculation)


        val tvNameOfClient: TextView = findViewById(R.id.textView2)

        try {

            val client = getClientFromPreviousActivity()
            previousActivity = intent.getStringExtra("PreviousActivity").toString()

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName}"
            )

            if ((previousActivity == "ClientActivity") or (previousActivity == "Clients") or (previousActivity == "TypeOfWorkActivity")) {
                tvNameOfClient.text = client.ClientName
                var sum = 0
                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao = CategoriesDataBase.getInstance(this@Calculation).categoriesDao
                    val someList =
                        dao.selectStrokesEstimateByClient(getClientFromPreviousActivity()._id)

                    for (i in someList) {
                        sum += i.Price * i.Count

                        Log.d(
                            "mytag",
                            "calculation items CategoryName = ${i.CategoryName} Price = ${i.Price} Count = ${i.Count}"
                        )
                    }
                }
                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    //set view

                    val tvSum = findViewById<TextView>(R.id.MainSumCalculation)

                    tvSum.text = "$sum ₽"

                    Log.d("mytag", "Main Thread is Running")
                }

            } else {
                tvNameOfClient.text = ""
            }


        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)

            when (previousActivity) {
                "ClientActivity" -> {
                    intent = Intent(this, ClientActivity::class.java).also {
                        it.putExtra("ClientEntity", getClientFromPreviousActivity())

                        it.putExtra("PreviousActivity", "Calculation")
                    }
                }
                "Clients" -> {
                    intent = Intent(this, Clients::class.java).also {
                        it.putExtra("ClientEntity", setNullClient())
                        it.putExtra("PreviousActivity", "Calculation")
                    }
                }
                "StartActivity" -> {
                    intent = Intent(this, MainActivity::class.java).also {
                        it.putExtra("ClientEntity", setNullClient())
                        it.putExtra("PreviousActivity", "Calculation")
                    }
                }
                "TypeOfWorkActivity" -> {
                    intent = if (getClientFromPreviousActivity().ClientName == "") {
                        Intent(this, MainActivity::class.java).also {
                            it.putExtra("ClientEntity", setNullClient())
                            it.putExtra("PreviousActivity", "Calculation")
                        }
                    } else {
                        Intent(this, Clients::class.java).also {
                            it.putExtra("ClientEntity", setNullClient())
                            it.putExtra("PreviousActivity", "Calculation")
                        }
                    }
                }
            }

            startActivity(intent)

        }

        val btnDemoCalculation: ImageView = findViewById(R.id.btnDemoCalculation)
        btnDemoCalculation.setOnClickListener {
          /*  val toast = Toast.makeText(
                applicationContext,
                "btnDemoCalculation ДЕМО сметы pressed",
                Toast.LENGTH_SHORT
            )
            toast.show()

            */

            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "Calculation")
                it.putExtra("idTypeOfWork", 0)
                it.putExtra("NameTypeOfWork", "Общая смета")
            }
            startActivity(intent)

        }

        val btnExportCalculation: ImageView = findViewById(R.id.btnExportCalculation)
        btnExportCalculation.setOnClickListener {
            /*val toast = Toast.makeText(
                applicationContext,
                "btnExportCalculation экспорт файла pressed",
                Toast.LENGTH_SHORT
            )
            toast.show()*/


            val pdfDocument: PdfDocument = PdfDocument()
            val paint: Paint = Paint()
            val title: Paint = Paint()
            val myPageInfo: PdfDocument.PageInfo? =
                PdfDocument.PageInfo.Builder(792, 1120, 1).create()

            val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)


            // our text which we will be adding in our PDF file.
            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            // which we will be displaying in our PDF file.
            title.textSize = 15F
            // of our text inside our PDF file.
            title.color = ContextCompat.getColor(this@Calculation, R.color.purple_200)
            val canvas: Canvas = myPage.canvas
            val scaledbmp: Bitmap
           // val bmp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_back_btn)
           // scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

          //  canvas.drawBitmap(scaledbmp, 56F, 40F, paint)
            // and then we are passing our variable of paint which is title.
            canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
            canvas.drawText("Geeks for Geeks", 209F, 80F, title)
            title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            title.color = ContextCompat.getColor(this@Calculation, R.color.purple_200)
            title.textSize = 15F
// our text to center of PDF.
            title.textAlign = Paint.Align.CENTER
            canvas.drawText(
                "This is sample document which we have created.",
                396F,
                560F,
                title
            )

// PDF file we will be finishing our page.
            pdfDocument.finishPage(myPage)
            val file: File = File(Environment.getExternalStorageDirectory(), "fileName.pdf")

            try {
                // after creating a file name we will
                // write our PDF file to that location.
                pdfDocument.writeTo(FileOutputStream(file))

                // on below line we are displaying a toast message as PDF file generated..
                Toast.makeText(applicationContext, "PDF file generated..", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // below line is used
                // to handle error
                e.printStackTrace()

                // on below line we are displaying a toast message as fail to generate PDF
                Toast.makeText(applicationContext, "Fail to generate PDF file..", Toast.LENGTH_SHORT)
                    .show()
            }
            // after storing our pdf to that
            // location we are closing our PDF file.
            pdfDocument.close()

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                //type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "fileName.pdf")
               // putExtra(Intent.EXTRA_STREAM, file)
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, "")
            }

            resultLauncher.launch(intent)

        }

        val btnGoToSystem: Button = findViewById(R.id.btnGoToSystem)
        btnGoToSystem.setOnClickListener {
            /*val toast = Toast.makeText(applicationContext, "btnGoToSystem открываем активность тип работы с Системой", Toast.LENGTH_SHORT)
            toast.show()

             */
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "Calculation")
                it.putExtra("idTypeOfWork", 1)
                it.putExtra("NameTypeOfWork", "Система")
            }
            startActivity(intent)
        }
        val btnGoToLight: Button = findViewById(R.id.btnGoToLight)
        btnGoToLight.setOnClickListener {
            //val toast = Toast.makeText(applicationContext, "btnGoToLight открываем активность тип работы с Освещением", Toast.LENGTH_SHORT)
            //toast.show()
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "Calculation")
                it.putExtra("idTypeOfWork", 2)
                it.putExtra("NameTypeOfWork", "Освещение")
            }
            startActivity(intent)
        }
        val btnGoToBonusWork: Button = findViewById(R.id.btnGoToBonusWork)
        btnGoToBonusWork.setOnClickListener {
            /*val toast = Toast.makeText(applicationContext, "btnGoToBonusWork открываем активность тип работы с Доп. работы", Toast.LENGTH_SHORT)
            toast.show()*/
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "Calculation")
                it.putExtra("idTypeOfWork", 3)
                it.putExtra("NameTypeOfWork", "Доп. работы")
            }
            startActivity(intent)
        }
        val btnGoToMaterials: Button = findViewById(R.id.btnGoToMaterials)
        btnGoToMaterials.setOnClickListener {
            /* val toast = Toast.makeText(applicationContext, "btnGoToMaterials открываем активность тип работы Материалы", Toast.LENGTH_SHORT)
             toast.show()*/
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "Calculation")
                it.putExtra("idTypeOfWork", 4)
                it.putExtra("NameTypeOfWork", "Материалы")
            }
            startActivity(intent)
        }

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val uri = data!!.data
                try {
                    //запись файла
                    /*val outPutStream = this.contentResolver.openOutputStream(uri!!)
                    outPutStream?.write("CodeLIb file save Demo".toByteArray())
                    outPutStream?.close()
                    Toast.makeText(this, "File saved", Toast.LENGTH_LONG).show()
*/



                    val sendIntent: Intent = Intent().apply {


                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT, "This is my text to send.\n" +
                                    "This is my text to send.\n" +
                                    "This is my text to send.\n" +
                                    "This is my text to send.\n"
                        )
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "text/plain"

                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                } catch (e: Exception) {
                    print(e.localizedMessage)
                    Toast.makeText(this, "Файл не отправлен", Toast.LENGTH_LONG).show()
                }
            }
        }

    // Kotlin
    override fun onBackPressed() {
        var intent = Intent(this, MainActivity::class.java)

        when (previousActivity) {
            "ClientActivity" -> {
                intent = Intent(this, ClientActivity::class.java).also {
                    it.putExtra("ClientEntity", getClientFromPreviousActivity())
                    it.putExtra("PreviousActivity", "Calculation")
                }
            }
            "Clients" -> {
                intent = Intent(this, Clients::class.java).also {
                    it.putExtra("ClientEntity", setNullClient())
                    it.putExtra("PreviousActivity", "Calculation")
                }
            }
            "StartActivity" -> {
                intent = Intent(this, MainActivity::class.java).also {
                    it.putExtra("ClientEntity", setNullClient())
                    it.putExtra("PreviousActivity", "Calculation")
                }
            }
            "TypeOfWorkActivity" -> {
                intent = Intent(this, Clients::class.java).also {
                    it.putExtra("ClientEntity", setNullClient())
                    it.putExtra("PreviousActivity", "Calculation")
                }
            }
        }
        startActivity(intent)
    }

    fun setNullClient(): Client = Client(
        0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
        DateOfCreation = "",
        DateOfEditing = ""
    )


    private fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", " Calculation onStart() called")

    }

}