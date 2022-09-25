package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.CategoriesDataBase
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TypeOfWorkRecyclerViewAdapterForPriceInEstimate(private val listener: RowClickListenerRecyclerPriceInEstimate) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapterForPriceInEstimate.PriceViewHolder>() {

    private var items: MutableList<ViewEstimate> = arrayListOf()

    fun setListData(data: MutableList<ViewEstimate>) {
        this.items = data
    }

    fun getListData(): MutableList<ViewEstimate> {
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {

        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_of_work_recyclerview_row_without_count, parent, false)
        return PriceViewHolder(inflater, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])

    }

    class PriceViewHolder(view: View, private val listener: RowClickListenerRecyclerPriceInEstimate) :
        RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
        private val nameOfMeansure = view.findViewById<TextView>(R.id.tvMensure)!!
        private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
        private val price = view.findViewById<EditText>(R.id.Price)!!


        @OptIn(DelicateCoroutinesApi::class)
        fun bind(data: ViewEstimate) {


            // if (data.TypeLayout == 1) {
            // layoutType0.isInvisible = true
            /*val params: ViewGroup.LayoutParams = layoutType0.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = 0
            layoutType0.layoutParams = params*/

            // layoutType0.layoutParams.height = 0


            GlobalScope.launch(Dispatchers.Default) {
                val dao = CategoriesDataBase.getInstance(itemView.context).categoriesDao

                titleOfWork.text = dao.getTypeOfWorkNameByTypeCategory(data._idTypeOfWork)
            }
            nameOfWork.text = data.CategoryName
            nameOfMeansure.text = data.UnitsOfMeasurement

            val priseStr = "${data.Price}"
            price.setText(priseStr)

            var previousPrice = data.Price

            // edit text enter key listener
            price.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    // if the event is a key down event on the enter button
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER
                    ) {

                        val newPrice = price.text.toString().split(" ")[0].toInt()

                        Log.d("mytag", "new price = $newPrice")
                        if (newPrice >= 0) {

                            val string = "$newPrice"
                            price.setText(string)

                            listener.onChangeClickPrice(
                                ViewEstimate(
                                    data._id,
                                    data._idTypeOfWork,
                                    newPrice,
                                    data.CategoryName,
                                    data.UnitsOfMeasurement,


                                    ), previousPrice, "set"
                            )

                            previousPrice = newPrice
                        }
                        // clear focus and hide cursor from edit text
                        price.clearFocus()

                        return true
                    }
                    return false
                }
            })

        }

    }

    interface RowClickListenerRecyclerPriceInEstimate {
        fun onDeletePriceClickListener(user: ViewEstimate)
        fun onChangeClickPrice(
            data: ViewEstimate,
            oldPrice: Int,
            typeChange: String,

            )

        fun onItemClickListener(user: ViewEstimate)
    }
}