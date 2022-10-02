package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.type_of_work_activity.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt


@OptIn(DelicateCoroutinesApi::class)
class TypeOfWorkActivity : AppCompatActivity(),
    TypeOfWorkRecyclerViewAdapterForCountInEstimate.RowClickListenerRecyclerCountInEstimate,
    TypeOfWorkRecyclerViewAdapterForPriceInEstimate.RowClickListenerRecyclerPriceInEstimate {


    private var wantChange = false

    private val constantCopyListClient: MutableList<ClientAndEstimateModification> =
        arrayListOf() // содержит в себе первозданную копию массива значений сметы клиента
    private val constantCopyListPrices: MutableList<ViewEstimate> =
        arrayListOf() // содержит в себе первозданную копию массива цен смет

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

        val needAllListTypesOfWork: Boolean
        val previousActivity: String


        try {
            previousActivity = intent.getStringExtra("PreviousActivity").toString()
            needAllListTypesOfWork = intent.getBooleanExtra("idTypeOfWork", false)
            wantChange = intent.getBooleanExtra("WantChange", false)
            idTypesOfWorkList =
                intent.getIntegerArrayListExtra("idTypeOfWorkList") as ArrayList<Int>

            Log.d("mytag", "WantChange Value = $wantChange")

            if (wantChange) {


                createRecyclerViewAboutPrice(recyclerView)


                val job = GlobalScope.launch(Dispatchers.Default) {

                    val someList: MutableList<ViewEstimate> = if (needAllListTypesOfWork)
                    // надо вывести весь список со всеми категориями
                        dao.getTypesCategory()
                    else
                    // выводим список выбранных категорий
                        dao.getEstimateByList(
                            idTypesOfWorkList
                        )


                    typeOfWorkRecyclerViewAdapterForPriceInEstimate.setListData(someList)
                    typeOfWorkRecyclerViewAdapterForPriceInEstimate.notifyDataSetChanged()
                    constantCopyListPrices.addAll(typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData())

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

                    if (needAllListTypesOfWork) {
                        // надо вывести весь список со всеми категориями
                        idTypesOfWorkList =
                            dao.getIdTypeOfWorkList() // typeOfWorkList с столбцом только ID
                        // заполняем массив с Id всех категорий
                    }

                    // выводим массив категорий

                    var idTypeOfWork: Int
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

                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
                    constantCopyListClient.addAll(typeOfWorkRecyclerViewAdapter.getListData())

                    // фильтрация нужна или нет?

                }

            }

            // вывести значение суммы по категори
            if (previousActivity == "Calculation" || needAllListTypesOfWork) {
                var sum = 0f

                tvNameOfWork.text = intent.getStringExtra("NameTypeOfWork").toString()

                val job = GlobalScope.launch(Dispatchers.Default) {

                    val someList: MutableList<ClientAndEstimate> = if (needAllListTypesOfWork)
                    // надо вывести весь список со всеми категориями
                        dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                    else
                        dao.getUnionClientAndEstimateAndTypeCategoryInLists(
                            getClientFromPreviousActivity()._id,
                            idTypesOfWorkList
                        )

                    for (i in someList) sum += i.Price * i.Count


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
                        tvSum.text =
                            "" //  идеале надо удалить с разметки и центрировать оставшееся textview

                        showHide(btnCorrectListOfClients)

                    }
                    Log.d("mytag", "Main Thread is Running")
                }
            }


        } catch (_: RuntimeException) {

        }


        if (!wantChange)
            btnCorrectListOfClients.setOnCheckedChangeListener { _, isChecked ->
                filterList(isChecked)
            }


        val textField = findViewById<TextInputLayout>(R.id.cost_of_service)
        val editText = findViewById<TextInputEditText>(R.id.cost_of_service_edit_text)

        textField.editText?.doOnTextChanged { _, _, _, _ ->
            // Respond to input text change
            Log.d(
                "mytag2",
                " constantCopyListClient size = ${constantCopyListClient.size} doOnTextChanged editText.getText() = ${editText.text}"
            )


            if (!wantChange) {
                // надо в ресуклер цен отфильтроваться по наименованию

                val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

                finalList.clear()
                finalList.addAll(
                    saveChangesInStringFilter(
                        editText,
                        btnCorrectListOfClients.isChecked
                    )
                )
                // обновим данные в финальный лист
                typeOfWorkRecyclerViewAdapter.setListData(finalList)
                typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
                Log.d("mytag", "Main Thread is Running")


            }
        }

        textField.setEndIconOnClickListener {
            // Respond to end icon presses
            // очистить поле ввода
            Log.d("mytag2", "setEndIconOnClickListener обнуление")
            editText.setText("")
            editText.clearFocus()
            saveResultInDataBase(dao, btnCorrectListOfClients, false)
        }

        // очистить фокус с текстового ввода при нажатии на Готово на клаве
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Call onDone result here
                Toast.makeText(this, editText.text, Toast.LENGTH_SHORT).show()
                // clear focus from edit text
                editText.clearFocus()

            }
            false
        }


        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {

            saveResultInDataBase(dao, btnCorrectListOfClients, true)

        }

    }

    private fun saveChangesInStringFilter(
        editText: TextInputEditText,
        isResultFlagActivated: Boolean
    ): Collection<ClientAndEstimateModification> {
        // надо в ресуклер цен отфильтроваться по наименованию
        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)

        val previousList: MutableList<ClientAndEstimateModification> =
            typeOfWorkRecyclerViewAdapter.getListData() // хранятся измененные значения

        val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

        if (editText.text.toString() == "") {
            // когда пользователь нажал очистить все и надо сохранить результат повышения/понижения кол-ва элементов

            for (i in constantCopyListClient) {
                if (previousList.indexOf(i) == -1) {
                    // индекс не найден, а это означает что кол-во у этой категории изменено
                    for (j in previousList) {
                        if (j._idTypeCategory == i._idTypeCategory) {
                            // одинаковое наименование, но разное кол-во
                            i.Count = j.Count

                        }
                    }
                }
                if (btnCorrectListOfClients.isChecked && i.Count > 0) {
                    finalList.add(i)
                    Log.d("mytag3", "check box is not switched")
                } else if (!btnCorrectListOfClients.isChecked)

                    finalList.add(i)

            }

        } else
            for (i in constantCopyListClient) {
                if (i.CategoryName.contains(editText.text.toString())) {
                    // наименование содержит введенный в поиск текст
                    // добавим найденный элемент в финалььный список
                    if (isResultFlagActivated && i.Count > 0) {
                        finalList.add(i)
                        Log.d("mytag3", "check box is not switched")
                    } else if (!isResultFlagActivated)
                        
                        finalList.add(i)
                }
            }
        return finalList
    }

    private fun saveResultInDataBase(
        dao: TypeCategoryDao,
        btnCorrectListOfClients: CheckBox,
        needTransition: Boolean
    ) {
        var flag = false
        if (btnCorrectListOfClients.isChecked) {
            btnCorrectListOfClients.isChecked = false
            filterList(btnCorrectListOfClients.isChecked)
            flag = true
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
                if (needTransition)
                    getTransition()
                Log.d("mytag", "Main Thread is Running")
            }
        } else {
            // тут проверка признака wantChange если да, то с одним сохраняем типо или с другим типом
            // тип цена, ед. измерения, название
            val someList = //constantCopyListClient
                typeOfWorkRecyclerViewAdapter.getListData()
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
                if (needTransition)
                    getTransition()
                Log.d("mytag", "Main Thread is Running")
            }
        }

        if (flag) {
            btnCorrectListOfClients.isChecked = true
            filterList(btnCorrectListOfClients.isChecked)
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
        val editText = findViewById<TextInputEditText>(R.id.cost_of_service_edit_text)


        if (isChecked) {

            Log.d("mytag", "Флажок выбран")
            // в recycler view удалить все строки содержащие нули
            val items =
                typeOfWorkRecyclerViewAdapter.getListData()
            val items2 = ArrayList<ClientAndEstimateModification>()


            items.removeAll { it.Count == 0F }
            if (editText.text.toString() != "") {

                items2.addAll(
                    items.filter { it.CategoryName.contains(editText.text.toString()) }
                        .toMutableList()
                ) // все символы, кроме 'z'
            } else {
                items2.addAll(items)
            }
            typeOfWorkRecyclerViewAdapter.setListData(items2)
        } else {

            Log.d("mytag", "Флажок не выбран")

            for (i in constantCopyListClient) {
                Log.d(
                    "mytag",
                    "constantCopyListClient перед тем как обновлять данные ${i.CategoryName}"
                )
            }
            // мб записывать constantCopyListClient в shared Preferense
            // в recycler view вывести все строк
            val items = //= constantCopyListClient
                typeOfWorkRecyclerViewAdapter.getListData()
            for ((counter, i) in constantCopyListClient.withIndex()) {
                Log.d("mytag", "constantCopyListClient print = ${i.CategoryName}")
                for (j in items) {
                    if (j.CategoryName == i.CategoryName && j._idTypeOfWork == i._idTypeOfWork && j._idTypeCategory == i._idTypeCategory) {
                        // совпали имена, но значения штук могут быть разные
                        Log.d(
                            "mytag",
                            "отработала проверка перезаписи $counter and ${i.CategoryName}"
                        )

                        constantCopyListClient[counter] = j

                    }
                }
            }
            val listDataShort = ArrayList<ClientAndEstimateModification>()
            val listDataShort2 = ArrayList<ClientAndEstimateModification>()
            listDataShort.clear()
            listDataShort.addAll(constantCopyListClient)
            if (editText.text.toString() != "") {

                listDataShort2.addAll(listDataShort.filter { it.CategoryName.contains(editText.text.toString()) } as ArrayList<ClientAndEstimateModification>) //

            } else {
                listDataShort2.addAll(listDataShort)
            }

            typeOfWorkRecyclerViewAdapter.setListData(listDataShort2)
        }

        typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()

    }


    override fun onBackPressed() {

        val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity).categoriesDao
        saveResultInDataBase(dao, btnCorrectListOfClients, true)

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

        /*items.add(indexPrevious, data)
        items.removeAt(indexPrevious + 1)*/
        //if (indexPrevious == -1) indexPrevious = 0

        items[indexPrevious] = data

        val indexPreviousInConstantList = constantCopyListClient.indexOf(
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

        constantCopyListClient[indexPreviousInConstantList] = data
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

