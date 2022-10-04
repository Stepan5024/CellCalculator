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
import bokarev.st.stretchceilingcalculator.TypeOfWorkActivity
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import kotlin.math.roundToInt
import kotlin.math.truncate


class TypeOfWorkRecyclerViewAdapterForCountInEstimate(private val listener: TypeOfWorkActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>
//RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapterForCountInEstimate.MyViewHolder>
        () {

    private var items: MutableList<ClientAndEstimateModification> = arrayListOf()

    fun setListData(data: MutableList<ClientAndEstimateModification>) {
        this.items = data
    }

    fun getListData(): MutableList<ClientAndEstimateModification> {
        return items
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].TypeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            3 -> {
                MyViewHolder.PricesEditing(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.type_of_work_recyclerview_row_without_count, parent, false), listener
                )
            }
            2 -> {
                MyViewHolder.TitleOfTypeOfWork(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.items_title_category_name, parent, false), listener
                )
            }
            else -> {
                MyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.type_of_work_recyclerview_row, parent, false), listener
                )

            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    /*  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

          holder.itemView.setOnClickListener {
              listener.onItemClickListener(items[position])
          }
          holder.bind(items[position])

      }*/
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 3) {
            holder.itemView.setOnClickListener {
                listener.onItemClickListener(items[position])
            }
            (holder as MyViewHolder.PricesEditing).bind(items[position])
        }else if (getItemViewType(position) == 1) {
            holder.itemView.setOnClickListener {
                listener.onItemClickListener(items[position])
            }
            (holder as MyViewHolder).bind(items[position])
        } else {
            holder.itemView.setOnClickListener {
                listener.onItemClickListener(items[position])
            }
            (holder as MyViewHolder.TitleOfTypeOfWork).bind(items[position])
        }


    }

    class MyViewHolder(view: View, private val listener: TypeOfWorkActivity) :
        RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
        private val nameOfMeasure = view.findViewById<TextView>(R.id.tvMensure)!!
       // private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
        private val price = view.findViewById<TextView>(R.id.Price)!!
        private val countOfElement = view.findViewById<EditText>(R.id.CountOfElement)
        private val btnUpCounter = view.findViewById<ImageView>(R.id.btnCounterUp)!!
        private val btnDownCounter = view.findViewById<ImageView>(R.id.btnCounterDown)!!


        fun bind(data: ClientAndEstimateModification) {


           // titleOfWork.text = data.NameTypeOfWork

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


                        var previousCount =
                            countOfElement.text.toString().split(" ")[0].toFloat()

                        Log.d("mytag", "previousCount = $previousCount")
                        if (previousCount >= 0f) {

                            if (data.UnitsOfMeasurement == "шт." || data.UnitsOfMeasurement == "у.е.") {
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
                                    data.TypeLayout,
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
                            data.TypeLayout,
                            data.UnitsOfMeasurement,
                        ),
                        "up", data.Price, data.Count, 0F,
                    )
                }
            }

            btnDownCounter.setOnClickListener {
                val previousCount = countOfElement.text.toString().split(" ")[0].toFloat()

                if (previousCount > 0F && (previousCount - 1F >= 0)) {
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
                            data.TypeLayout,
                            data.UnitsOfMeasurement,
                        ),
                        "down", data.Price, data.Count, 0F,
                    )
                }
            }



        }
        class TitleOfTypeOfWork(view: View, private val listener: TypeOfWorkActivity) :
            RecyclerView.ViewHolder(view) {

            private val textTitle = view.findViewById<TextView>(R.id.textTitle)!!
           // private val description = view.findViewById<TextView>(R.id.description)!!


            fun bind(data: ClientAndEstimateModification) {

                textTitle.text = data.NameTypeOfWork

             //   description.text = data._idTypeOfWork.toString()

            }

        }

        class PricesEditing(view: View, private val listener: TypeOfWorkActivity) :
            RecyclerView.ViewHolder(view) {
            private val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)!!
            private val nameOfMeansure = view.findViewById<TextView>(R.id.tvMensure)!!
            //private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
            private val price = view.findViewById<EditText>(R.id.Price)!!



            fun bind(data: ClientAndEstimateModification) {

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

                                updateInfoFromEditTextPrice(price, newPrice, data, previousPrice)

                                previousPrice = newPrice
                            }
                            // clear focus and hide cursor from edit text
                            price.clearFocus()

                            return true
                        }
                        return false
                    }
                })
                price.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        // code to execute when EditText loses focus
                        val newPrice = price.text.toString().split(" ")[0].toInt()

                        Log.d("mytag", "new price = $newPrice")
                        if (newPrice >= 0) {

                            updateInfoFromEditTextPrice(price, newPrice, data, previousPrice)

                            previousPrice = newPrice
                        }
                        // clear focus and hide cursor from edit text
                        price.clearFocus()
                    }
                }

            }
            private fun updateInfoFromEditTextPrice(
                price: EditText,
                newPrice: Int,
                data: ClientAndEstimateModification,
                previousPrice: Int
            ) {
                val string = "$newPrice"
                price.setText(string)

                listener.onChangeClickPrice(
                    ClientAndEstimateModification(
                        data.ClientName,
                        data.Count,
                        data._idTypeCategory,
                        data._idTypeOfWork,
                        newPrice,
                        data.CategoryName,
                        data.NameTypeOfWork,
                        data.TypeLayout,
                        data.UnitsOfMeasurement,


                        ), previousPrice, "set"
                )

            }

        }

        interface RowClickListenerRecyclerCountInEstimate {

            fun onChangeClickPrice(
                data: ClientAndEstimateModification,
                typeChange: String,
                priceOld: Int,
                countOld: Float,
                deltaEdit: Float,
            )

            fun onItemClickListener(user: ClientAndEstimateModification)
        }
    }

}