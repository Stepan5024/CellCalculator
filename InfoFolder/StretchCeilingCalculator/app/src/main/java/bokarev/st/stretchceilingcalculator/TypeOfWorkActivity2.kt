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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapter2
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.TypeOfWorkForRecyclerView
import bokarev.st.stretchceilingcalculator.models.Item
import bokarev.st.stretchceilingcalculator.models.News
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class TypeOfWorkActivity2 : AppCompatActivity() {

    var items: MutableList<Item> = arrayListOf()


    //private var listDataFull: MutableList<ClientAndEstimate> = arrayListOf()
    private var listDataFull: MutableList<Item> = arrayListOf()

    private val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity2).categoriesDao

    //private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter
    private lateinit var typeOfWorkRecyclerViewAdapter: TypeOfWorkRecyclerViewAdapter2

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)

        val tvNameOfWork: TextView = findViewById(R.id.tvNameOfWork)

        var idTypesOfWorkList: ArrayList<Int> = arrayListOf()

        var idTypeOfWork = 0
        val previousActivity: String
        try {
            previousActivity = intent.getStringExtra("PreviousActivity").toString()
            val client = getClientFromPreviousActivity()
            idTypeOfWork = intent.getIntExtra("idTypeOfWork", -1)
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

            if (previousActivity == "Calculation") {
                var sum = 0.0

                tvNameOfWork.text = intent.getStringExtra("NameTypeOfWork").toString()

                val job = GlobalScope.launch(Dispatchers.Default) {

                    val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity2).categoriesDao

                    val someList: MutableList<ClientAndEstimate> = if (idTypeOfWork == 0)

                    // надо вывести весь список со всеми категориями
                        dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                    else
                        dao.getUnionClientAndEstimateAndTypeCategoryInLists(
                            getClientFromPreviousActivity()._id,
                            idTypesOfWorkList
                        )
                    /* dao.getUnionClientAndEstimateAndTypeCategory2(
                         getClientFromPreviousActivity()._id,
                         idTypeOfWork
                     )*/

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


        btnCorrectListOfClients.setOnCheckedChangeListener { _, isChecked ->
            filterList(isChecked)
        }


        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {

            if (btnCorrectListOfClients.isChecked) {
                btnCorrectListOfClients.isChecked = false
                filterList(btnCorrectListOfClients.isChecked)
            }



            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity2).categoriesDao
                for (i in items) {

                    if (i.`object`.javaClass == ClientAndEstimate::class.java) {
                        Log.d("mytag", "i.`object` = ${(i.`object`)}")
                        dao.updateCountStrokesEstimateByClient(
                            getClientFromPreviousActivity()._id,
                            //i._idTypeCategory
                            (i.`object` as ClientAndEstimate)._idTypeCategory,
                            //i.Count
                            (i.`object` as ClientAndEstimate).Count
                        )
                        // Log.d("mytag", "items back print = ${i.CategoryName}")
                        Log.d(
                            "mytag",
                            "items back print = ${(i.`object` as ClientAndEstimate).CategoryName} and count ${(i.`object` as ClientAndEstimate).Count}"
                        )


                    }

                }

            }
            runBlocking {
                // waiting for the coroutine to finish it"s work
                job.join()
                gettransition()
                Log.d("mytag", "Main Thread is Running")
            }
        }
        val tv: TextView = findViewById(R.id.textView2)
        val recyclerView: RecyclerView = findViewById(R.id.TypeOfWorkRecyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TypeOfWorkActivity2)


            // это не работает
            typeOfWorkRecyclerViewAdapter =
                TypeOfWorkRecyclerViewAdapter2(items, recyclerView.adapter, tv, getClientFromPreviousActivity()._id, btnCorrectListOfClients)


            for (i in typeOfWorkRecyclerViewAdapter.getListData()){

                Log.d("mister Stepan", "${i.`object`.javaClass}")
            }
            adapter = typeOfWorkRecyclerViewAdapter
            val divider =
                DividerItemDecoration(applicationContext, StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(divider)
        }

        // 0 - trip, 1- count, 2 - news,

        // 0 - trip, 1- count, 2 - news,
        /*val trip1 = Trip(R.drawable.crovatia, "Croatia", "Summer 20 days")
        items.add(Item(0, trip1))

        val count =
            TypeOfWorkForRecyclerView(
                "Christmas holiday",
                "Winter 20 days"
            )
        items.add(Item(1, count))


        val news = News("Russia", "Summer 20 days")
        items.add(Item(2, news))


        val trip2 = Trip(R.drawable.bali, "Bali", "Summer 20 days")
        items.add(Item(0, trip2))

        val ads2 =
            TypeOfWorkForRecyclerView(
                "Christmas holiday",
                "Winter 20 days"
            )
        items.add(Item(1, ads2))

        val trip3 = Trip(R.drawable.bora_bora, "bora-bora", "Summer 20 days")
        items.add(Item(0, trip3))

        val news2 = News("Russia", "Summer 20 days")
        items.add(Item(2, news2))

        recyclerView.adapter = TypeOfWorkRecyclerViewAdapter2(items)*/


        //Without ViewModelFactory
        val job = GlobalScope.launch(Dispatchers.Default) {
            var getClientAndEstimate: MutableList<ClientAndEstimate>



            if (idTypeOfWork == 0) {
                // надо вывести весь список со всеми категориями

               /* getClientAndEstimate =
                    dao.getClientAndEstimate(getClientFromPreviousActivity()._id)
                Log.d(
                    "mytag",
                    "someList.size = ${getClientAndEstimate.size}  "
                )

                val news = News("Russia")
                items.add(Item(2, news))

                for (i in getClientAndEstimate) {

                    val news2 = ClientAndEstimate(
                        i.ClientName,
                        i.Count,
                        i._idTypeCategory,
                        i._idTypeOfWork,
                        i.Price,
                        i.CategoryName
                        /*
                i.CategoryName,
                i.Price.toString(),
                i.Count.toString()*/
                    )

                    items.add(Item(1, news2))
                    //Log.d("mytag", "listDataFull после того как добавили данные ${i.CategoryName}")
                    // Log.d("mytag", "listDataFull после того как добавили данные объект ${i.`object`} java class = ${i.javaClass}")

                } */
            } else {

                if (idTypesOfWorkList.size > 0) {

                    Log.d("mytag", "ты попал на вывод листа ")

                    for (i in idTypesOfWorkList) {

                        Log.d("mytag", "i = $i ")
                        idTypeOfWork = i

                        getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(
                            getClientFromPreviousActivity()._id,
                            idTypeOfWork
                        )
                        val nameCategory = dao.getTypeOfWorkNameByTypeCategory(idTypeOfWork)

                        for (j in getClientAndEstimate) {
                            Log.d("mytag", "size = ${getClientAndEstimate.size} , ${j.Count}")
                        }
                        val news2 = News(
                            nameCategory,

                            )
                        items.add(Item(2, news2))

                        for (j in getClientAndEstimate) {

                            // Вывести привычный ресуклер
                            val clientAndEstimate =
                                ClientAndEstimate(
                                    j.CategoryName,
                                    j.Count,
                                    j._idTypeCategory,
                                    j._idTypeOfWork,
                                    j.Price,
                                    j.CategoryName,
                                    j.UnitsOfMeasurement,
                                )
                            items.add(Item(1, clientAndEstimate))

                            //Log.d("mytag", "listDataFull после того как добавили данные ${i.CategoryName}")
                            //  Log.d("mytag", "listDataFull после того как добавили данные объект ${i.javaClass .`object`} java class = ${i.javaClass}")
                        }
                    }
                } else {
                   /* getClientAndEstimate = dao.getUnionClientAndEstimateAndTypeCategory2(
                        getClientFromPreviousActivity()._id,
                        idTypeOfWork
                    )

                    val news2 = News("Russia")
                    items.add(Item(2, news2))

                    for (i in getClientAndEstimate) {


                        val clientAndEstimate =
                            ClientAndEstimate(
                                i.CategoryName,
                                i.Count,
                                i._idTypeCategory,
                                i._idTypeOfWork,
                                i.Price,
                                i.CategoryName,
                            )
                        items.add(Item(1, clientAndEstimate))
                        //Log.d("mytag", "listDataFull после того как добавили данные ${i.CategoryName}")
                        //  Log.d("mytag", "listDataFull после того как добавили данные объект ${i.javaClass .`object`} java class = ${i.javaClass}")
                    }*/
                }


            }

            recyclerView.adapter =
                TypeOfWorkRecyclerViewAdapter2(items, recyclerView.adapter, tv, getClientFromPreviousActivity()._id, btnCorrectListOfClients)



        }
        runBlocking {
            // waiting for the coroutine to finish it"s work
            job.join()
            Log.d("mytag", "Main Thread is Running")
        }

    }


    private fun filterList(isChecked: Boolean) {
        /*  if (isChecked) {

              Log.d("mytag", "Флажок выбран")
              // в recycler view удалить все строки содержащие нули
              val items = typeOfWorkRecyclerViewAdapter.getListData()
              listDataFull.clear()
              listDataFull.addAll(items)

              for (value in items) {

                  //Log.d("mytag", "items print = ${value.CategoryName}")
                  //Log.d("mytag", "items print = ${value.CategoryName}")

              }

              items.removeAll { it.Count == 0 }

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
              val listDataShort = ArrayList<ClientAndEstimate>()
              listDataShort.clear()
              listDataShort.addAll(listDataFull)
              typeOfWorkRecyclerViewAdapter.setListData(listDataShort)
          }

          typeOfWorkRecyclerViewAdapter.notifyDataSetChanged()


         */
    }

    // Kotlin
    override fun onBackPressed() {
/*
        val btnCorrectListOfClients: CheckBox = findViewById(R.id.btnCorrectListOfClients)
        if (btnCorrectListOfClients.isChecked) {
            btnCorrectListOfClients.isChecked = false
            filterList(btnCorrectListOfClients.isChecked)
        }

        val someList = typeOfWorkRecyclerViewAdapter.getListData()
        val job = GlobalScope.launch(Dispatchers.Default) {

            val dao = CategoriesDataBase.getInstance(this@TypeOfWorkActivity2).categoriesDao
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


 */
    }

    fun gettransition() {
        val intent = Intent(this, Calculation::class.java).also {
            it.putExtra("ClientEntity", getClientFromPreviousActivity())
            it.putExtra("PreviousActivity", "TypeOfWorkActivity")
        }
        startActivity(intent)
    }

    fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client


}

