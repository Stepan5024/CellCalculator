package bokarev.st.stretchceilingcalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import kotlinx.coroutines.launch

class TypeOfWorkActivity : AppCompatActivity(), TypeOfWorkRecyclerViewAdapter.RowClickListener {


    val dao = CategoriesDataBase.getInstance(this).categoriesDao

    lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)
        try {
            val name = intent.getStringExtra("KEY1")
            //val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            //Log.d("mytag", "school 6 = ${school6.title}")
        } catch (exp: RuntimeException) {

        }
        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, Calculation::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.TypeOfWorkRecyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TypeOfWorkActivity)
            typeOfWorkRecyclerViewAdapter = TypeOfWorkRecyclerViewAdapter(this@TypeOfWorkActivity)
            adapter = typeOfWorkRecyclerViewAdapter
            val divider =
                DividerItemDecoration(applicationContext, StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(divider)
        }


        //Without ViewModelFactory
        lifecycleScope.launch {

            val getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(1, 1)

            typeOfWorkRecyclerViewAdapter.setListData(ArrayList(getClientAndEstimate))
            typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()


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

    override fun onDeleteUserClickListener(user: ClientAndEstimate) {

    }

    override fun onItemClickListener(user: ClientAndEstimate) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
}