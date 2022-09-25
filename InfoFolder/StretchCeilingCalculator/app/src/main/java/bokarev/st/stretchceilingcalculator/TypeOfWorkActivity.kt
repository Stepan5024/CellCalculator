package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapterForCountInEstimate
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapterForPriceInEstimate
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import kotlinx.android.synthetic.main.type_of_work_activity.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt


@OptIn(DelicateCoroutinesApi::class)
class TypeOfWorkActivity : AppCompatActivity(), TypeOfWorkRecyclerViewAdapterForCountInEstimate.RowClickListenerRecyclerCountInEstimate,
    TypeOfWorkRecyclerViewAdapterForPriceInEstimate.RowClickListenerRecyclerPriceInEstimate {

    private var listDataFull: MutableList<ClientAndEstimateModification> = arrayListOf()
    private var wantChange = false

    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapterForCountInEstimate
    private lateinit var typeOfWorkRecyclerViewAdapterForPriceInEstimate: TypeOfWorkRecyclerViewAdapterForPriceInEstimate

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)

        val recyclerView: RecyclerView = findViewById(R.id.TypeOfWorkRecyclerView)
        val tvNameOfWork: TextView = findViewById(R.id.tvNameOfWork)
        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)
        val dao = CategoriesDataBase.getInstance(this).categoriesDao

        var idTypesOfWorkList: MutableList<Int>

        var allListTypesOfWork: Boolean
        val previousActivity: String


        try {
            previousActivity = intent.getStringExtra("PreviousActivity").toString()
            val client = getClientFromPreviousActivity()
            allListTypesOfWork = intent.getBooleanExtra("idTypeOfWork", false)
            wantChange = intent.getBooleanExtra("WantChange", false)
            idTypesOfWorkList =
                intent.getIntegerArrayListExtra("idTypeOfWorkList") as ArrayList<Int>

            Log.d("mytag", "WantChange Value = $wantChange")

            if (wantChange) {


                createRecyclerViewAboutPrice(recyclerView)


                val job = GlobalScope.launch(Dispatchers.Default) {

                    Log.d("mytag", "idTypesOfWorkList data size = ${idTypesOfWorkList.size}")
                    Log.d("mytag", "idTypeOfWork data size = $allListTypesOfWork")
                    val someList: MutableList<ViewEstimate> = if (allListTypesOfWork)

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

                    typeOfWorkRecyclerViewAdapterForPriceInEstimate.setListData(someList)
                    typeOfWorkRecyclerViewAdapterForPriceInEstimate.notifyDataSetChanged()

                }
                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    //set view
                    Log.d("mytag", "Main Thread is Running")
                }
            } else {

                createRecyclerViewAboutEstimate(recyclerView)

                val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

                lifecycleScope.launch {
                    var getClientAndEstimate: MutableList<ClientAndEstimate>

                    if (allListTypesOfWork) {
                        // надо вывести весь список со всеми категориями
                        idTypesOfWorkList =
                            dao.getIdTypeOfWorkList() // typeOfWorkList с столбцом только ID
                        // заполняем массив с Id всех категорий
                    }


                   // if (idTypesOfWorkList.size == 0) idTypesOfWorkList.add(allListTypesOfWork) // если надо вывести только конкретную категорию, то ее просто заносим в лист

                    // выводим массив категорий

                    var idTypeOfWork = 0
                    for (i in idTypesOfWorkList) {

                        Log.d("mytag", "i = $i ")
                        idTypeOfWork = i

                        getClientAndEstimate =
                            dao.getUnionClientAndEstimateAndTypeCategory2(
                                getClientFromPreviousActivity()._id,
                                idTypeOfWork
                            )
                        for (j in setListDataByClient(
                            getClientAndEstimate,
                            idTypeOfWork,
                            dao
                        )) {
                            finalList.add(j)
                        }
                        //  }
                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()

                    // фильтрация нужна или нет?

                }

            }

            // вывести значение суммы по категори
            if (previousActivity == "Calculation" || allListTypesOfWork) {
                var sum = 0f

                tvNameOfWork.text = intent.getStringExtra("NameTypeOfWork").toString()

                val job = GlobalScope.launch(Dispatchers.Default) {

                    val someList: MutableList<ClientAndEstimate> = if (allListTypesOfWork)

                    // надо вывести весь список со всеми категориями
                        dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                    else

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
                    if (!wantChange) {

                        val string = "сумма: ${(sum * 100f).roundToInt() / 100f} ₽"
                        tvSum.text = string
                    } else {
                        tvSum.text = "" //  идеале надо удалить с разметки и центрировать оставшееся textview

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

            saveResultInDataBase(dao, btnCorrectListOfClients)

        }

    }

    private fun saveResultInDataBase(dao: TypeCategoryDao, btnCorrectListOfClients: CheckBox) {

        if (btnCorrectListOfClients.isChecked) {
            btnCorrectListOfClients.isChecked = false
            filterList(btnCorrectListOfClients.isChecked)
        }

        if (wantChange) {
            // тут проверка признака wantChange если да, то с одним сохраняем типо или с другим типом
            // тип кол-во в смете
            val someList = typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

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
            // тут проверка признака wantChange если да, то с одним сохраняем типо или с другим типом
            // тип цена, ед. измерения, название
            val someList = typeOfWorkRecyclerViewAdapter.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

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

    private suspend fun setListDataByClient(
        clientAndEstimate: MutableList<ClientAndEstimate>,
        idTypeOfWork: Int,
        dao: TypeCategoryDao
    ): MutableList<ClientAndEstimateModification> {

        val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

        var prev = 0
        for (j in clientAndEstimate) {
            if (j._idTypeOfWork == idTypeOfWork) {
                val nameCategory =
                    dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)

                finalList.add(
                    createClientAndEstimateModificationRow(
                        j,
                        1,
                        nameCategory
                    )
                )
            } else {
                prev++
                val nameCategory =
                    dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)

                finalList.add(
                    createClientAndEstimateModificationRow(
                        j,
                        0,
                        nameCategory
                    )
                )
            }
        }
        return finalList
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
                TypeOfWorkRecyclerViewAdapterForCountInEstimate(this@TypeOfWorkActivity)
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
            typeOfWorkRecyclerViewAdapterForPriceInEstimate =
                TypeOfWorkRecyclerViewAdapterForPriceInEstimate(this@TypeOfWorkActivity)
            adapter = typeOfWorkRecyclerViewAdapterForPriceInEstimate
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

    /*private fun hasDuplicates(someList: MutableList<ViewEstimate>): Boolean {

        for (i in 0 until someList.size) {
            for (j in (i+1) until someList.size) {
                if (someList[j]._id == someList[i]._id) {

                    return true

                }
            }
        }

        return false
    }*/

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(isChecked: Boolean) {
        if (isChecked) {

            Log.d("mytag", "Флажок выбран")
            // в recycler view удалить все строки содержащие нули
            val items = typeOfWorkRecyclerViewAdapter.getListData()
            listDataFull.clear()
            listDataFull.addAll(items)


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


    override fun onBackPressed() {

        val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
        saveResultInDataBase(dao, btnCorrectListOfClients)

       /* val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)
        if (btnCorrectListOfClients.isChecked) {
            btnCorrectListOfClients.isChecked = false
            filterList(btnCorrectListOfClients.isChecked)
        }
        if (wantChange) {
            val someList = typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData()
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
        }*/


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
        } else {
            tv.text = ""
            showHide(btnCorrectListOfClients)

        }
        var indexPrevious = typeOfWorkRecyclerViewAdapter.getListData().indexOf(
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

        /*items.add(indexPrevious, data)
        items.removeAt(indexPrevious + 1)*/
        if (indexPrevious == -1) indexPrevious = 0

        items[indexPrevious] = data

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

    private fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun onDeletePriceClickListener(user: ViewEstimate) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeClickPrice(data: ViewEstimate, oldPrice: Int, typeChange: String) {


        val indexPrevious = typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData().indexOf(
            ViewEstimate(
                data._id,
                data._idTypeOfWork,
                oldPrice,
                data.CategoryName,
                data.UnitsOfMeasurement,

                )
        )
        val items = typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData()

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

        typeOfWorkRecyclerViewAdapterForPriceInEstimate.setListData(items)
        typeOfWorkRecyclerViewAdapterForPriceInEstimate.notifyDataSetChanged()
    }

    override fun onItemClickListener(user: ViewEstimate) {

    }
}

