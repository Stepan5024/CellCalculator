package bokarev.st.stretchceilingcalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bokarev.st.stretchceilingcalculator.entities.Client

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
            val toast = Toast.makeText(
                applicationContext,
                "btnDemoCalculation ДЕМО сметы pressed",
                Toast.LENGTH_SHORT
            )
            toast.show()

        }

        val btnExportCalculation: ImageView = findViewById(R.id.btnExportCalculation)
        btnExportCalculation.setOnClickListener {
            val toast = Toast.makeText(
                applicationContext,
                "btnExportCalculation экспорт файла pressed",
                Toast.LENGTH_SHORT
            )
            toast.show()

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
            }
            startActivity(intent)
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