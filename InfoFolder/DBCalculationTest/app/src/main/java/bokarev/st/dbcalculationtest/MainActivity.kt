package bokarev.st.dbcalculationtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = CategoriesDataBase.getInstance(this).categoriesDao

        val typeOfWork = listOf(
            TypeOfWork(0, "Освещение"),
            TypeOfWork(1, "Потолки"),
            TypeOfWork(2, "Другой вид работы")
        )

        val typeCategory = listOf(
            TypeCategory(0, "Подвеска люстры", 3000, 1),
            TypeCategory(1, "Провода", 1000, 1),
            TypeCategory(2, "Крепление в потолке", 5000, 1),
            TypeCategory(3, "Потолок глянцевый", 1400, 2),
            TypeCategory(4, "Потолок матовый", 1200, 2),
            TypeCategory(5, "Другие работы", 700, 3)

        )


        lifecycleScope.launch {
            typeOfWork.forEach { dao.insertTypeOfWork(it) }
            typeCategory.forEach { dao.insertTypeCategory(it) }


            val typeOfWorkWithTypeCategory = dao.getTypeOfWorkWithTypeCategory(2)
            val someList = arrayOf(typeOfWorkWithTypeCategory)
            for (i in someList) {

                Log.d("mytag", "listDB = ${i.joinToString(" || ")}")

            }


        }
    }
}