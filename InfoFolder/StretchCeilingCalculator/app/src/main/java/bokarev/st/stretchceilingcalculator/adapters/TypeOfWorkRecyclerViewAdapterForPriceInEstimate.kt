package bokarev.st.stretchceilingcalculator.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.CategoriesDataBase
import bokarev.st.stretchceilingcalculator.R
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class TypeOfWorkRecyclerViewAdapterForPriceInEstimate(private val listener: RowClickListenerRecyclerPriceInEstimate) :
    RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapterForPriceInEstimate.PriceViewHolder>() {

    private var items: MutableList<ViewEstimate> = arrayListOf()

    private val unitsOfMeasurement = listOf("м2", "шт.", "у.е.", "м.п.")

    var measureAdapter: ArrayAdapter<CharSequence>? = null

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

    inner class PriceViewHolder(
        view: View,
        private val listener: RowClickListenerRecyclerPriceInEstimate
    ) :
        RecyclerView.ViewHolder(view) {

        private val nameOfWork = view.findViewById<EditText>(R.id.NameOfWork)!!
        private val nameOfMeasure = view.findViewById<TextInputLayout>(R.id.choose_unit_measure)!!
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
            nameOfWork.setText(data.CategoryName)
            measureAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            (nameOfMeasure.editText as AutoCompleteTextView).setAdapter(measureAdapter)
            (nameOfMeasure.editText as AutoCompleteTextView).setText(data.UnitsOfMeasurement)

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
            price.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
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
                }
            }

            var previousName = data.CategoryName


            nameOfWork.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
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

                }
            }

            var previousUnitMeasure = data.UnitsOfMeasurement

            // edit text enter key listener
            (nameOfMeasure.editText as AutoCompleteTextView).onItemClickListener =
                AdapterView.OnItemClickListener { _, _, itemId, _ ->
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

    }

    private fun updateInfoFromEditTextPrice(
        price: EditText,
        newPrice: Int,
        data: ViewEstimate,
        previousPrice: Int
    ) {
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

    }

    private fun updateInfoFromEditTextName(
        name: EditText,
        newName: String,
        data: ViewEstimate,
        previousName: String
    ) {

        name.setText(newName)

        listener.onChangeClickName(
            ViewEstimate(
                data._id,
                data._idTypeOfWork,
                data.Price,
                newName,
                data.UnitsOfMeasurement,


                ), previousName, "set"
        )

    }

    private fun updateInfoFromEditTextUnitMeasure(
        unitMeasure: EditText,
        newUnitMeasure: String,
        data: ViewEstimate,
        previousUnitMeasure: String
    ) {

        unitMeasure.setText(newUnitMeasure)

        listener.onChangeClickUnitMeasure(
            ViewEstimate(
                data._id,
                data._idTypeOfWork,
                data.Price,
                data.CategoryName,
                newUnitMeasure,


                ), previousUnitMeasure, "set"
        )

    }

    interface RowClickListenerRecyclerPriceInEstimate {

        fun onDeletePriceClickListener(user: ViewEstimate)
        fun onChangeClickPrice(
            data: ViewEstimate,
            oldPrice: Int,
            typeChange: String,

            )

        fun onItemClickListener(user: ViewEstimate) {

        }

        fun onChangeClickName(
            data: ViewEstimate,
            oldName: String,
            typeChange: String,
        )

        fun onChangeClickUnitMeasure(
            data: ViewEstimate,
            oldUnitMeasure: String,
            typeChange: String,
        )
    }
}



