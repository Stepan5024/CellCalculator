package bokarev.st.stretchceilingcalculator

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate


class TypeOfWorkRecyclerViewAdapter(val listener: RowClickListener) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapter.MyViewHolder>() {

    var items = ArrayList<ClientAndEstimate>()

    fun setListData(data: ArrayList<ClientAndEstimate>) {
        this.items = data
    }

    fun getListData(): ArrayList<ClientAndEstimate>{
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_of_work_recyclerview_row, parent, false)
        return MyViewHolder(inflater, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])

    }

    class MyViewHolder(view: View, val listener: RowClickListener) : RecyclerView.ViewHolder(view) {

        val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)
        val price = view.findViewById<TextView>(R.id.Price)
        val countOfElement = view.findViewById<TextView>(R.id.CountOfElement)
        val btnUpCounter = view.findViewById<ImageView>(R.id.btnCounterUp)
        val btnDownCounter = view.findViewById<ImageView>(R.id.btnCounterDown)

        fun bind(data: ClientAndEstimate) {
            nameOfWork.text = data.CategoryName

            val priseStr = "${data.Price} ₽"
            price.text = priseStr
            countOfElement.text = "${data.Count} шт"


            btnUpCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toInt()
                Log.d("mytag", "previousCount = $previousCount")
                if (previousCount >= 0) {
                    countOfElement.text = "${previousCount + 1} шт"

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
        }
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: ClientAndEstimate)
        fun onChangeClick(data: ClientAndEstimate, typeChange: String, priceOld:Int, countOld:Int)
        fun onItemClickListener(user: ClientAndEstimate)
    }
}