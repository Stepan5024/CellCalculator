package bokarev.st.stretchceilingcalculator.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.CategoriesDataBase
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.Item
import bokarev.st.stretchceilingcalculator.models.News
import bokarev.st.stretchceilingcalculator.models.Trip
import kotlinx.coroutines.*


class TypeOfWorkRecyclerViewAdapter2(
    private var items: MutableList<Item>,
    private val recycler: RecyclerView.Adapter<RecyclerView.ViewHolder>?,
    private val tv: TextView,
    val clientName: Int,
    private val btnCorrectListOfClients: CheckBox,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun getListData(): MutableList<Item> {
        return items
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 0 - trip, 1 - count, 2 - news
        when (viewType) {
            0 -> {
                return TripViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_container_trip,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                return TypeOfCategoryViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.type_of_work_recyclerview_row,
                        parent,
                        false
                    ), recycler, tv, items, clientName, btnCorrectListOfClients
                )
            }
            else -> {
                return NewsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_container_news,
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val trip = items[position].`object` as Trip
            (holder as TripViewHolder).setTripData(trip)
        } else if (getItemViewType(position) == 1) {
            val typeOfWorkForRecyclerView = items[position].`object` as ClientAndEstimate
            (holder as TypeOfCategoryViewHolder).setTypeOfCategoryData(typeOfWorkForRecyclerView)
        } else if (getItemViewType(position) == 2) {
            val news = items[position].`object` as News
            (holder as NewsViewHolder).setNewsData(news)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    internal class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageTrip: ImageView
        private val textTripTitle: TextView
        private val textTrip: TextView
        fun setTripData(trip: Trip) {
            imageTrip.setImageResource(trip.tripImage)
            textTripTitle.text = trip.tripTitle
            textTrip.text = trip.trip
        }

        init {
            imageTrip = itemView.findViewById(R.id.imageTrip)
            textTripTitle = itemView.findViewById(R.id.textTripTitle)
            textTrip = itemView.findViewById(R.id.textTrip)
            textTripTitle.setOnClickListener { Log.d("mytag", "previousCount") }
        }
    }

    internal class TypeOfCategoryViewHolder(
        itemView: View,
        private var recycler: RecyclerView.Adapter<RecyclerView.ViewHolder>?,
        private val tv: TextView,
        private var items: MutableList<Item>,
        val clientName: Int,
        private val btnCorrectListOfClients: CheckBox,

        ) : RecyclerView.ViewHolder(itemView) {

        private val nameOfWork: TextView
        private val price: TextView
        private val countOfElement: TextView
        private val btnUpCounter: ImageView
        private val btnDownCounter: ImageView


        @SuppressLint("SetTextI18n")
        fun setTypeOfCategoryData(data: ClientAndEstimate) {
            nameOfWork.text = data.CategoryName

            val priseStr = "${data.Price} ₽"
            price.text = priseStr
            var string = "${data.Count} шт"
            countOfElement.text = string


            btnUpCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()
                Log.d("mytag", "previousCount = $previousCount")
                if (previousCount >= 0) {

                    string = "${previousCount + 1} шт"
                    countOfElement.text = string

                    onChangeClick(
                        ClientAndEstimate(
                            data.ClientName,
                            previousCount + 1,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.UnitsOfMeasurement,

                        ), "up", data.Price, data.Count, tv, items, clientName, btnCorrectListOfClients
                    )


                }
            }
            btnDownCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()

                if (previousCount > 0) {
                    countOfElement.text = "${previousCount - 1} шт"

                    onChangeClick(
                        ClientAndEstimate(
                            data.ClientName,
                            previousCount - 1,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.UnitsOfMeasurement,

                        ), "down", data.Price, data.Count, tv, items, clientName, btnCorrectListOfClients
                    )
                }
            }
        }

        init {

            nameOfWork = itemView.findViewById(R.id.NameOfWork)!!
            price = itemView.findViewById(R.id.Price)!!
            countOfElement = itemView.findViewById(R.id.CountOfElement)!!
            btnUpCounter = itemView.findViewById(R.id.btnCounterUp)!!
            btnDownCounter = itemView.findViewById(R.id.btnCounterDown)!!

        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun onChangeClick(
            data: ClientAndEstimate,
            typeChange: String,
            priceOld: Int,
            countOld: Float,
            tv: TextView,
            items: MutableList<Item>,
            clientId: Int,
            btnCorrectListOfClients: CheckBox
        ) {

            val oldSum = tv.text.split(" ")[1].toInt()
            var newSum = oldSum
            if (typeChange == "down") newSum = oldSum - data.Price
            else if (typeChange == "up") newSum = oldSum + data.Price

            val string = "сумма: $newSum ₽"
            tv.text = string

            val clientsList: MutableList<ClientAndEstimate> = arrayListOf()

            val itemsCopy: MutableList<Item> = arrayListOf()
            itemsCopy.clear()
            // фильтрация
            for (i in items) {
                Log.d("mytag", "VOVA items class = ${i.javaClass}, i object = ${i.`object`.javaClass}")
                if (i.`object`.javaClass == ClientAndEstimate::class.java) {
                    Log.d("mytag", "Это класс Клиент и Смета")
                    val obj = i.`object` as ClientAndEstimate


                    if (obj.CategoryName == data.CategoryName && obj._idTypeOfWork == data._idTypeOfWork && obj.ClientName == data.ClientName
                        && obj.Count == countOld && obj.Price == priceOld
                    ) {
                        Log.d("mytag", "важно !!! зашли в дубль")
                        clientsList.add(
                            ClientAndEstimate(
                                obj.ClientName, obj.Count, obj._idTypeCategory,
                                obj._idTypeOfWork, obj.Price, obj.CategoryName, obj.UnitsOfMeasurement,
                            )
                        )
                        itemsCopy.add(
                            Item(
                                1, ClientAndEstimate(
                                    obj.ClientName, obj.Count, obj._idTypeCategory,
                                    obj._idTypeOfWork, obj.Price, obj.CategoryName, obj.UnitsOfMeasurement,
                                )
                            )
                        )
                    } else {
                        clientsList.add(obj)
                        itemsCopy.add(Item(1, obj))
                    }

                } else {
                    itemsCopy.add(i)
                }

            }
            items.clear()
            items.addAll(itemsCopy)

            recycler = TypeOfWorkRecyclerViewAdapter2(itemsCopy, recycler, tv, clientName, btnCorrectListOfClients)


            val job = GlobalScope.launch(Dispatchers.Default) {

                val dao = CategoriesDataBase.getInstance(itemView.context).categoriesDao
                for (i in itemsCopy) {

                    if (i.`object`.javaClass == ClientAndEstimate::class.java) {
                        Log.d("mytag", "запись в БД = ${(i.`object`)}")
                        dao.updateCountStrokesEstimateByClient(
                           clientId,
                            //i._idTypeCategory
                            (i.`object` as ClientAndEstimate)._idTypeCategory,
                            //i.Count
                            (i.`object` as ClientAndEstimate).Count
                        )


                    }

                }

            }
            runBlocking {
                // waiting for the coroutine to finish it"s work
                job.join()

                Log.d("mytag", "Main Thread is Running")
            }

        }


    }

    internal class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNewsTitle: TextView

        //private val textNews: TextView
        fun setNewsData(news: News) {
            textNewsTitle.text = news.newsTitle
            // textNews.text = news.news
        }

        init {
            textNewsTitle = itemView.findViewById(R.id.textNewsTitle)
            //textNews = itemView.findViewById(R.id.textNews)
        }
    }
}
