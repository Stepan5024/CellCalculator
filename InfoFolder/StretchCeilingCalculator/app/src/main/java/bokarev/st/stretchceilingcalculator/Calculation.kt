package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.PdfToDisplay
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import kotlinx.coroutines.*

class Calculation : AppCompatActivity() {

    private var previousActivity = ""

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
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

                if (client.ClientName == "") {
                    tvNameOfClient.text = "Выберите категорию \nдля редактирования"
                    tvNameOfClient.textSize = 12f
                } else {
                    tvNameOfClient.text = client.ClientName
                }
                var sum = 0.0F
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

            } else if (previousActivity == "StartActivity" || previousActivity == "TypeOfWorkActivity") {
                tvNameOfClient.text = "Выберите категорию \nдля редактирования"
                tvNameOfClient.textSize = 12f

            } else {
                tvNameOfClient.text = "Что-то незнакомое"
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
                val l = arrayListOf<Int>()
                it.putExtra("idTypeOfWorkList", l)
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

            val finishList: MutableList<ClientAndEstimateModification> = arrayListOf()
            var someList: MutableList<ClientAndEstimate>


            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@Calculation).categoriesDao

                // надо вывести весь список со всеми категориями
                someList = dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                Log.d(
                    "mytag",
                    "someList.size = ${someList.size}"
                )
                for (i in someList) {
                    val nameTypeOfWork = dao.getTypeOfWorkNameByTypeCategory(i._idTypeOfWork)
                    val suk = ClientAndEstimateModification(
                        i.ClientName,
                        i.Count,
                        i._idTypeCategory,
                        i._idTypeOfWork,
                        i.Price,
                        i.CategoryName,
                        nameTypeOfWork,
                        1,
                        i.UnitsOfMeasurement
                    )
                    if (suk.Count != 0f) {
                        finishList.add(suk)
                    }

                }
                /* for (i in someList) {

                     sum += i.Price * i.Count

                     Log.d(
                         "mytag",
                         "calculation items CategoryName = ${i.CategoryName} Price = ${i.Price} Count = ${i.Count}"
                     )
                 }*/

            }
            runBlocking {
                // waiting for the coroutine to finish it"s work
                job.join()
                //set view
                if (finishList.isNotEmpty()) {
                    val pdfToDisplay = PdfToDisplay(
                        0,
                        finishList,
                        getClientFromPreviousActivity().Address,
                        getClientFromPreviousActivity().Tel,
                        ""
                    )

                    //gotShowPdfPage(receipt)
                    if (pdfToDisplay.FilePath.isEmpty() || pdfToDisplay.FilePath.isBlank()) {
                        generatePdf(pdfToDisplay)
                        displayPdf(pdfToDisplay)
                    } else {
                        displayPdf(pdfToDisplay)
                    }
                } else {
                    val toast = Toast.makeText(
                        applicationContext,
                        "В смете нет хотя бы одной позиции!\nНельзя экспортировать пустую смету",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
                Log.d("mytag", "Main Thread is Running")
            }


            /*

              val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                  addCategory(Intent.CATEGORY_OPENABLE)
                  type = "application/pdf"
                  //type = "text/plain"
                  putExtra(Intent.EXTRA_TITLE, "fileName.pdf")
                 // putExtra(Intent.EXTRA_STREAM, file)
                  putExtra(DocumentsContract.EXTRA_INITIAL_URI, "")
              }

              resultLauncher.launch(intent)*/

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
                it.putExtra("idTypeOfWorkList", arrayListOf(1, 2, 3, 4, 5, 6, 7, 8))
                it.putExtra("NameTypeOfWork", "Система")
                if (previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == "") {
                    // хотим менять цены
                    it.putExtra("WantChange", true)
                } else {
                    // не хотим менять цены
                    it.putExtra("WantChange", false)
                }

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
                it.putExtra("idTypeOfWork", 9)
                it.putExtra("idTypeOfWorkList", arrayListOf(9))
                it.putExtra("NameTypeOfWork", "Освещение")
                if (previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == "") {
                    // хотим менять цены
                    it.putExtra("WantChange", true)
                } else {
                    // не хотим менять цены
                    it.putExtra("WantChange", false)
                }
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
                it.putExtra("idTypeOfWork", 10)
                it.putExtra("idTypeOfWorkList", arrayListOf(10))
                it.putExtra("NameTypeOfWork", "Доп. работы")
                if (previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == "") {
                    // хотим менять цены
                    it.putExtra("WantChange", true)
                } else {
                    // не хотим менять цены
                    it.putExtra("WantChange", false)
                }
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
                it.putExtra("idTypeOfWork", 11)
                it.putExtra("idTypeOfWorkList", arrayListOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20))
                it.putExtra("NameTypeOfWork", "Материалы")
                if (previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == "") {
                    // хотим менять цены
                    it.putExtra("WantChange", true)
                } else {
                    // не хотим менять цены
                    it.putExtra("WantChange", false)
                }
            }
            startActivity(intent)
        }

    }


    private fun generatePdf(pdfToDisplay: PdfToDisplay) {
        val bitmapFactory = BitmapFactory.decodeResource(
            this.resources, R.drawable.pizzahead
        )
        val filePath = GeneratePdf.generate(
            bitmapFactory, pdfToDisplay
        )
        pdfToDisplay.FilePath = filePath


    }

    private fun displayPdf(pdfToDisplay: PdfToDisplay) {

        val filePath = ShowPdf().findFilePath(pdfToDisplay.FilePath)
        if (filePath != null) {

            SharePdf().sharePdf(this, filePath)

        } else {
            generatePdf(pdfToDisplay)
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

    private fun setNullClient(): Client = Client(
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