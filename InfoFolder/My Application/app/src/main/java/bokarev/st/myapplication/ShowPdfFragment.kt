package bokarev.st.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels


class ShowPdfFragment : Fragment() {

    companion object {
        fun newInstance() = ShowPdfFragment()
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
            SharePdf().sharePdf(requireActivity(), filePath)
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


    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

}