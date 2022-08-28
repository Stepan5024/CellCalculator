package bokarev.st.stretchceilingcalculator

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
            countOfElement.text = data.Count.toString()


            btnUpCounter.setOnClickListener {
                val pred = countOfElement.text.toString().split(" ")

                    countOfElement.text = "${pred.get(0).toInt() + 1} шт"

                    listener.onChangeClick(
                        TypeOfWorkDataClass(
                            pred.get(0).toInt() + 1,
                            price.text.toString().split(" ").get(0).toInt(),
                            nameOfWork.text.toString()
                        ), "up"
                    )

            }
            btnDownCounter.setOnClickListener {
                val pred = countOfElement.text.toString().split(" ")

                if (pred.get(0).toInt() - 1 < 0) countOfElement.text = "0 шт"
                else {
                    countOfElement.text = "${pred.get(0).toInt() - 1} шт"

                    listener.onChangeClick(
                        TypeOfWorkDataClass(
                            pred.get(0).toInt() - 1,
                            price.text.toString().split(" ").get(0).toInt(),
                            nameOfWork.text.toString()
                        ), "down"
                    )
                }
            }
        }
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: ClientAndEstimate)
        fun onChangeClick(data: TypeOfWorkDataClass, typeChange: String)
        fun onItemClickListener(user: ClientAndEstimate)
    }
}