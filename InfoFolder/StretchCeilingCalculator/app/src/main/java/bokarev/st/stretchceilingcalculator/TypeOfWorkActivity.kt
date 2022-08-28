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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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


            if (previousActivity == "Calculation") {
                var sum = 0
                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                    val someList =
                        dao.getUnionClientAndEstimateAndTypeCategory2(
                            getClientFromPreviousActivity()._id,
                            idTypeOfWork
                        )

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

                    val tvSum = findViewById<TextView>(R.id.textView2)

                    tvSum.text = "сумма: ${sum} ₽"

                    Log.d("mytag", "Main Thread is Running")
                }
            }

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName} idClient = ${client._id}"
            )
        } catch (exp: RuntimeException) {

        }
        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val someList = typeOfWorkRecyclerViewAdapter.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                for (i in someList) {
                    dao.updateCountStrokesEstimateByClient(
                        getClientFromPreviousActivity()._id,
                        i._idTypeCategory,
                        i.Count
                    )
                    Log.d("mytag", "items back print = ${i.CategoryName}")
                }
            }

            runBlocking {
                // waiting for the coroutine to finish it"s work
                job.join()
                gettransition()
                Log.d("mytag", "Main Thread is Running")
            }
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
        val someList = typeOfWorkRecyclerViewAdapter.getListData()
        val job = GlobalScope.launch(Dispatchers.Default) {

            val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
            for (i in someList) {
                dao.updateCountStrokesEstimateByClient(
                    getClientFromPreviousActivity()._id,
                    i._idTypeCategory,
                    i.Count
                )
                Log.d("mytag", "items back print = ${i.CategoryName}")
            }
        }

        runBlocking {
            // waiting for the coroutine to finish it"s work
            job.join()
            gettransition()
            Log.d("mytag", "Main Thread is Running")
        }

    }

    fun gettransition() {
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

    override fun onChangeClick(
        data: ClientAndEstimate,
        typeChange: String,
        priceOld: Int,
        countOld: Int
    ) {
        val tv = findViewById<TextView>(R.id.textView2)
        val oldSum = tv.text.split(" ")[1].toInt()
        if (typeChange == "down") {
            val newSum = oldSum - data.Price
            tv.text = "сумма: ${newSum} ₽"

        } else if (typeChange == "up") {
            val newSum = oldSum + data.Price
            tv.text = "сумма: ${newSum} ₽"
        }

        val indexPrevious = typeOfWorkRecyclerViewAdapter.getListData().indexOf(
            ClientAndEstimate(
                data.ClientName,
                countOld,
                data._idTypeCategory,
                data._idTypeOfWork,
                priceOld,
                data.CategoryName
            )
        )
        val items = typeOfWorkRecyclerViewAdapter.getListData()

        items.add(indexPrevious, data)
        items.removeAt(indexPrevious + 1)

        for (i in items) {
            Log.d("mytag", "items print = ${i.CategoryName}")
        }
        typeOfWorkRecyclerViewAdapter.setListData(items)
        typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onItemClickListener(user: ClientAndEstimate) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
}