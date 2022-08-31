package bokarev.st.pdffinaltest

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bokarev.st.pdffinaltest.databinding.FragmentShowPdfBinding


class ShowPdfFragment : Fragment() {

    companion object {
        fun newInstance() = ShowPdfFragment()
    }

    private var _binding: FragmentShowPdfBinding? = null

    private val binding get() = _binding!!
    private val receiptViewModel: ReceiptViewModel by viewModels {
        ReceiptViewModel.ReceiptViewModelFactory((requireActivity().application as MyApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowPdfBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            try {
                var receipt = bundle.getSerializable("RECEIPT_EXTRA") as Receipt

                if (receipt.filePath.isEmpty() || receipt.filePath.isBlank()) {
                    generatePdf(receipt)
                    displayPdf(receipt)
                } else {
                    displayPdf(receipt)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun displayPdf(receipt: Receipt) {

        var filePath = ShowPdf().findFilePath(receipt.filePath)
        if (filePath != null) {
            _binding!!.pdfImage.setImageBitmap(ShowPdf().showPage(filePath))
            _binding!!.shareDdf.setOnClickListener {
                SharePdf().sharePdf(requireActivity(), filePath)
            }
        } else {
            generatePdf(receipt)
        }
    }

    private fun generatePdf(receipt: Receipt) {
        val bitmapFactory = BitmapFactory.decodeResource(
            this.resources, R.drawable.pizzahead
        )
        var filePath = GeneratePdf.generate(
            bitmapFactory, receipt
        )
        receipt.filePath = filePath
        receiptViewModel.updateReceipt(receipt)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}