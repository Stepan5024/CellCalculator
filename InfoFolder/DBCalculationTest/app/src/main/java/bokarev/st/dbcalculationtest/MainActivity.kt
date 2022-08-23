package bokarev.st.dbcalculationtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import bokarev.st.dbcalculationtest.entities.Calculation
import bokarev.st.dbcalculationtest.entities.Client
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val calculation = listOf(

            Calculation(1, 1, 1, 3),
            Calculation(2, 1, 2, 0),
            Calculation(3, 1, 3, 0),
            Calculation(4, 1, 4, 2),
            Calculation(5, 1, 5, 0),
            Calculation(6, 1, 6, 0),
            Calculation(7, 2, 1, 1),
            Calculation(8, 2, 2, 0),
            Calculation(9, 2, 3, 4),
            Calculation(10, 2, 4, 0),
            Calculation(11, 2, 5, 0),
            Calculation(12, 2, 6, 0),
            Calculation(13, 3, 1, 1),
            Calculation(14, 3, 2, 0),
            Calculation(15, 3, 3, 0),
            Calculation(16, 3, 4, 1),
            Calculation(17, 3, 5, 0),
            Calculation(18, 3, 6, 0),
        )

        val clients = listOf(

            Client(0, "Ваня", "89774968939", IsNew = true, IsPurcharse = false, IsArchive = false),
            Client(0, "Петя", "89774968939", IsNew = true, IsPurcharse = false, IsArchive = false),
            Client(0, "Шура", "89774968939", IsNew = false, IsPurcharse = true, IsArchive = false),
        )


        lifecycleScope.launch {
            typeOfWork.forEach { dao.insertTypeOfWork(it) }
            typeCategory.forEach { dao.insertTypeCategory(it) }
            calculation.forEach { dao.insertCalculation(it) }
            clients.forEach { dao.insertClient(it) }

            val typeOfWorkWithTypeCategory = dao.getTypeOfWorkWithTypeCategory(2)
            val typeCategoryInCalculation = dao.getTypeCategoryInCalculation(2)
            val clientsWithCalculation = dao.getClientWithCalculation(2)


            val someList = arrayOf(clientsWithCalculation)
            for (i in someList) {

                Log.d("mytag", "listDB = ${i.joinToString(" || ")}")

            }


        }
    }
}