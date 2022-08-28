package bokarev.st.stretchceilingcalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Calculation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculation)
        try {
            val name = intent.getStringExtra("KEY1")
            //val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            //Log.d("mytag", "school 6 = ${school6.title}")
        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }

        val btnDemoCalculation: ImageView = findViewById(R.id.btnDemoCalculation)
        btnDemoCalculation.setOnClickListener{
            val toast = Toast.makeText(applicationContext, "btnDemoCalculation ДЕМО сметы pressed", Toast.LENGTH_SHORT)
            toast.show()

        }

        val btnExportCalculation: ImageView = findViewById(R.id.btnExportCalculation)
        btnExportCalculation.setOnClickListener{
            val toast = Toast.makeText(applicationContext, "btnExportCalculation экспорт файла pressed", Toast.LENGTH_SHORT)
            toast.show()

        }

        val btnGoToSystem: Button = findViewById(R.id.btnGoToSystem)
        btnGoToSystem.setOnClickListener{
            /*val toast = Toast.makeText(applicationContext, "btnGoToSystem открываем активность тип работы с Системой", Toast.LENGTH_SHORT)
            toast.show()
            
             */
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }
        val btnGoToLight: Button = findViewById(R.id.btnGoToLight)
        btnGoToLight.setOnClickListener{
            //val toast = Toast.makeText(applicationContext, "btnGoToLight открываем активность тип работы с Освещением", Toast.LENGTH_SHORT)
            //toast.show()
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }
        val btnGoToBonusWork: Button = findViewById(R.id.btnGoToBonusWork)
        btnGoToBonusWork.setOnClickListener{
            /*val toast = Toast.makeText(applicationContext, "btnGoToBonusWork открываем активность тип работы с Доп. работы", Toast.LENGTH_SHORT)
            toast.show()*/
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }
        val btnGoToMaterials: Button = findViewById(R.id.btnGoToMaterials)
        btnGoToMaterials.setOnClickListener{
           /* val toast = Toast.makeText(applicationContext, "btnGoToMaterials открываем активность тип работы Материалы", Toast.LENGTH_SHORT)
            toast.show()*/
            val intent = Intent(this, TypeOfWorkActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }

    }

    // Kotlin
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).also {
            it.putExtra("KEY1", "value1")
            it.putExtra("KEY2", "value1")
            it.putExtra("KEY3", "value1")
            //it.putExtra("School 6", School("School 6", false))
        }
        startActivity(intent)
    }
}