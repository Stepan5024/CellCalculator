package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.ClientAndEstimateMidifation


class TypeOfWorkRecyclerViewAdapter3(private val listener: RowClickListener) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapter3.MyViewHolder>() {

    private var items: MutableList<ClientAndEstimateMidifation> = arrayListOf()
    //= ArrayList<ClientAndEstimate>()
    //val mutableList : MutableList<ClientAndEstimate> = arrayListOf()

    fun setListData(data: MutableList<ClientAndEstimateMidifation>) {
        this.items = data
    }

    fun getListData(): MutableList<ClientAndEstimateMidifation> {
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

    class MyViewHolder(view: View, private val listener: RowClickListener) :
        RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
        private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
        private val price = view.findViewById<TextView>(R.id.Price)!!
        private val countOfElement = view.findViewById<TextView>(R.id.CountOfElement)
        private val btnUpCounter = view.findViewById<ImageView>(R.id.btnCounterUp)!!
        private val btnDownCounter = view.findViewById<ImageView>(R.id.btnCounterDown)!!
        private val layoutType0 = view.findViewById<LinearLayout>(R.id.layoutType0)!!
        private val layoutType1 = view.findViewById<LinearLayout>(R.id.layoutType1)!!

        fun bind(data: ClientAndEstimateMidifation) {

           // if (data.TypeLayout == 1) {
               // layoutType0.isInvisible = true
                /*val params: ViewGroup.LayoutParams = layoutType0.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = 0
                layoutType0.layoutParams = params*/

               // layoutType0.layoutParams.height = 0

                titleOfWork.text = data.NameTypeOfWork

                nameOfWork.text = data.CategoryName

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
                            ClientAndEstimateMidifation(
                                data.ClientName,
                                previousCount + 1,
                                data._idTypeCategory,
                                data._idTypeOfWork,
                                price.text.toString().split(" ")[0].toInt(),
                                nameOfWork.text.toString(),
                                data.NameTypeOfWork,
                                1,
                            ), "up", data.Price, data.Count
                        )
                    }
                }

                btnDownCounter.setOnClickListener {
                    val previousCount = countOfElement.text.toString().split(" ")[0].toInt()

                    if (previousCount > 0) {
                        countOfElement.text = "${previousCount - 1} шт"

                        listener.onChangeClick(
                            ClientAndEstimateMidifation(
                                data.ClientName,
                                previousCount - 1,
                                data._idTypeCategory,
                                data._idTypeOfWork,
                                price.text.toString().split(" ")[0].toInt(),
                                nameOfWork.text.toString(),
                                data.NameTypeOfWork,
                                1,

                            ), "down", data.Price, data.Count
                        )
                    }
                }
            /* }else if (data.TypeLayout == 0){
                  //layoutType1.isInvisible = true

                  val params: ViewGroup.LayoutParams = layoutType1.layoutParams
                  params.width = ViewGroup.LayoutParams.MATCH_PARENT
                  params.height = 0
                  layoutType1.layoutParams = params
                  layoutType1.layoutParams.height = 0
                  titleOfWork.text = data.NameTypeOfWork
              }*/
        }

    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: ClientAndEstimateMidifation)
        fun onChangeClick(
            data: ClientAndEstimateMidifation,
            typeChange: String,
            priceOld: Int,
            countOld: Int
        )

        fun onItemClickListener(user: ClientAndEstimateMidifation)
    }
}