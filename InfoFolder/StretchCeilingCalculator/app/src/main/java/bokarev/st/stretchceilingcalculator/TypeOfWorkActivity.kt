package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapterForCountInEstimate
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
class TypeOfWorkActivity : AppCompatActivity(){


    private var wantChange = false

    private val constantCopyListClient: MutableList<ClientAndEstimateModification> =
        arrayListOf() // содержит в себе первозданную копию массива значений сметы клиента

    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapterForCountInEstimate


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


                createRecyclerViewAboutEstimate(recyclerView)
                //createRecyclerViewAboutPrice(recyclerView)


                val job = GlobalScope.launch(Dispatchers.Default) {

                    val someList: MutableList<ViewEstimate> = if (needAllListTypesOfWork)
                    // надо вывести весь список со всеми категориями
                        dao.getTypesCategory()
                    else
                    // выводим список выбранных категорий
                        dao.getEstimateByList(
                            idTypesOfWorkList
                        )

                    // преобразуем viewEstimate to ClientAndModification
                    var idPreviousTypeOfWork = -1
                    val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

                    for (j in someList) {
                        val nameOfWork = dao.getTypeOfWorkNameByTypeCategory(j._idTypeOfWork)
                        if (idPreviousTypeOfWork != j._idTypeOfWork) {

                            finalList.add(
                                ClientAndEstimateModification(
                                    "разделительный элемент",
                                    0f,
                                    j._id,
                                    j._idTypeOfWork,
                                    j.Price,
                                    j.CategoryName,
                                    nameOfWork,
                                    2,
                                    j.UnitsOfMeasurement,
                                )
                            )
                            idPreviousTypeOfWork = j._idTypeOfWork
                        }
                        finalList.add(
                            ClientAndEstimateModification(
                                "меняем цены",
                                0f,
                                j._id,
                                j._idTypeOfWork,
                                j.Price,
                                j.CategoryName,
                                nameOfWork,
                                3,
                                j.UnitsOfMeasurement,
                            )
                        )

                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
                    constantCopyListClient.addAll(typeOfWorkRecyclerViewAdapter.getListData())

                    /*                  typeOfWorkRecyclerViewAdapterForPriceInEstimate.setListData(someList)
                                      typeOfWorkRecyclerViewAdapterForPriceInEstimate.notifyDataSetChanged()
                                      constantCopyListPrices.addAll(typeOfWorkRecyclerViewAdapterForPriceInEstimate.getListData())
                  */
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

                val job = GlobalScope.launch(Dispatchers.Default) {
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

                        // обычный элемент имеет тип layout = 1
                        getClientAndEstimate =
                            dao.getUnionClientAndEstimateAndTypeCategory2(
                                getClientFromPreviousActivity()._id,
                                idTypeOfWork
                            )
                        // разделительный элемент имеет тип layout = 2

                        var idPreviousTypeOfWork = -1
                        for (j in setListDataByClient(
                            getClientAndEstimate,
                            idTypeOfWork,
                            dao
                        )) {
                            if (idPreviousTypeOfWork != j._idTypeOfWork) {

                                finalList.add(
                                    ClientAndEstimateModification(
                                        "разделительный элемент",
                                        j.Count,
                                        j._idTypeCategory,
                                        j._idTypeOfWork,
                                        j.Price,
                                        j.CategoryName,
                                        j.NameTypeOfWork,
                                        2,
                                        j.UnitsOfMeasurement,
                                    )
                                )
                                idPreviousTypeOfWork = j._idTypeOfWork
                            }

                            finalList.add(j)
                        }

                    }
                    typeOfWorkRecyclerViewAdapter.setListData(finalList)
                    typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
                    constantCopyListClient.addAll(typeOfWorkRecyclerViewAdapter.getListData())

                    // фильтрация нужна или нет?


                }
                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    //set view


                    if (needAllListTypesOfWork) {
                        if (!btnCorrectListOfClients.isChecked) {
                            btnCorrectListOfClients.isChecked = true
                            filterList(btnCorrectListOfClients.isChecked)

                        }
                    }
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

                val finalList: MutableList<ClientAndEstimateModification> = mutableListOf()

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


            } else {
                // мы меняем цены, фильтруем позиции цен

                val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()

                finalList.clear()
                finalList.addAll(
                    saveChangesPricesInStringFilter(
                        editText,

                        )
                )
                // обновим данные в финальный лист

                typeOfWorkRecyclerViewAdapter.setListData(finalList)
                typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()


            }
        }

        textField.setEndIconOnClickListener {
            // Respond to end icon presses
            // очистить поле ввода
            Log.d("mytag2", "setEndIconOnClickListener обнуление")
            saveResultInDataBase(dao, btnCorrectListOfClients, false)
            editText.setText("")
            editText.clearFocus()


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


        val btnReturnToHome: LinearLayout = findViewById(R.id.btnReturnToHome)
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

        val temp: MutableList<ClientAndEstimateModification> = arrayListOf()
        temp.addAll(constantCopyListClient)



        constantCopyListClient.clear()
        constantCopyListClient.addAll(
            temp.filter { it.TypeLayout != 2 }
                .toMutableList()
        )

        val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()
        var idPreviousTypeOfWork = -1


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
                    if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1
                    ) {

                        finalList.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork
                    }
                    finalList.add(i)
                    Log.d("mytag3", "check box is not switched")
                } else if (!btnCorrectListOfClients.isChecked) {
                    if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1
                    ) {

                        finalList.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork
                    }


                    finalList.add(i)
                }
            }

        } else
            for (i in constantCopyListClient) {
                if (i.CategoryName.lowercase().contains(editText.text.toString().lowercase())) {
                    // наименование содержит введенный в поиск текст
                    // добавим найденный элемент в финалььный список


                    if (isResultFlagActivated && i.Count > 0) {

                        if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                                ClientAndEstimateModification(
                                    "разделительный элемент",
                                    i.Count,
                                    i._idTypeCategory,
                                    i._idTypeOfWork,
                                    i.Price,
                                    i.CategoryName,
                                    i.NameTypeOfWork,
                                    2,
                                    i.UnitsOfMeasurement,
                                )
                            ) == -1
                        ) {

                            finalList.add(
                                ClientAndEstimateModification(
                                    "разделительный элемент",
                                    i.Count,
                                    i._idTypeCategory,
                                    i._idTypeOfWork,
                                    i.Price,
                                    i.CategoryName,
                                    i.NameTypeOfWork,
                                    2,
                                    i.UnitsOfMeasurement,
                                )
                            )
                            idPreviousTypeOfWork = i._idTypeOfWork
                        }


                        finalList.add(i)
                        Log.d("mytag3", "check box is not switched")
                    } else if (!isResultFlagActivated)
                        if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                                ClientAndEstimateModification(
                                    "разделительный элемент",
                                    i.Count,
                                    i._idTypeCategory,
                                    i._idTypeOfWork,
                                    i.Price,
                                    i.CategoryName,
                                    i.NameTypeOfWork,
                                    2,
                                    i.UnitsOfMeasurement,
                                )
                            ) == -1
                        ) {

                            finalList.add(
                                ClientAndEstimateModification(
                                    "разделительный элемент",
                                    i.Count,
                                    i._idTypeCategory,
                                    i._idTypeOfWork,
                                    i.Price,
                                    i.CategoryName,
                                    i.NameTypeOfWork,
                                    2,
                                    i.UnitsOfMeasurement,
                                )
                            )
                            idPreviousTypeOfWork = i._idTypeOfWork
                        }


                    finalList.add(i)
                }
            }


        return finalList
    }

    private fun saveChangesPricesInStringFilter(
        editText: TextInputEditText,

        ): Collection<ClientAndEstimateModification> {
        // надо в ресуклер цен отфильтроваться по наименованию

        val previousList: MutableList<ClientAndEstimateModification> =
            typeOfWorkRecyclerViewAdapter.getListData() // хранятся измененные значения

        val temp: MutableList<ClientAndEstimateModification> = arrayListOf()
        temp.addAll(constantCopyListClient)



        constantCopyListClient.clear()
        constantCopyListClient.addAll(
            temp.filter { it.TypeLayout != 2 }
                .toMutableList()
        )

        val finalList: MutableList<ClientAndEstimateModification> = arrayListOf()
        var idPreviousTypeOfWork = -1

        if (editText.text.toString() == "") {
            // когда пользователь нажал очистить все и надо сохранить результат повышения/понижения цен элементов

            for (i in constantCopyListClient) {
                if (previousList.indexOf(i) == -1) {
                    // индекс не найден, а это означает что кол-во у этой категории изменено
                    for (j in previousList) {
                        if (j._idTypeCategory == i._idTypeCategory) {
                            // одинаковое наименование, но разная цена
                            Log.d(
                                "mytagRest",
                                "j._id = ${j._idTypeCategory} = i._id ${i._idTypeCategory} price = ${i.Price} and  "
                            )
                            // как показывает тестирование в этот цикл не заходит программа
                            i.Price = j.Price

                        }
                    }
                }
                if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                        ClientAndEstimateModification(
                            "разделительный элемент",
                            i.Count,
                            i._idTypeCategory,
                            i._idTypeOfWork,
                            i.Price,
                            i.CategoryName,
                            i.NameTypeOfWork,
                            2,
                            i.UnitsOfMeasurement,
                        )
                    ) == -1
                ) {

                    finalList.add(
                        ClientAndEstimateModification(
                            "разделительный элемент",
                            i.Count,
                            i._idTypeCategory,
                            i._idTypeOfWork,
                            i.Price,
                            i.CategoryName,
                            i.NameTypeOfWork,
                            2,
                            i.UnitsOfMeasurement,
                        )
                    )
                    idPreviousTypeOfWork = i._idTypeOfWork
                }

                finalList.add(i)

            }

        } else
            for (i in constantCopyListClient) {
                if (i.CategoryName.lowercase().contains(editText.text.toString().lowercase())) {
                    // наименование содержит введенный в поиск текст
                    // добавим найденный элемент в финалььный список
                    if (idPreviousTypeOfWork != i._idTypeOfWork && finalList.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1
                    ) {

                        finalList.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork
                    }

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
            val someList =
                typeOfWorkRecyclerViewAdapter.getListData()
            val job = GlobalScope.launch(Dispatchers.Default) {

                for (i in someList) {
                    dao.updatePriceByTypeCategory(
                        i._idTypeCategory,
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
                    if (i.ClientName != "разделительный элемент")
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

        //var prev = 0
        for (j in clientAndEstimate) {
            // if (j._idTypeOfWork == idTypeOfWork) {
            val nameCategory =
                dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)

            finalList.add(
                createClientAndEstimateModificationRow(
                    j,
                    1,
                    nameCategory
                )
            )
            /* } else {
                 // ветка не работает
                 prev++
                 val nameCategory =
                     dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)

                 finalList.add(
                     createClientAndEstimateModificationRow(
                         j,
                         200,
                         nameCategory
                     )
                 )
             }

             */
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

        }
    }


   /* private fun hasDuplicates(someList: MutableList<ClientAndEstimateModification>): ClientAndEstimateModification {

        for (i in 0 until someList.size) {
            for (j in (i + 1) until someList.size) {
                if (someList[j].ClientName == someList[i].ClientName) {

                    return someList[j]
                    //return true
                }
            }
        }
        // return false
        return ClientAndEstimateModification("разделительный элемент", 0f, 0, 0, 0, "", "", 2, "")
    }*/

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(isChecked: Boolean) {
        val editText = findViewById<TextInputEditText>(R.id.cost_of_service_edit_text)

        val temp: MutableList<ClientAndEstimateModification> = arrayListOf()
        temp.addAll(constantCopyListClient)


        constantCopyListClient.clear()
        constantCopyListClient.addAll(
            temp.filter { it.TypeLayout != 2 }
                .toMutableList()
        )


        if (isChecked) {

            Log.d("mytag", "Флажок выбран")
            // в recycler view удалить все строки содержащие нули
            val items =
                typeOfWorkRecyclerViewAdapter.getListData()
            val items2 = ArrayList<ClientAndEstimateModification>()


            items.removeAll { it.Count == 0F }
            if (editText.text.toString() != "") {

                var idPreviousTypeOfWork = -1
                for (i in constantCopyListClient) {
                    if (idPreviousTypeOfWork != i._idTypeOfWork && items2.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1 && i.CategoryName.lowercase()
                            .contains(editText.text.toString().lowercase()) && i.Count != 0f
                    ) {

                        items2.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork

                    }
                    if (i.CategoryName.lowercase()
                            .contains(editText.text.toString().lowercase()) && i.Count != 0f
                    )
                        items2.add(i)
                }
                /* items2.addAll(
                     items.filter { it.CategoryName.contains(editText.text.toString()) }
                        // .filter { it.TypeLayout != 2 }
                         .toMutableList()
                 )*/
            } else {
                var idPreviousTypeOfWork = -1
                for (i in constantCopyListClient) {
                    if (idPreviousTypeOfWork != i._idTypeOfWork && items2.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1 && i.Count != 0f
                    ) {

                        items2.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork

                    }
                    if (i.Count != 0f)
                        items2.add(i)
                }
                //items2.addAll(items)
            }

            typeOfWorkRecyclerViewAdapter.setListData(items2)
        } else {

            Log.d("mytag", "Флажок не выбран")

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

                // listDataShort2.addAll(listDataShort.filter { it.CategoryName.contains(editText.text.toString()) } as ArrayList<ClientAndEstimateModification>) //
                var idPreviousTypeOfWork = -1
                for (i in constantCopyListClient) {
                    if (idPreviousTypeOfWork != i._idTypeOfWork && listDataShort2.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1 && i.CategoryName.lowercase()
                            .contains(editText.text.toString().lowercase())
                    ) {

                        listDataShort2.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork

                    }
                    if (i.CategoryName.lowercase().contains(editText.text.toString().lowercase()))
                        listDataShort2.add(i)
                }

                typeOfWorkRecyclerViewAdapter.setListData(listDataShort2)

            } else {
                var idPreviousTypeOfWork = -1
                for (i in constantCopyListClient) {
                    if (idPreviousTypeOfWork != i._idTypeOfWork && listDataShort2.indexOf(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        ) == -1
                    ) {

                        listDataShort2.add(
                            ClientAndEstimateModification(
                                "разделительный элемент",
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                                i.NameTypeOfWork,
                                2,
                                i.UnitsOfMeasurement,
                            )
                        )
                        idPreviousTypeOfWork = i._idTypeOfWork

                    }

                    listDataShort2.add(i)
                    //listDataShort2.addAll(listDataShort)
                }

                typeOfWorkRecyclerViewAdapter.setListData(listDataShort2)
            }
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
    fun onChangeClick(
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


        items[indexPrevious] = data

        var indexPreviousInConstantList = constantCopyListClient.indexOf(
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
        //if (indexPreviousInConstantList == -1) indexPreviousInConstantList = 0

        constantCopyListClient[indexPreviousInConstantList] = data
        typeOfWorkRecyclerViewAdapter.setListData(items)
        typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun onItemClickListener(user: ClientAndEstimateModification) {


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

    fun onDeletePriceClickListener(user: ViewEstimate) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onChangeClickPrice(data: ClientAndEstimateModification, oldPrice: Int, typeChange: String) {

        val indexPrevious = typeOfWorkRecyclerViewAdapter.getListData().indexOf(
            ClientAndEstimateModification(
                data.ClientName,
                data.Count,
                data._idTypeCategory,
                data._idTypeOfWork,
                oldPrice,
                data.CategoryName,
                data.NameTypeOfWork,
                data.TypeLayout,
                data.UnitsOfMeasurement,

                )
        )


        val indexPreviousInConstantList = constantCopyListClient.indexOf(
            ClientAndEstimateModification(
                data.ClientName,
                data.Count,
                data._idTypeCategory,
                data._idTypeOfWork,
                oldPrice,
                data.CategoryName,
                data.NameTypeOfWork,
                data.TypeLayout,
                data.UnitsOfMeasurement,

                )
        )
        val items = typeOfWorkRecyclerViewAdapter.getListData()

        Log.d(
            "mytagStepan",
            "index arr = $indexPrevious data new price = ${data.Price} | data old price = $oldPrice"
        )

        items[indexPrevious] = data



        Log.d(
            "mytagStepan",
            "index arr items= $indexPrevious data new price = ${items[indexPrevious].Price} | data old price = $oldPrice"
        )

        constantCopyListClient[indexPreviousInConstantList] = data
        typeOfWorkRecyclerViewAdapter.setListData(items)
        try {
            typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()
        } catch (_: IllegalStateException) {

        }


    }


    fun onItemPriceClickListener(user: ViewEstimate) {

    }
}

