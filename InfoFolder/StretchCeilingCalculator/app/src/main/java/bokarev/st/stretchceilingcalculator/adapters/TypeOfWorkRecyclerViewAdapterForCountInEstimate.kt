package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.TypeOfWorkActivity
import bokarev.st.stretchceilingcalculator.entities.ClientAndEstimateModification
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.roundToInt
import kotlin.math.truncate


class TypeOfWorkRecyclerViewAdapterForCountInEstimate(private val listener: TypeOfWorkActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>
//RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapterForCountInEstimate.MyViewHolder>
        () {

    private var items: MutableList<ClientAndEstimateModification> = arrayListOf()


    var measureAdapter: ArrayAdapter<CharSequence>? = null


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
                        .inflate(
                            R.layout.type_of_work_recyclerview_row_without_count,
                            parent,
                            false
                        ),
                    listener,
                    measureAdapter
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
        } else if (getItemViewType(position) == 1) {
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

        class PricesEditing(
            view: View,
            private val listener: TypeOfWorkActivity,
            private val measureAdapter: ArrayAdapter<CharSequence>?,
        ) :
            RecyclerView.ViewHolder(view) {
            private val nameOfWork = view.findViewById<EditText>(R.id.NameOfWork)!!
            private val nameOfMeasure =
                view.findViewById<TextInputLayout>(R.id.choose_unit_measure)!!

            private val unitsOfMeasurement = listOf("м2", "шт.", "у.е.", "м.п.")

            //private val titleOfWork = view.findViewById<TextView>(R.id.textTypeOfWorkTitle)!!
            private val price = view.findViewById<EditText>(R.id.Price)!!


            fun bind(data: ClientAndEstimateModification) {
                

                nameOfWork.setText(data.CategoryName)

                measureAdapter?.filter?.filter(null)
                (nameOfMeasure.editText as AutoCompleteTextView).setAdapter(measureAdapter)
                (nameOfMeasure.editText as AutoCompleteTextView).setText(
                    data.UnitsOfMeasurement,
                    false
                )

                val priseStr = "${data.Price}"
                price.setText(priseStr)

                var previousPrice = data.Price

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

                        if (newPrice != previousPrice) {
                            Log.d("mytag", "new price = $newPrice")
                            if (newPrice >= 0) {

                                updateInfoFromEditTextPrice(price, newPrice, data, previousPrice)

                                previousPrice = newPrice
                            }
                        }
                        // clear focus and hide cursor from edit text
                        price.clearFocus()
                    } else {
                        listener.onItemClickListener(data)
                    }
                }

                var previousName = data.CategoryName


                nameOfWork.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        // code to execute when EditText loses focus
                        val newName = nameOfWork.text.toString()

                        if (newName != previousName) {
                            Log.d("mytag", "new namee = $newName")
                            if (newName != "") {

                                updateInfoFromEditTextName(nameOfWork, newName, data, previousName)

                                previousName = newName
                            }

                        }
                        // clear focus and hide cursor from edit text
                        nameOfWork.clearFocus()
                    } else {
                        listener.onItemClickListener(data)
                    }
                }

                var previousUnitMeasure = data.UnitsOfMeasurement

                // edit text enter key listener
                (nameOfMeasure.editText as AutoCompleteTextView).onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, itemId, _ ->

                        listener.onItemClickListener(data)
                        val newUnitMeasure = (nameOfMeasure.editText as EditText).text.toString()

                        Log.d("mytag", "new unit = $newUnitMeasure")
                        if (newUnitMeasure != "" && unitsOfMeasurement.contains(newUnitMeasure)) {

                            updateInfoFromEditTextUnitMeasure(
                                nameOfMeasure.editText as EditText,
                                newUnitMeasure,
                                data,
                                previousUnitMeasure
                            )

                            previousUnitMeasure = newUnitMeasure
                        } else {
                            nameOfMeasure.error = "Некоректные значения"
                        }
                        (nameOfMeasure.editText as EditText).clearFocus()
                    }

                (nameOfMeasure.editText as EditText).doOnTextChanged { _, _, _, _ ->
                    nameOfMeasure.error = null
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

            private fun updateInfoFromEditTextName(
                name: EditText,
                newName: String,
                data: ClientAndEstimateModification,
                previousName: String
            ) {

                name.setText(newName)
                listener.onChangeClickName(
                    ClientAndEstimateModification(
                        data.ClientName,
                        data.Count,
                        data._idTypeCategory,
                        data._idTypeOfWork,
                        data.Price,
                        newName,
                        data.NameTypeOfWork,
                        data.TypeLayout,
                        data.UnitsOfMeasurement,


                        ), previousName, "set"
                )

            }

            private fun updateInfoFromEditTextUnitMeasure(
                unitMeasure: EditText,
                newUnitMeasure: String,
                data: ClientAndEstimateModification,
                previousUnitMeasure: String
            ) {

                unitMeasure.setText(newUnitMeasure)

                listener.onChangeClickUnitMeasure(
                    ClientAndEstimateModification(
                        data.ClientName,
                        data.Count,
                        data._idTypeCategory,
                        data._idTypeOfWork,
                        data.Price,
                        data.CategoryName,
                        data.NameTypeOfWork,
                        data.TypeLayout,
                        newUnitMeasure,


                        ), previousUnitMeasure, "set"
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