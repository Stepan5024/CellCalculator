package bokarev.st.stretchceilingcalculator

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.adapters.TypeOfWorkRecyclerViewAdapterForPriceInEstimate
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.ViewEstimate
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AddWorkDialogFragment(private val recyclerView: RecyclerView) : DialogFragment() {

    private lateinit var dao: TypeCategoryDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)

        dao = CategoriesDataBase.getInstance(requireContext()).categoriesDao

        val chooseTypeOfWork = view.findViewById<TextInputLayout>(R.id.choose_type_of_work)
        val chooseCategory = view.findViewById<TextInputLayout>(R.id.choose_category)
        val chooseUnitMeasure = view.findViewById<TextInputLayout>(R.id.choose_unit_measure)

        val nameInputLayout = view.findViewById<TextInputLayout>(R.id.layout_name)
        val priceInputLayout = view.findViewById<TextInputLayout>(R.id.layout_price)

        val saveTv = view.findViewById<TextView>(R.id.tv_save)
        val cancelTv = view.findViewById<TextView>(R.id.tv_cancel)

        val unitMeasureList = listOf("м2", "шт.", "у. е.", "м. п.")
        val typeOfWorkList = listOf("Система", "Освещение", "Доп. работы", "Материалы")
        var categoryList = mutableListOf<String>()

        val adapterTypeOfFork = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            typeOfWorkList
        )
        (chooseTypeOfWork.editText as? AutoCompleteTextView)?.setAdapter(adapterTypeOfFork)

        (chooseTypeOfWork.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, itemId, _ ->
                var typeOfWorkIdStart = 0
                var typeOfWorkIdEnd = 0
                when (itemId) {
                    0 -> {
                        typeOfWorkIdStart = 1
                        typeOfWorkIdEnd = 8
                    }
                    1 -> {
                        typeOfWorkIdStart = 9
                        typeOfWorkIdEnd = 9
                    }
                    2 -> {
                        typeOfWorkIdStart = 10
                        typeOfWorkIdEnd = 10
                    }
                    3 -> {
                        typeOfWorkIdStart = 11
                        typeOfWorkIdEnd = 20
                    }
                }


                lifecycleScope.launch {
                    categoryList = mutableListOf()
                    for (i in typeOfWorkIdStart..typeOfWorkIdEnd) {
                        categoryList.add(dao.getTypeOfWorkNameByTypeCategory(i))
                    }

                    val adapterCategories = ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        categoryList
                    )
                    (chooseCategory.editText as? AutoCompleteTextView)?.setAdapter(adapterCategories)
                }
            }

        val adapterUnitMeasure = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            unitMeasureList
        )
        (chooseUnitMeasure.editText as? AutoCompleteTextView)?.setAdapter(adapterUnitMeasure)

        cancelTv.setOnClickListener {
            dialog?.dismiss()
        }

        saveTv.setOnClickListener {
            var isSaveEnabled = true
            if (nameInputLayout.editText?.text?.isBlank() == true) {
                nameInputLayout.error = "Ведите название"
                isSaveEnabled = false
            }

            if (priceInputLayout.editText?.text?.isBlank() == true) {
                priceInputLayout.error = "Ведите цену"
                isSaveEnabled = false
            }

            if (chooseTypeOfWork.editText?.text?.isBlank() == true
                || !typeOfWorkList.contains(chooseTypeOfWork.editText?.text.toString())
            ) {
                chooseTypeOfWork.error = "Введите корректный тип работ"
                isSaveEnabled = false
            }

            if (chooseCategory.editText?.text?.isBlank() == true
                || !categoryList.contains(chooseCategory.editText?.text.toString())
            ) {
                chooseCategory.error = "Введите корректную категорию"
                isSaveEnabled = false
            }

            if (chooseUnitMeasure.editText?.text?.isBlank() == true
                || !unitMeasureList.contains(chooseUnitMeasure.editText?.text.toString())
            ) {
                chooseUnitMeasure.error = "Введите корректные ед. измерения"
                isSaveEnabled = false
            }

            if (isSaveEnabled) {
                var allCategories: MutableList<ViewEstimate>
                var typeOfWorkId = -1
                lifecycleScope.launch {
                    for (i in 1..20) {
                        if (dao.getTypeOfWorkNameByTypeCategory(i) == chooseCategory.editText?.text.toString()) {
                            typeOfWorkId = i
                        }
                    }
                    dao.insertTypeCategory(
                        TypeCategory(
                            CategoryName = nameInputLayout.editText?.text.toString(),
                            UnitsOfMeasurement = chooseUnitMeasure.editText?.text.toString(),
                            Price = priceInputLayout.editText?.text.toString().toInt(),
                            _idTypeOfWork = typeOfWorkId
                        )
                    )
                    allCategories = dao.getEstimateByList(listOf(typeOfWorkId))


                    (recyclerView.adapter as TypeOfWorkRecyclerViewAdapterForPriceInEstimate).setListData(
                        allCategories
                    )
                    (recyclerView.adapter as TypeOfWorkRecyclerViewAdapterForPriceInEstimate).notifyDataSetChanged()
                    dialog?.dismiss()
                }
            }

        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }
}