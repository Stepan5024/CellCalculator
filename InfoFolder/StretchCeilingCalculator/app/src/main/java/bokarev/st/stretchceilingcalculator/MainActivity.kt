package bokarev.st.stretchceilingcalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.Estimate
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGoToClients: Button = findViewById(R.id.btnGoToClients)
        val btnGoToPrices: Button = findViewById(R.id.btnGoToPrices)

        btnGoToClients.setOnClickListener {

            val intent = Intent(this, Clients::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)

        }

        btnGoToPrices.setOnClickListener {
            val intent = Intent(this, Calculation::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)

        }

        val dao = CategoriesDataBase.getInstance(this).categoriesDao

        val typeOfWork = listOf(
            TypeOfWork(1, "Освещение"),
            TypeOfWork(2, "Потолки"),
            TypeOfWork(3, "Другой вид работы")
        )

        val typeCategory = listOf(
            TypeCategory(1, "Подвеска люстры", 3000, 1),
            TypeCategory(2, "Провода", 1000, 1),
            TypeCategory(3, "Крепление в потолке", 5000, 1),
            TypeCategory(4, "Потолок глянцевый", 1400, 2),
            TypeCategory(5, "Потолок матовый", 1200, 2),
            TypeCategory(6, "Другие работы", 700, 3)

        )

        val estimate = listOf(

            Estimate(1, 1, 1, 3, "17.06.2022", "17.06.2022"),
            Estimate(2, 1, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(3, 1, 3, 0, "17.06.2022", "17.06.2022"),
            Estimate(4, 1, 4, 2, "17.06.2022", "17.06.2022"),
            Estimate(5, 1, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(6, 1, 6, 0, "17.06.2022", "17.06.2022"),
            Estimate(7, 2, 1, 1, "17.06.2022", "17.06.2022"),
            Estimate(8, 2, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(9, 2, 3, 4, "17.06.2022", "17.06.2022"),
            Estimate(10, 2, 4, 0, "17.06.2022", "17.06.2022"),
            Estimate(11, 2, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(12, 2, 6, 0, "17.06.2022", "17.06.2022"),
            Estimate(13, 3, 1, 1, "17.06.2022", "17.06.2022"),
            Estimate(14, 3, 2, 0, "17.06.2022", "17.06.2022"),
            Estimate(15, 3, 3, 0, "17.06.2022", "17.06.2022"),
            Estimate(16, 3, 4, 1, "17.06.2022", "17.06.2022"),
            Estimate(17, 3, 5, 0, "17.06.2022", "17.06.2022"),
            Estimate(18, 3, 6, 0, "17.06.2022", "17.06.2022"),
        )

        val clients = listOf(

            Client(
                0,
                "Ваня",
                "Москва",
                "89774968939",
                IsNew = true,
                false,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
            Client(
                0,
                "Петя",
                "Москва",
                "89774968939",
                IsNew = true,
                false,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
            Client(
                0,
                "Шура",
                "Москва",
                "89774968939",
                IsNew = false,
                true,
                IsArchive = false,
                "17.06.2022",
                "17.06.2022"
            ),
        )


        lifecycleScope.launch {
            typeOfWork.forEach { dao.insertTypeOfWork(it) }
            typeCategory.forEach { dao.insertTypeCategory(it) }
            estimate.forEach { dao.insertEstimate(it) }
            //clients.forEach { dao.insertClient(it) }

            val typeOfWorkWithTypeCategory = dao.getTypeOfWorkWithTypeCategory(2)
            val typeCategoryInEstimate = dao.getTypeCategoryInEstimate(2)
            val clientsWithEstimate = dao.getClientWithEstimate(2)
            //val unionEstimateWithClient = dao.getUnionEstimateWithClient(2)
            val getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(1, 1)

            val someList = arrayOf(getClientAndEstimate)
            for (i in someList) {

                Log.d("mytag", "listDB = ${i.joinToString(" || ")}")

            }

        }
    }
}