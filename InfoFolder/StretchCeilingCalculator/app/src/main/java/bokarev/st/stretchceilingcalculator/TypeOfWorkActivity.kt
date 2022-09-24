package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter3
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter4
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import kotlinx.android.synthetic.main.type_of_work_activity.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt


@OptIn(DelicateCoroutinesApi::class)
class TypeOfWorkActivity : AppCompatActivity(), TypeOfWorkRecyclerViewAdapter3.RowClickListener,
    TypeOfWorkRecyclerViewAdapter4.RowClickListener {

    private var listDataFull: MutableList<ClientAndEstimateModification> = arrayListOf()
    private var wantChange = false

    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter3
    private lateinit var typeOfWorkRecyclerViewAdapter4: TypeOfWorkRecyclerViewAdapter4

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)

        val recyclerView:RecyclerView = findViewById(R.id.TypeOfWorkRecyclerView)
        val tvNameOfWork: TextView = findViewById(R.id.tvNameOfWork)
        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)
        val dao = CategoriesDataBase.getInstance(this).categoriesDao

        val idTypesOfWorkList: ArrayList<Int>

        var idTypeOfWork: Int
        val previousActivity: String


        try {
            previousActivity = intent.getStringExtra("PreviousActivity").toString()
            val client = getClientFromPreviousActivity()
            idTypeOfWork = intent.getIntExtra("idTypeOfWork", -1)
            wantChange = intent.getBooleanExtra("WantChange", false)
            idTypesOfWorkList =
                intent.getIntegerArrayListExtra("idTypeOfWorkList") as ArrayList<Int>
            // idTypeOfWork == 0 означает вывести все категории работ в смете
            // idTypeOfWork == -1 означает что пользователь попал на активность не по кнопкам Система, Освещение, Доп. работы, материалы
            if (idTypeOfWork == -1) exceptionReturn()

            Log.d("mytag", "WantChange Value = $wantChange")

            if (wantChange) {


                createRecyclerViewAboutPrice(recyclerView)

                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                val job = GlobalScope.launch(Dispatchers.Default) {

                    Log.d("mytag", "idTypesOfWorkList data size = ${idTypesOfWorkList.size}")
                    Log.d("mytag", "idTypeOfWork data size = $idTypeOfWork")
                    val someList: MutableList<ViewEstimate> = if (idTypeOfWork == 0)

                    // надо вывести весь список со всеми категориями
                        dao.getTypesCategory()

                    else
                    // выводим список выбранных категорий
                        dao.getEstimateByList(
                            idTypesOfWorkList
                        )

                    Log.d("mytag", "someList data size = ${someList.size}")

                    /*if (hasDuplicates(someList)) {
                        Log.d("mytag", "Repeated id elements found = ${someList.size}")
                        println("Repeated id elements found")
                    } else {
                        println("No repeated elements found")
                        Log.d("mytag", "No repeated elements found = ${someList.size}")
                    }*/

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

                createRecyclerViewAboutEstimate(recyclerView)

                var finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

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

                        var previousIdTypeOfWork = 0
                        // здесь сделать разный тип лайаута для ресуклер вью. Например разные категории разделять фоновым цветом или записью
                        for (i in getClientAndEstimate) {
                            if (i._idTypeOfWork == previousIdTypeOfWork) {
                                val nameCategory = dao.getTypeOfWorkNameByTypeCategory(previousIdTypeOfWork)

                                finalList.add(createClientAndEstimateModificationRow(i, 1, nameCategory))

                            } else {
                                previousIdTypeOfWork++
                                val nameCategory = dao.getTypeOfWorkNameByTypeCategory(previousIdTypeOfWork)

                                finalList.add(createClientAndEstimateModificationRow(i, 0, nameCategory))
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
                                        val clientAndEstimateModification =
                                            ClientAndEstimateModification(
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
                                        finalList.add(clientAndEstimateModification)

                                    } else {
                                        prev++
                                        val nameCategory =
                                            dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)
                                        /* val clientAndEstimateMidifation1 =
                                             ClientAndEstimateModification(
                                                 "NewList",
                                                 0,
                                                 i._idTypeCategory,
                                                 prev,
                                                 0,
                                                 nameCategory,
                                                 nameCategory,
                                                 0
                                             )*/

                                        val clientAndEstimateModification2 =
                                            ClientAndEstimateModification(
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
                                        finalList.add(clientAndEstimateModification2)
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
                                    val clientAndEstimateModification =
                                        ClientAndEstimateModification(
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
                                    finalList.add(clientAndEstimateModification)

                                } else {
                                    prev++
                                    val nameCategory = dao.getTypeOfWorkNameByTypeCategory(prev)
                                    /* val clientAndEstimateMidifation1 =
                                         ClientAndEstimateModification(
                                             "NewList",
                                             0,
                                             i._idTypeCategory,
                                             prev,
                                             0,
                                             nameCategory,
                                             nameCategory,
                                             0
                                         )*/

                                    val clientAndEstimateModification2 =
                                        ClientAndEstimateModification(
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
                                    finalList.add(clientAndEstimateModification2)
                                }
                            }
                        }
                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()

                    // фильтрация нужна или нет?

                }


            }


            // вывести значение суммы по категори
            if (previousActivity == "Calculation" || idTypeOfWork == 0) {
                var sum = 0f


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
                    if(!wantChange) {

                        val string = "сумма: ${(sum * 100f).roundToInt() / 100f} ₽"
                        tvSum.text = string
                    }else {
                        tvSum.text = ""
                        showHide(btnCorrectListOfClients)

                    }
                    Log.d("mytag", "Main Thread is Running")
                }
            }

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName} idClient = ${client._id}"
            )


        } catch (exp: RuntimeException) {

        }


        if (!wantChange)
            btnCorrectListOfClients.setOnCheckedChangeListener { _, isChecked ->
                filterList(isChecked)
            }


        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {

            if (btnCorrectListOfClients.isChecked) {
                btnCorrectListOfClients.isChecked = false
                filterList(btnCorrectListOfClients.isChecked)
            }

            if (wantChange) {
                // тут надо сделать проверку признак wantChange если да, то с одним или с другим
                val someList = typeOfWorkRecyclerViewAdapter4.getListData()
                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                    for (i in someList) {
                        dao.updatePriceByTypeCategory(
                            i._id,
                            i.Price,
                        )
                        Log.d("mytag", "items back print = ${i.CategoryName} prise = ${i.Price}")
                    }
                }

                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    getTransition()
                    Log.d("mytag", "Main Thread is Running")
                }
            } else {
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
                    getTransition()
                    Log.d("mytag", "Main Thread is Running")
                }
            }


        }

    }

    private fun createClientAndEstimateModificationRow(
        clientAndEstimate: ClientAndEstimate,
        typeLayout: Int,
        nameCategory: String
    ): ClientAndEstimateModification {
        return ClientAndEstimateModification(
            clientAndEstimate.CategoryName,
            clientAndEstimate.Count,
            clientAndEstimate._idTypeCategory,
            clientAndEstimate._idTypeOfWork,
            clientAndEstimate.Price,
            clientAndEstimate.CategoryName,
            nameCategory,
            typeLayout,
            clientAndEstimate.UnitsOfMeasurement,
        )
    }

    private fun createRecyclerViewAboutEstimate(recyclerView: RecyclerView) {

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
    }

    private fun createRecyclerViewAboutPrice(recyclerView: RecyclerView) {

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
    }

    private fun exceptionReturn() {
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

    private fun hasDuplicates(someList: MutableList<ViewEstimate>): Boolean {

        for (i in 0 until someList.size) {
            for (j in (i+1) until someList.size) {
                if (someList[j]._id == someList[i]._id) {

                    return true

                }
            }
        }

        return false
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
                // Log.d("mytag", "items print = ${value.CategoryName}")
            }

            items.removeAll { it.Count == 0F }

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
                    if (j.CategoryName == i.CategoryName && j._idTypeOfWork == i._idTypeOfWork && j._idTypeCategory == i._idTypeCategory) {
                        // совпали имена, но значения штук могут быть разные
                        Log.d(
                            "mytag",
                            "отработала проверка перезаписи $counter and ${i.CategoryName}"
                        )

                        listDataFull[counter] = j

                    }
                }
            }
            val listDataShort = ArrayList<ClientAndEstimateModification>()
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
        if (wantChange) {
            val someList = typeOfWorkRecyclerViewAdapter4.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
                for (i in someList) {
                    dao.updatePriceByTypeCategory(
                        i._id,
                        i.Price,
                    )
                    if (i.CategoryName == "Установка потолочного профиля") {
                        Log.d(
                            "mytag",
                            "items back button print  = ${i.CategoryName} new price = ${i.Price} id = ${i._id}"
                        )
                    }

                }
            }

            runBlocking {
                // waiting for the coroutine to finish it"s work
                job.join()
                getTransition()
                Log.d("mytag", "Main Thread is Running")
            }
        } else {
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
                getTransition()
                Log.d("mytag", "Main Thread is Running")
            }
        }


    }

    private fun getTransition() {
        val intent = Intent(this, Calculation::class.java).also {
            it.putExtra("ClientEntity", getClientFromPreviousActivity())
            it.putExtra("PreviousActivity", "TypeOfWorkActivity")
        }
        startActivity(intent)
    }

    private fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client

    override fun onDeleteUserClickListener(user: ClientAndEstimateModification) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeClick(
        data: ClientAndEstimateModification,
        typeChange: String,
        priceOld: Int,
        countOld: Float,
        deltaEdit: Float,

        ) {
        val tv = findViewById<TextView>(R.id.textView2)
        val oldSum = tv.text.split(" ")[1].toFloat()
        var newSum = 0F
        when (typeChange) {
            "down" -> newSum = oldSum - data.Price
            "up" -> newSum = oldSum + data.Price
            "set" -> newSum = oldSum + data.Price * deltaEdit
        }

        if (!wantChange) {
            val string = "сумма: $newSum ₽"
            tv.text = string
        }
        else {
            tv.text = ""
            showHide(btnCorrectListOfClients)

        }
        val indexPrevious = typeOfWorkRecyclerViewAdapter.getListData().indexOf(
            ClientAndEstimateModification(
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
        
        typeOfWorkRecyclerViewAdapter.setListData(items)
        typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onItemClickListener(user: ClientAndEstimateModification) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }
    override fun onDeleteUserClickListener(user: ViewEstimate) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeClickPrice(data: ViewEstimate, oldPrice: Int, typeChange: String) {


        val indexPrevious = typeOfWorkRecyclerViewAdapter4.getListData().indexOf(
            ViewEstimate(
                data._id,
                data._idTypeOfWork,
                oldPrice,
                data.CategoryName,
                data.UnitsOfMeasurement,

                )
        )
        val items = typeOfWorkRecyclerViewAdapter4.getListData()

        //items.add(indexPrevious, data)
        //items.removeAt(indexPrevious + 1)
        Log.d(
            "mytag",
            "index arr = $indexPrevious data new price = ${data.Price} | data old price = $oldPrice"
        )
        items[indexPrevious] = data
        Log.d(
            "mytag",
            "index arr items= $indexPrevious data new price = ${items[indexPrevious].Price} | data old price = $oldPrice"
        )
        for (i in items) {
            //Log.d("mytag", "change price  print = ${i.CategoryName} price = ${i.Price}")
        }
        typeOfWorkRecyclerViewAdapter4.setListData(items)
        typeOfWorkRecyclerViewAdapter4.notifyDataSetChanged()
    }

    override fun onItemClickListener(user: ViewEstimate) {

    }
}

