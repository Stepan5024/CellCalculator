package bokarev.st.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.myapplication.R
import bokarev.st.myapplication.models.Item
import bokarev.st.myapplication.models.Ads
import bokarev.st.myapplication.models.News
import bokarev.st.myapplication.models.Trip



class TypeOfWorkRecyclerViewAdapter2(
    private var items: MutableList<Item>

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {




    //= ArrayList<ClientAndEstimate>()
    //val mutableList : MutableList<ClientAndEstimate> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // 0 - trip, 1 - ads, 2 - news
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
                return AdsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_container_ads,
                        parent,
                        false
                    )
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

    /*
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_of_work_recyclerview_row, parent, false)
        return MyViewHolder(inflater, listener)*/

    }

    override fun getItemCount(): Int {
        return items.size
    }


    interface RowClickListener {
      //  fun onDeleteUserClickListener(user: ClientAndEstimate)
       // fun onChangeClick(data: ClientAndEstimate, typeChange: String, priceOld: Int, countOld: Int)
        fun onItemClickListener(user: Item)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (getItemViewType(position) == 0) {
            val trip = items[position].`object` as Trip
            (holder as TripViewHolder).setTripData(trip)
        } else if (getItemViewType(position) == 1) {
            val ads = items[position].`object` as Ads
            (holder as AdsViewHolder).setAdsData(ads)
        } else if (getItemViewType(position) == 2) {
            val news = items[position].`object` as News
            (holder as NewsViewHolder).setNewsData(news)
        }
    }



}

internal class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageTrip: ImageView
    private val textTripTitle: TextView
    private val textTrip: TextView

    fun setTripData(trip: Trip) {
        imageTrip.setImageResource(trip.tripImage)
        textTripTitle.setText(trip.tripTitle)
        textTrip.setText(trip.trip)
    }
    fun bind() {


       /* nameOfWork.text = data.CategoryName

        val priseStr = "${data.Price} ₽"
        price.text = priseStr
        var string = "${data.Count} шт"
        countOfElement.text = string


        btnUpCounter.setOnClickListener {
            val previousCount = countOfElement.text.toString().split(" ")[0].toInt()
            Log.d("mytag", "previousCount = $previousCount")
            if (previousCount >= 0) {

                string = "${previousCount + 1} шт"
                countOfElement.text = string

                listener.onChangeClick(
                    ClientAndEstimate(
                        data.ClientName,
                        previousCount + 1,
                        data._idTypeCategory,
                        data._idTypeOfWork,
                        price.text.toString().split(" ")[0].toInt(),
                        nameOfWork.text.toString()
                    ), "up", data.Price, data.Count
                )
            }
        }
        btnDownCounter.setOnClickListener {
            val previousCount = countOfElement.text.toString().split(" ")[0].toInt()

            if (previousCount > 0) {
                countOfElement.text = "${previousCount - 1} шт"

                listener.onChangeClick(
                    ClientAndEstimate(
                        data.ClientName,
                        previousCount - 1,
                        data._idTypeCategory,
                        data._idTypeOfWork,
                        price.text.toString().split(" ")[0].toInt(),
                        nameOfWork.text.toString()
                    ), "down", data.Price, data.Count
                )
            }
        }

        */
    }

    init {
        imageTrip = itemView.findViewById(R.id.imageTrip)
        textTripTitle = itemView.findViewById(R.id.textTripTitle)
        textTrip = itemView.findViewById(R.id.textTrip)

        textTripTitle.setOnClickListener{

            Log.d("mytag", "previousCount")

        }
    }
}

internal class AdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textAdsTitle: TextView
    private val textAds: TextView
    fun setAdsData(ads: Ads) {
        textAdsTitle.setText(ads.getTypeOfWorkTitle())
        textAds.setText(ads.ads)
    }

    init {
        textAdsTitle = itemView.findViewById(R.id.textAdsTitle)
        textAds = itemView.findViewById(R.id.textAds)
    }
}

internal class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textNewsTitle: TextView
    private val textNews: TextView
    fun setNewsData(news: News) {
        textNewsTitle.setText(news.newsTitle)
        textNews.setText(news.news)
    }

    init {
        textNewsTitle = itemView.findViewById(R.id.textNewsTitle)
        textNews = itemView.findViewById(R.id.textNews)
    }
}

