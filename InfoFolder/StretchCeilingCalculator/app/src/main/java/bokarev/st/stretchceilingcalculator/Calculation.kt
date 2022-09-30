package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import bokarev.st.stretchceilingcalculator.entities.PdfToDisplay
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import kotlinx.android.synthetic.main.calculation.*
import kotlinx.coroutines.*


class Calculation : AppCompatActivity() {

    private var previousActivity = ""

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculation)

        val tvNameOfClient: TextView = findViewById(R.id.textView2)
        val dao = CategoriesDataBase.getInstance(this).categoriesDao

        try {

            val client = getClientFromPreviousActivity()
            previousActivity = intent.getStringExtra("PreviousActivity").toString()


            if (client.ClientName == "") {
                tvNameOfClient.text = "Выберите категорию \nдля редактирования"
                tvNameOfClient.textSize = 12f
                val tv: TextView = findViewById(R.id.textView)
                tv.text =
                    "" // //  идеале надо удалить с разметки и центрировать оставшееся textview
                val parentRelativeLayoutCalculation: RelativeLayout =
                    findViewById(R.id.parentRelativeLayoutCalculation)
                showHide(parentRelativeLayoutCalculation)
                showHide(btnExportCalculation)

            } else {
                tvNameOfClient.text = client.ClientName
            }

            getSumByClient() // установить в текстовое поле обзую сумму сметы по всем категориям


        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            returnToPreviousActivity()
        }

        val btnDemoCalculation: ImageView = findViewById(R.id.btnDemoCalculation)
        btnDemoCalculation.setOnClickListener {

            val wantChange =
                previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == ""

            startNextTypeOfWorkActivity(

                true,
                arrayListOf(),
                "Все позиции в смете",
                wantChange
            )

        }

        val btnExportCalculation: ImageView = findViewById(R.id.btnExportCalculation)
        btnExportCalculation.setOnClickListener {

            val finishList: MutableList<ClientAndEstimateModification> = arrayListOf()
            var someList: MutableList<ClientAndEstimate>

            val job = GlobalScope.launch(Dispatchers.Default) {

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

        }



        val btnGoToSystem: Button = findViewById(R.id.btnGoToSystem)
        btnGoToSystem.setOnClickListener {

            val wantChange =
                previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == ""

            //по хорошему надо создать новую таблицу и из нее считывать значения констант
            startNextTypeOfWorkActivity(
                false,
                arrayListOf(1, 2, 3, 4, 5, 6, 7, 8),
                "Система",
                wantChange
            )

        }
        val btnGoToLight: Button = findViewById(R.id.btnGoToLight)
        btnGoToLight.setOnClickListener {

            val wantChange =
                previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == ""

            //по хорошему надо создать новую таблицу и из нее считывать значения констант
            startNextTypeOfWorkActivity(false, arrayListOf(9), "Освещение", wantChange)

        }
        val btnGoToBonusWork: Button = findViewById(R.id.btnGoToBonusWork)
        btnGoToBonusWork.setOnClickListener {

            val wantChange =
                previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == ""

            //по хорошему надо создать новую таблицу и из нее считывать значения констант
            startNextTypeOfWorkActivity(false, arrayListOf(10), "Доп. работы", wantChange)

        }
        val btnGoToMaterials: Button = findViewById(R.id.btnGoToMaterials)
        btnGoToMaterials.setOnClickListener {

            val wantChange =
                previousActivity == "StartActivity" || (previousActivity == "TypeOfWorkActivity") && getClientFromPreviousActivity().ClientName == ""

            //по хорошему надо создать новую таблицу и из нее считывать значения констант
            startNextTypeOfWorkActivity(
                false,
                arrayListOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                "Материалы",
                wantChange
            )
        }

    }

    private fun startNextTypeOfWorkActivity(

        allListTypesOfWork: Boolean,
        idTypeOfWorkList: ArrayList<Int>,
        nameTypeOfWork: String,
        wantChange: Boolean
    ) {
        val intent = Intent(this, TypeOfWorkActivity::class.java).also {
            it.putExtra("ClientEntity", getClientFromPreviousActivity())
            it.putExtra("PreviousActivity", "Calculation")
            it.putExtra("idTypeOfWork", allListTypesOfWork)
            it.putExtra("idTypeOfWorkList", idTypeOfWorkList)
            it.putExtra("NameTypeOfWork", nameTypeOfWork)
            it.putExtra("WantChange", wantChange)

        }
        startActivity(intent)

    }

    private fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun returnToPreviousActivity() {
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun getSumByClient() {
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
            val str = "$sum ₽"
            tvSum.text = str

            Log.d("mytag", "Main Thread is Running")
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

        if (filePath != null) SharePdf().sharePdf(this, filePath)
        else generatePdf(pdfToDisplay)

    }

    override fun onBackPressed() = returnToPreviousActivity()

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