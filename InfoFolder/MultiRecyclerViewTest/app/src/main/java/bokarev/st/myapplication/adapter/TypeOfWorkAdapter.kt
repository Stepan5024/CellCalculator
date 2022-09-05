package bokarev.st.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.myapplication.R
import bokarev.st.myapplication.models.Ads
import bokarev.st.myapplication.models.Item
import bokarev.st.myapplication.models.News
import bokarev.st.myapplication.models.Trip


class TypeOfWorkAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 0 - trip, 1 - ads, 2 - news
        if (viewType == 0) {
            return TripViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_container_trip,
                    parent,
                    false
                )
            )
        } else if (viewType == 1) {
            return AdsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_container_ads,
                    parent,
                    false
                )
            )
        } else  {
            return NewsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_container_news,
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val trip = items[position].getObject() as Trip
            (holder as TripViewHolder).setTripData(trip)
        } else if (getItemViewType(position) == 1) {
            val ads = items[position].getObject() as Ads
            (holder as AdsViewHolder).setAdsData(ads)
        } else if (getItemViewType(position) == 2) {
            val news = items[position].getObject() as News
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

    internal class AdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textAdsTitle: TextView
        private val textAds: TextView
        fun setAdsData(ads: Ads) {
            textAdsTitle.text = ads.typeOfWorkTitle
            textAds.text = ads.ads
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
            textNewsTitle.text = news.newsTitle
            textNews.text = news.news
        }

        init {
            textNewsTitle = itemView.findViewById(R.id.textNewsTitle)
            textNews = itemView.findViewById(R.id.textNews)
        }
    }
}
