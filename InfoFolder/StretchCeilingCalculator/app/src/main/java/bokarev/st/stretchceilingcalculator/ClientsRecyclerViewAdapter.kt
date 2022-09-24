package bokarev.st.stretchceilingcalculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.stretchceilingcalculator.entities.Client


class ClientsRecyclerViewAdapter(private val listener: RowClickListener) :
    RecyclerView.Adapter<ClientsRecyclerViewAdapter.MyViewHolder>() {

    private var items = ArrayList<Client>()

    fun setListData(data: ArrayList<Client>) {
        this.items = data
    }

    fun getListData(): ArrayList<Client> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.clients_recyclerview_row, parent, false)
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

    class MyViewHolder(view: View, private val listener: RowClickListener) :
        RecyclerView.ViewHolder(view) {

        private val tvName = view.findViewById<TextView>(R.id.tvName)
        private val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        private val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        private val deleteUserID = view.findViewById<ImageView>(R.id.deleteUserID)

        fun bind(data: Client) {
            tvName.text = data.ClientName

            tvEmail.text = data.Address


            tvPhone.text = data.Tel

            tvPhone.setOnClickListener {

                val clipboard =
                    itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("text", tvPhone.text)
                clipboard!!.setPrimaryClip(clip)
                val toast = Toast.makeText(
                    itemView.context,
                    "номер телефона скопирован",
                    Toast.LENGTH_LONG
                )
                toast.show()
            }

            deleteUserID.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(user: Client)
        fun onItemClickListener(user: Client)
    }
}