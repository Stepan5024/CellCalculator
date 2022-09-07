package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter3
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter4
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.ClientAndEstimateMidifation
import bokarev.st.stretchceilingcalculator.models.ViewEstimate
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class TypeOfWorkActivity : AppCompatActivity(), TypeOfWorkRecyclerViewAdapter3.RowClickListener,
    TypeOfWorkRecyclerViewAdapter4.RowClickListener {

    private var listDataFull: MutableList<ClientAndEstimateMidifation> = arrayListOf()


    private val dao = CategoriesDataBase.getInstance(this).categoriesDao
    var wantChange = false

    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter3
    private lateinit var typeOfWorkRecyclerViewAdapter4: TypeOfWorkRecyclerViewAdapter4

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)

        val tvNameOfWork: TextView = findViewById(R.id.tvNameOfWork)

        var idTypesOfWorkList: ArrayList<Int> = arrayListOf()

        var idTypeOfWork = 0
        val previousActivity: String

        val recyclerView: RecyclerView = findViewById(R.id.TypeOfWorkRecyclerView)


        try {
            previousActivity = intent.getStringExtra("PreviousActivity").toString()
            val client = getClientFromPreviousActivity()
            idTypeOfWork = intent.getIntExtra("idTypeOfWork", -1)
            wantChange = intent.getBooleanExtra("WantChange", false)
            idTypesOfWorkList =
                intent.getIntegerArrayListExtra("idTypeOfWorkList") as ArrayList<Int>
            // idTypeOfWork == 0 означает вывести все категории работ в смете
            // idTypeOfWork == -1 означает что пользователь попал на активность не по кнопкам Система, Освещение, Доп. работы, материалы
            if (idTypeOfWork == -1) {

                val toast = Toast.makeText(
                    applicationContext,
                    "Произошла ошибка выбора типа работы. Обратитесь к разработчику",
                    Toast.LENGTH_SHORT
                )
                toast.show()


                val intent = Intent(this, MainActivity::class.java).also {
                    it.putExtra("ClientEntity", getClientFromPreviousActivity())
                    it.putExtra("PreviousActivity", "TypeOfWorkActivity")

                }
                startActivity(intent)
            }
            if (wantChange) {

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@TypeOfWorkActivity)
                    typeOfWorkRecyclerViewAdapter4 =
                        TypeOfWorkRecyclerViewAdapter4(this@TypeOfWorkActivity)
                    adapter = typeOfWorkRecyclerViewAdapter4
                    val divider =
                        DividerItemDecoration(
                            applicationContext,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                    addItemDecoration(divider)
                    recycledViewPool.setMaxRecycledViews(0, 300)
                }


                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                val job = GlobalScope.launch(Dispatchers.Default) {
                    val someList: MutableList<ViewEstimate> = if (idTypeOfWork == 0)

                    // надо вывести весь список со всеми категориями
                        dao.getEstimate()
                    else
                    // выводим список выбранных категорий
                        dao.getEstimateByList(
                            idTypesOfWorkList
                        )
                    typeOfWorkRecyclerViewAdapter4.setListData(someList)
                    typeOfWorkRecyclerViewAdapter4.notifyDataSetChanged()

                }
                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    //set view
                    Log.d("mytag", "Main Thread is Running")
                }
            } else {

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@TypeOfWorkActivity)
                    typeOfWorkRecyclerViewAdapter =
                        TypeOfWorkRecyclerViewAdapter3(this@TypeOfWorkActivity)
                    adapter = typeOfWorkRecyclerViewAdapter
                    val divider =
                        DividerItemDecoration(
                            applicationContext,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                    addItemDecoration(divider)
                    recycledViewPool.setMaxRecycledViews(0, 300)
                }

                val finalList: MutableList<ClientAndEstimateMidifation> = arrayListOf()
                //Without ViewModelFactory
                lifecycleScope.launch {
                    var getClientAndEstimate: MutableList<ClientAndEstimate>

                    if (idTypeOfWork == 0) {
                        // надо вывести весь список со всеми категориями
                        getClientAndEstimate =
                            dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                        Log.d(
                            "mytag",
                            "someList.size = ${getClientAndEstimate.size}"

                        )

                        var prev = 0
                        for (i in getClientAndEstimate) {
                            if (i._idTypeOfWork == prev) {
                                val nameCategory = dao.getTypeOfWorkNameByTypeCategory(prev)
                                val clientAndEstimateMidifation =
                                    ClientAndEstimateMidifation(
                                        i.CategoryName,
                                        i.Count,
                                        i._idTypeCategory,
                                        i._idTypeOfWork,
                                        i.Price,
                                        i.CategoryName,
                                        nameCategory,
                                        1,
                                        i.UnitsOfMeasurement,
                                    )
                                finalList.add(clientAndEstimateMidifation)

                            } else {
                                prev++
                                val nameCategory = dao.getTypeOfWorkNameByTypeCategory(prev)
                                /* val clientAndEstimateMidifation1 =
                                     ClientAndEstimateMidifation(
                                         "NewList",
                                         0,
                                         i._idTypeCategory,
                                         prev,
                                         0,
                                         nameCategory,
                                         nameCategory,
                                         0
                                     )*/

                                val clientAndEstimateMidifation2 =
                                    ClientAndEstimateMidifation(
                                        i.CategoryName,
                                        i.Count,
                                        i._idTypeCategory,
                                        i._idTypeOfWork,
                                        i.Price,
                                        i.CategoryName,
                                        nameCategory,
                                        1,
                                        i.UnitsOfMeasurement,
                                    )
                                //finalList.add(clientAndEstimateMidifation1)
                                finalList.add(clientAndEstimateMidifation2)
                            }


                        }

                    } else {
                        if (idTypesOfWorkList.size > 0) {

                            Log.d("mytag", "ты попал на вывод листа ")

                            for (i in idTypesOfWorkList) {

                                Log.d("mytag", "i = $i ")
                                idTypeOfWork = i

                                getClientAndEstimate =
                                    dao.getUnionClientAndEstimateAndTypeCategory2(
                                        getClientFromPreviousActivity()._id,
                                        idTypeOfWork
                                    )
                                var prev = 0
                                for (j in getClientAndEstimate) {
                                    if (j._idTypeOfWork == idTypeOfWork) {
                                        val nameCategory =
                                            dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)
                                        val clientAndEstimateMidifation =
                                            ClientAndEstimateMidifation(
                                                j.CategoryName,
                                                j.Count,
                                                j._idTypeCategory,
                                                j._idTypeOfWork,
                                                j.Price,
                                                j.CategoryName,
                                                nameCategory,
                                                1,
                                                j.UnitsOfMeasurement,
                                            )
                                        finalList.add(clientAndEstimateMidifation)

                                    } else {
                                        prev++
                                        val nameCategory =
                                            dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)
                                        /* val clientAndEstimateMidifation1 =
                                             ClientAndEstimateMidifation(
                                                 "NewList",
                                                 0,
                                                 i._idTypeCategory,
                                                 prev,
                                                 0,
                                                 nameCategory,
                                                 nameCategory,
                                                 0
                                             )*/

                                        val clientAndEstimateMidifation2 =
                                            ClientAndEstimateMidifation(
                                                j.CategoryName,
                                                j.Count,
                                                j._idTypeCategory,
                                                j._idTypeOfWork,
                                                j.Price,
                                                j.CategoryName,
                                                nameCategory,
                                                1,
                                                j.UnitsOfMeasurement,
                                            )
                                        //finalList.add(clientAndEstimateMidifation1)
                                        finalList.add(clientAndEstimateMidifation2)
                                    }
                                }
                            }
                        } else {
                            getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(
                                getClientFromPreviousActivity()._id,
                                idTypeOfWork
                            )

                            var prev = 0
                            for (i in getClientAndEstimate) {
                                if (i._idTypeOfWork == prev) {
                                    val nameCategory = dao.getTypeOfWorkNameByTypeCategory(prev)
                                    val clientAndEstimateMidifation =
                                        ClientAndEstimateMidifation(
                                            i.CategoryName,
                                            i.Count,
                                            i._idTypeCategory,
                                            i._idTypeOfWork,
                                            i.Price,
                                            i.CategoryName,
                                            nameCategory,
                                            1,
                                            i.UnitsOfMeasurement,
                                        )
                                    finalList.add(clientAndEstimateMidifation)

                                } else {
                                    prev++
                                    val nameCategory = dao.getTypeOfWorkNameByTypeCategory(prev)
                                    /* val clientAndEstimateMidifation1 =
                                         ClientAndEstimateMidifation(
                                             "NewList",
                                             0,
                                             i._idTypeCategory,
                                             prev,
                                             0,
                                             nameCategory,
                                             nameCategory,
                                             0
                                         )*/

                                    val clientAndEstimateMidifation2 =
                                        ClientAndEstimateMidifation(
                                            i.CategoryName,
                                            i.Count,
                                            i._idTypeCategory,
                                            i._idTypeOfWork,
                                            i.Price,
                                            i.CategoryName,
                                            nameCategory,
                                            1,
                                            i.UnitsOfMeasurement,
                                        )
                                    //finalList.add(clientAndEstimateMidifation1)
                                    finalList.add(clientAndEstimateMidifation2)
                                }
                            }
                        }
                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()


                }


            }


            if (previousActivity == "Calculation" || idTypeOfWork == 0) {
                var sum = 0.0


                tvNameOfWork.text = intent.getStringExtra("NameTypeOfWork").toString()

                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao =
                        CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao

                    val someList: MutableList<ClientAndEstimate> = if (idTypeOfWork == 0)

                    // надо вывести весь список со всеми категориями
                        dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                    else
                    /* dao.getUnionClientAndEstimateAndTypeCategory2(
                         getClientFromPreviousActivity()._id,
                         idTypeOfWork
                     )*/
                        dao.getUnionClientAndEstimateAndTypeCategoryInLists(
                            getClientFromPreviousActivity()._id,
                            idTypesOfWorkList
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
                    val string = "сумма: $sum ₽"
                    tvSum.text = string

                    Log.d("mytag", "Main Thread is Running")
                }
            }

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName} idClient = ${client._id}"
            )
        } catch (exp: RuntimeException) {

        }
        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)

        if(!wantChange)
        btnCorrectListOfClients.setOnCheckedChangeListener { _, isChecked ->
            filterList(isChecked)
        }


        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {

            if (btnCorrectListOfClients.isChecked) {
                btnCorrectListOfClients.isChecked = false
                filterList(btnCorrectListOfClients.isChecked)
            }

            if(wantChange){
                // тут надо сделать проверку признак wantChange если да, то с одним или с другим
                val someList = typeOfWorkRecyclerViewAdapter4.getListData()
                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                    for (i in someList) {
                        dao.updatePriceByTypeCategory(
                            i._idTypeCategory,
                            i.Price,
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
            }else {
                // тут надо сделать проверку признак wantChange если да, то с одним или с другим
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


        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(isChecked: Boolean) {
        if (isChecked) {

            Log.d("mytag", "Флажок выбран")
            // в recycler view удалить все строки содержащие нули
            val items = typeOfWorkRecyclerViewAdapter.getListData()
            listDataFull.clear()
            listDataFull.addAll(items)

            for (value in items) {

                Log.d("mytag", "items print = ${value.CategoryName}")

            }

            items.removeAll { it.Count.toInt() == 0 }

            typeOfWorkRecyclerViewAdapter.setListData(items)
        } else {

            Log.d("mytag", "Флажок не выбран")

            for (i in listDataFull) {
                Log.d("mytag", "listDataFull перед тем как обновлять данные ${i.CategoryName}")
            }
            // мб записывать listDataFull в shared Preferense
            // в recycler view вывести все строк
            val items = typeOfWorkRecyclerViewAdapter.getListData()
            for ((counter, i) in listDataFull.withIndex()) {
                Log.d("mytag", "listDataFull print = ${i.CategoryName}")
                for (j in items) {
                    if (j.CategoryName == i.CategoryName) {
                        // совпали имена, но значения штук могут быть разные
                        Log.d(
                            "mytag",
                            "отработала проверка перезаписи $counter and ${i.CategoryName}"
                        )

                        listDataFull[counter] = j
                    }
                }
            }
            val listDataShort = ArrayList<ClientAndEstimateMidifation>()
            listDataShort.clear()
            listDataShort.addAll(listDataFull)
            typeOfWorkRecyclerViewAdapter.setListData(listDataShort)
        }

        typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()

    }

    // Kotlin
    override fun onBackPressed() {

        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)
        if (btnCorrectListOfClients.isChecked) {
            btnCorrectListOfClients.isChecked = false
            filterList(btnCorrectListOfClients.isChecked)
        }
        if(wantChange){
            val someList = typeOfWorkRecyclerViewAdapter4.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                for (i in someList) {
                    dao.updatePriceByTypeCategory(
                        i._idTypeCategory,
                        i.Price,
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
        else {
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


    }

    private fun gettransition() {
        val intent = Intent(this, Calculation::class.java).also {
            it.putExtra("ClientEntity", getClientFromPreviousActivity())
            it.putExtra("PreviousActivity", "TypeOfWorkActivity")
        }
        startActivity(intent)
    }

    private fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client

    override fun onDeleteUserClickListener(user: ClientAndEstimateMidifation) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeClick(
        data: ClientAndEstimateMidifation,
        typeChange: String,
        priceOld: Int,
        countOld: Float,
        deltaEdit: Float,

        ) {
        val tv = findViewById<TextView>(R.id.textView2)
        val oldSum = tv.text.split(" ")[1].toFloat()
        var newSum = 0F
        if (typeChange == "down") newSum = oldSum - data.Price
        else if (typeChange == "up") newSum = oldSum + data.Price
        else if (typeChange == "set") newSum = oldSum + data.Price * deltaEdit


        val string = "сумма: $newSum ₽"
        tv.text = string

        val indexPrevious = typeOfWorkRecyclerViewAdapter.getListData().indexOf(
            ClientAndEstimateMidifation(
                data.ClientName,
                countOld,
                data._idTypeCategory,
                data._idTypeOfWork,
                priceOld,
                data.CategoryName,
                data.NameTypeOfWork,
                data.TypeLayout,
                data.UnitsOfMeasurement,
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

    override fun onItemClickListener(user: ClientAndEstimateMidifation) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }

    override fun onDeleteUserClickListener(user: ViewEstimate) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeClick(data: ViewEstimate, oldPrice: Int, typeChange: String) {



            if (typeChange == "set") {

            }


            val indexPrevious = typeOfWorkRecyclerViewAdapter4.getListData().indexOf(
                ViewEstimate(
                    data._idTypeCategory,
                    data._idTypeOfWork,
                    oldPrice,
                    data.CategoryName,
                    data.UnitsOfMeasurement,

                )
            )
            val items = typeOfWorkRecyclerViewAdapter4.getListData()

            items.add(indexPrevious, data)
            items.removeAt(indexPrevious + 1)

            for (i in items) {
                Log.d("mytag", "items print = ${i.CategoryName}")
            }
            typeOfWorkRecyclerViewAdapter4.setListData(items)
            typeOfWorkRecyclerViewAdapter4.notifyDataSetChanged()
    }

    override fun onItemClickListener(user: ViewEstimate) {

    }
}

