package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import kotlinx.coroutines.launch

class TypeOfWorkActivity : AppCompatActivity(), TypeOfWorkRecyclerViewAdapter.RowClickListener {


    private val dao = CategoriesDataBase.getInstance(this).categoriesDao

    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)
        var idTypeOfWork = 0

        try {
            val previousActivity = intent.getStringExtra("PreviousActivity").toString()
            val client = getClientFromPreviousActivity()
            idTypeOfWork = intent.getIntExtra("idTypeOfWork", 0)

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName} idClient = ${client._id}"
            )
        } catch (exp: RuntimeException) {

        }
        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, Calculation::class.java).also {
                it.putExtra("ClientEntity", getClientFromPreviousActivity())
                it.putExtra("PreviousActivity", "TypeOfWorkActivity")
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

            val getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(
                getClientFromPreviousActivity()._id,
                idTypeOfWork
            )

            typeOfWorkRecyclerViewAdapter.setListData(ArrayList(getClientAndEstimate))
            typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()


        }


    }

    // Kotlin
    override fun onBackPressed() {
        val intent = Intent(this, Calculation::class.java).also {
            it.putExtra("ClientEntity", getClientFromPreviousActivity())
            it.putExtra("PreviousActivity", "TypeOfWorkActivity")
        }
        startActivity(intent)
    }

    fun setNullClient(): Client {

        return Client(
            0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
            DateOfCreation = "",
            DateOfEditing = ""
        )

    }

    private fun getClientFromPreviousActivity(): Client {

        return intent.getSerializableExtra("ClientEntity") as Client
    }

    override fun onDeleteUserClickListener(user: ClientAndEstimate) {

    }

    override fun onChangeClick(data: TypeOfWorkDataClass, typeChange: String) {
        val tv = findViewById<TextView>(R.id.textView2)
        val oldSum = tv.text.split(" ").get(1).toInt()
        if (typeChange == "down") {
            val newSum = oldSum - data.Price
            tv.text = "сумма: ${newSum} ₽"
        } else if (typeChange == "up"){
            val newSum = oldSum + data.Price
            tv.text = "сумма: ${newSum} ₽"
        }
    }

    override fun onItemClickListener(user: ClientAndEstimate) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
}