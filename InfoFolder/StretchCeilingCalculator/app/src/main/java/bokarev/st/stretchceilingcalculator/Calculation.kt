package bokarev.st.stretchceilingcalculator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.ClientAndEstimateMidifation
import kotlinx.coroutines.*

class Calculation : AppCompatActivity() {

    var previousActivity = ""

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
                tvNameOfClient.text = client.ClientName
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

            }
            else if (previousActivity == "StartActivity") {
                tvNameOfClient.text = "Выберите категорию \nдля редактирования"
                tvNameOfClient.textSize = 12f

            }
            else {
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

            val finishList: MutableList<ClientAndEstimateMidifation> = arrayListOf()
            var someList: MutableList<ClientAndEstimate> = arrayListOf()


            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@Calculation).categoriesDao

                // надо вывести весь список со всеми категориями
                someList = dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                Log.d(
                    "mytag",
                    "someList.size = ${someList.size}"
                )
                for(i in someList){
                    val nameTypeOfWork = dao.getTypeOfWorkNameByTypeCategory(i._idTypeOfWork)
                    val suk = ClientAndEstimateMidifation(i.ClientName, i.Count, i._idTypeCategory, i._idTypeOfWork, i.Price, i.CategoryName, nameTypeOfWork, 1, i.UnitsOfMeasurement)
                    finishList.add(suk)
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
                val receipt = Receipt(
                    0,
                    finishList,
                    getClientFromPreviousActivity().Address,
                    getClientFromPreviousActivity().Tel,
                    ""
                )

                //gotShowPdfPage(receipt)
                if (receipt.FilePath.isEmpty() || receipt.FilePath.isBlank()) {
                    generatePdf(receipt)
                    displayPdf(receipt)
                } else {
                    displayPdf(receipt)
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
                if(previousActivity == "StartActivity") {
                    // хотим менять цены
                    it.putExtra("WantChange", true)
                }else {
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

    private val STORAGE_REQUEST_CODE = 99;

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            //bellow 11
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), STORAGE_REQUEST_CODE
            )
        }
    }

    private val storageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //Todo call Save
                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
            } else {
                //bellow 11
            }
        }

    private fun generatePdf(receipt: Receipt) {
        val bitmapFactory = BitmapFactory.decodeResource(
            this.resources, R.drawable.pizzahead
        )
        val filePath = GeneratePdf.generate(
            bitmapFactory, receipt
        )
        receipt.FilePath = filePath


    }

    private fun displayPdf(receipt: Receipt) {

        val filePath = ShowPdf().findFilePath(receipt.FilePath)
        if (filePath != null) {

            SharePdf().sharePdf(this, filePath)

        } else {
            generatePdf(receipt)
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