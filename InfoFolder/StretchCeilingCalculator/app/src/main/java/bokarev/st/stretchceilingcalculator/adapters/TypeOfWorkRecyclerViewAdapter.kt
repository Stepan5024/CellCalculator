package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate


class TypeOfWorkRecyclerViewAdapter(private val listener: RowClickListener) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapter.MyViewHolder>() {

    private var items : MutableList<ClientAndEstimate> = arrayListOf()
    //= ArrayList<ClientAndEstаimate>()
    //val mutableList : MutableList<ClientAndEstimate> = arrayListOf()

    fun setListData(data: MutableList<ClientAndEstimate>) {
        this.items = data
    }

    fun getListData(): MutableList<ClientAndEstimate>{
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

    class MyViewHolder(view: View, private val listener: RowClickListener) : RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
        private val price = view.findViewById<TextView>(R.id.Price)!!
        private val countOfElement = view.findViewById<EditText>(R.id.CountOfElement)
        private val btnUpCounter = view.findViewById<ImageView>(R.id.btnCounterUp)!!
        private val btnDownCounter = view.findViewById<ImageView>(R.id.btnCounterDown)!!

        fun bind(data: ClientAndEstimate) {
            nameOfWork.text = data.CategoryName

            val priseStr = "${data.Price} ₽"
            price.text = priseStr
            var string = "${data.Count}"
            // добавить единицы измерения
            countOfElement.setText(string)


            btnUpCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()
                Log.d("mytag", "previousCount = $previousCount")
                if (previousCount >= 0) {

                    string = "${previousCount + 1} шт"
                    countOfElement.setText(string)

                    listener.onChangeClick(
                        ClientAndEstimate(
                            data.ClientName,
                            previousCount + 1,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.UnitsOfMeasurement,

                        ), "up", data.Price, data.Count
                    )
                }
            }
            btnDownCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()

                if (previousCount > 0) {
                    countOfElement.setText("${previousCount - 1}") // добавить единицы измерения

                    listener.onChangeClick(
                        ClientAndEstimate(
                            data.ClientName,
                            previousCount - 1,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.UnitsOfMeasurement,

                        ), "down", data.Price, data.Count
                    )
                }
            }
        }
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: ClientAndEstimate)
        fun onChangeClick(data: ClientAndEstimate, typeChange: String, priceOld:Int, countOld:Float)
        fun onItemClickListener(user: ClientAndEstimate)
    }
}