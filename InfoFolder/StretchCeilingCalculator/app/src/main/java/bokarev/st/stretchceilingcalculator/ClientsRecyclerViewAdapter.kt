package bokarev.st.stretchceilingcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.entities.Client



class ClientsRecyclerViewAdapter(val listener: RowClickListener): RecyclerView.Adapter<ClientsRecyclerViewAdapter.MyViewHolder>() {

    var items  = ArrayList<Client>()

    fun setListData(data: ArrayList<Client>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val inflater = LayoutInflater.from(parent.context).inflate(R.layout.clients_recyclerview_row, parent, false)
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

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val deleteUserID = view.findViewById<ImageView>(R.id.deleteUserID)

        fun bind(data: Client) {
            tvName.text = data.ClientName

            tvEmail.text = data.Address


            tvPhone.text = data.Tel

            deleteUserID.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    interface RowClickListener{
        fun onDeleteUserClickListener(user: Client)
        fun onItemClickListener(user: Client)
    }
}