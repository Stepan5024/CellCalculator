package bokarev.st.pdffinaltest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReceiptRecyclerAdapter(
    private var receiptList: List<Receipt>,
    private var iGetReceiptData: IGetReceiptData
) :
    RecyclerView.Adapter<ReceiptRecyclerAdapter.ViewHolder>() {
    private val formatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm a")
    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.receipt_item, viewGroup, false)
        layoutInflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val receipt = receiptList[i]

        viewHolder.p1ValueTextView.text = String.format("%.2f", receipt.priceP1)
        viewHolder.p2ValueTextView.text = String.format("%.2f", receipt.priceP2)
        viewHolder.p3ValueTextView.text = String.format("%.2f", receipt.priceP2)
        viewHolder.s1ValueTextView.text = String.format("%.2f", receipt.priceP1 * receipt.quantityP1)
        viewHolder.s2ValueTextView.text = String.format("%.2f", receipt.priceP2 * receipt.quantityP2)
        viewHolder.s3ValueTextView.text = String.format("%.2f", receipt.priceP2 * receipt.quantityP3)
        viewHolder.q1ValueTextView.text = receipt.quantityP1.toString()
        viewHolder.q2ValueTextView.text = receipt.quantityP2.toString()
        viewHolder.q3ValueTextView.text = receipt.quantityP3.toString()
        viewHolder.totalValueTextView.text =
            String.format("%.2f", receipt.priceP1 + receipt.priceP3 + receipt.priceP3)
        viewHolder.generatedDateTxt.text = formatterDate.format(
            LocalDateTime.parse(receipt.createdDate, formatterDate))

        viewHolder.editPrintBtn.setOnClickListener {
            iGetReceiptData.onReceiptEditClicked(receipt)
        }
    }

    override fun getItemCount(): Int {
        return this.receiptList.size
    }

    fun updateUserList(receiptList: List<Receipt>) {
        this.receiptList = receiptList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var p1ValueTextView: TextView
        var p2ValueTextView: TextView
        var p3ValueTextView: TextView
        var q1ValueTextView: TextView
        var q2ValueTextView: TextView
        var q3ValueTextView: TextView
        var s1ValueTextView: TextView
        var s2ValueTextView: TextView
        var s3ValueTextView: TextView
        var generatedDateTxt: TextView
        var totalValueTextView: TextView
        var editPrintBtn: ConstraintLayout

        init {
            p1ValueTextView = view.findViewById<View>(R.id.p1ValueTextView) as TextView
            p2ValueTextView = view.findViewById<View>(R.id.p2ValueTextView) as TextView
            p3ValueTextView = view.findViewById<View>(R.id.p3ValueTextView) as TextView
            q1ValueTextView = view.findViewById<View>(R.id.q1ValueTextView) as TextView
            q2ValueTextView = view.findViewById<View>(R.id.q2ValueTextView) as TextView
            q3ValueTextView = view.findViewById<View>(R.id.q3ValueTextView) as TextView
            s1ValueTextView = view.findViewById<View>(R.id.sum1ValueTextView) as TextView
            s2ValueTextView = view.findViewById<View>(R.id.sum2ValueTextView) as TextView
            s3ValueTextView = view.findViewById<View>(R.id.sum3ValueTextView) as TextView
            generatedDateTxt = view.findViewById<View>(R.id.dateTextView) as TextView
            totalValueTextView = view.findViewById<View>(R.id.totalValueTextView) as TextView
            editPrintBtn = view.findViewById<View>(R.id.editPrintBtn) as ConstraintLayout

        }
    }

    interface IGetReceiptData {
        fun onReceiptEditClicked(receipt: Receipt)
    }

}
