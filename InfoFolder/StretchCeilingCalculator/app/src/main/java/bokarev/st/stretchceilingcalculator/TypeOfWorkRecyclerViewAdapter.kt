package bokarev.st.stretchceilingcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate


class TypeOfWorkRecyclerViewAdapter(val listener: RowClickListener): RecyclerView.Adapter<TypeOfWorkRecyclerViewAdapter.MyViewHolder>() {

    var items  = ArrayList<ClientAndEstimate>()

    fun setListData(data: ArrayList<ClientAndEstimate>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val inflater = LayoutInflater.from(parent.context).inflate(R.layout.type_of_work_recyclerview_row, parent, false)
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

    class MyViewHolder(view: View, val listener: RowClickListener): RecyclerView.ViewHolder(view) {

        val nameOfWork = view.findViewById<TextView>(R.id.NameOfWork)
        val price = view.findViewById<TextView>(R.id.Price)
        val countOfElement = view.findViewById<TextView>(R.id.CountOfElement)

        fun bind(data: ClientAndEstimate) {
            nameOfWork.text = data.CategoryName

            val priseStr = "${data.Price} ₽"
            price.text = priseStr
            countOfElement.text = data.Count.toString()

          //  deleteUserID.setOnClickListener {
                //listener.onDeleteUserClickListener(data)
            //}
        }
    }

    interface RowClickListener{
        fun onDeleteUserClickListener(user: ClientAndEstimate)
        fun onItemClickListener(user: ClientAndEstimate)
    }
}