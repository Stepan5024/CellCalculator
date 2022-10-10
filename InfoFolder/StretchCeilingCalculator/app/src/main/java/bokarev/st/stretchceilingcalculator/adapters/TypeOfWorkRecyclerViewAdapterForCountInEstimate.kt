package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import kotlin.math.roundToInt
import kotlin.math.truncate


class TypeOfWorkRecyclerViewAdapterForCountInEstimate(private val listener: RowClickListenerRecyclerCountInEstimate) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapterForCountInEstimate.MyViewHolder>() {

    private var items: MutableList<ClientAndEstimateModification> = arrayListOf()


    fun setListData(data: MutableList<ClientAndEstimateModification>) {
        this.items = data
    }

    fun getListData(): MutableList<ClientAndEstimateModification> {
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

    class MyViewHolder(view: View, private val listener: RowClickListenerRecyclerCountInEstimate) :
        RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
        private val nameOfMeasure = view.findViewById<TextView>(R.id.tvMensure)!!
        private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
        private val price = view.findViewById<TextView>(R.id.Price)!!
        private val countOfElement = view.findViewById<EditText>(R.id.CountOfElement)
        private val btnUpCounter = view.findViewById<ImageView>(R.id.btnCounterUp)!!
        private val btnDownCounter = view.findViewById<ImageView>(R.id.btnCounterDown)!!


        fun bind(data: ClientAndEstimateModification) {

            // if (data.TypeLayout == 1) {
            // layoutType0.isInvisible = true
            /*val params: ViewGroup.LayoutParams = layoutType0.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = 0
            layoutType0.layoutParams = params*/

            // layoutType0.layoutParams.height = 0

            titleOfWork.text = data.NameTypeOfWork

            nameOfWork.text = data.CategoryName
            nameOfMeasure.text = data.UnitsOfMeasurement

            val priseStr = "${data.Price} ₽"
            price.text = priseStr
            var string = "${data.Count}"
            countOfElement.setText(string)
            var previousNumber = data.Count

            // edit text enter key listener
            countOfElement.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    // if the event is a key down event on the enter button
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER
                    ) {


                        var previousCount = countOfElement.text.toString().split(" ")[0].toFloat()

                        Log.d("mytag", "previousCount = $previousCount")
                        if (previousCount >= 0f) {

                            if(data.UnitsOfMeasurement == "шт." || data.UnitsOfMeasurement == "у.е."){
                                //previousCount = previousCount.toInt().toFloat()
                                previousCount = truncate(previousCount)
                            }

                            string = "$previousCount"
                            countOfElement.setText(string)

                            listener.onChangeClick(
                                ClientAndEstimateModification(
                                    data.ClientName,
                                    previousCount,
                                    data._idTypeCategory,
                                    data._idTypeOfWork,
                                    price.text.toString().split(" ")[0].toInt(),
                                    nameOfWork.text.toString(),
                                    data.NameTypeOfWork,
                                    1,
                                    data.UnitsOfMeasurement,
                                ), "set", data.Price, data.Count, previousCount - previousNumber
                            )

                            previousNumber = previousCount

                        }

                        // clear focus from edit text
                        countOfElement.clearFocus()


                        return true
                    }
                    return false
                }
            })

            btnUpCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()
                Log.d("mytag", "previousCount = $previousCount")
                if (previousCount >= 0) {

                    string = "${previousCount + 1}"
                    countOfElement.setText(string)

                    listener.onChangeClick(
                        ClientAndEstimateModification(
                            data.ClientName,
                            previousCount + 1,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.NameTypeOfWork,
                            1,
                            data.UnitsOfMeasurement,
                        ), "up", data.Price, data.Count, 0F,
                    )
                }
            }

            btnDownCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()

                if (previousCount > 0F && (previousCount - 1F >=0)) {
                    countOfElement.setText("${previousCount - 1}")



                    listener.onChangeClick(
                        ClientAndEstimateModification(
                            data.ClientName,
                            ((previousCount - 1) * 100f).roundToInt() / 100f,
                            data._idTypeCategory,
                            data._idTypeOfWork,
                            price.text.toString().split(" ")[0].toInt(),
                            nameOfWork.text.toString(),
                            data.NameTypeOfWork,
                            1,
                            data.UnitsOfMeasurement,
                        ), "down", data.Price, data.Count, 0F,
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

    interface RowClickListenerRecyclerCountInEstimate {

        fun onChangeClick(
            data: ClientAndEstimateModification,
            typeChange: String,
            priceOld: Int,
            countOld: Float,
            deltaEdit: Float,
        )

        fun onItemClickListener(user: ClientAndEstimateModification)
    }
}