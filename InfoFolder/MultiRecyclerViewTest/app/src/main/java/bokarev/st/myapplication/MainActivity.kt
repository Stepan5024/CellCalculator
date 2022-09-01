package bokarev.st.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.myapplication.adapter.TripsAdapter
import bokarev.st.myapplication.models.Ads
import bokarev.st.myapplication.models.Item
import bokarev.st.myapplication.models.News
import bokarev.st.myapplication.models.Trip

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val items: MutableList<Item> = ArrayList<Item>()
        // 0 - trip, 1- ads, 2 - news,

        // 0 - trip, 1- ads, 2 - news,
        val trip1 = Trip(R.drawable.crovatia, "Croatia", "Summer 20 days")
        items.add(Item(0, trip1))

        val ads = Ads("Christmas holiday", "Winter 20 days")
        items.add(Item(1, ads))


        val news = News("Russia", "Summer 20 days")
        items.add(Item(2, news))


        val trip2 = Trip(R.drawable.bali, "Bali", "Summer 20 days")
        items.add(Item(0, trip2))

        val ads2 = Ads("Christmas holiday", "Winter 20 days")
        items.add(Item(1, ads2))

        val trip3 = Trip(R.drawable.bora_bora, "bora-bora", "Summer 20 days")
        items.add(Item(0, trip3))

        val news2 = News("Russia", "Summer 20 days")
        items.add(Item(2, news2))

        recyclerView.adapter = TripsAdapter(items)
    }
}