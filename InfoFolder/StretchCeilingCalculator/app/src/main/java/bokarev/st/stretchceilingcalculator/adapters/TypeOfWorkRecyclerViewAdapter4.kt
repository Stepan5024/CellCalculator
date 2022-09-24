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


class TypeOfWorkRecyclerViewAdapter4(private val listener: RowClickListener) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapter4.MyViewHolder>() {

    private var items: MutableList<ViewEstimate> = arrayListOf()
    //= ArrayList<ClientAndEstimate>()
    //val mutableList : MutableList<ClientAndEstimate> = arrayListOf()

    fun setListData(data: MutableList<ViewEstimate>) {
        this.items = data
    }

    fun getListData(): MutableList<ViewEstimate> {
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_of_work_recyclerview_row_without_count, parent, false)
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
        private val nameOfMensure = view.findViewById<TextView>(R.id.tvMensure)!!
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

                titleOfWork.text =  dao.getTypeOfWorkNameByTypeCategory(data._idTypeOfWork)
            }
            nameOfWork.text = data.CategoryName
            nameOfMensure.text = data.UnitsOfMeasurement

            val priseStr = "${data.Price}"
            price.setText(priseStr)

            val previousNumber =price.text.toString().toInt()

            // edit text enter key listener
            price.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    // if the event is a key down event on the enter button
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER
                    ) {


                        val previousCount = price.text.toString().split(" ")[0].toInt()

                        Log.d("mytag", "previousCount = $previousCount")
                        if (previousCount >= 0) {

                           val string = "$previousCount"
                            price.setText(string)

                            listener.onChangeClick(
                                ViewEstimate(
                                    data._id,
                                    data._idTypeOfWork,
                                    price.text.toString().toInt(),
                                    data.CategoryName,
                                    data.UnitsOfMeasurement,


                                ), previousNumber, "set"
                            )



                        }
                        // clear focus and hide cursor from edit text
                        price.clearFocus()
                        price.isCursorVisible = false

                        return true
                    }
                    return false
                }
            })

        }

    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: ViewEstimate)
        fun onChangeClick(
            data: ViewEstimate,
            oldPrice: Int,
            typeChange: String,

            )

        fun onItemClickListener(user: ViewEstimate)
    }
}