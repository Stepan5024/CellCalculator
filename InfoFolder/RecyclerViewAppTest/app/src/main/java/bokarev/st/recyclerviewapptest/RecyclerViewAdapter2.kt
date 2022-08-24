package bokarev.st.recyclerviewapptest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter2(val listener: RowClickListener2): RecyclerView.Adapter<RecyclerViewAdapter2.MyViewHolder>() {

    var items  = ArrayList<UserEntity2>()

    fun setListData(data: ArrayList<UserEntity2>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        return MyViewHolder(inflater, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClickListener2(items[position])
        }
        holder.bind(items[position])

    }



    class MyViewHolder(view: View, val listener: RowClickListener2): RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val deleteUserID = view.findViewById<ImageView>(R.id.deleteUserID)

        fun bind(data: UserEntity2) {
            tvName.text = data.name

            tvEmail.text = data.email


            tvPhone.text = data.phone

            deleteUserID.setOnClickListener {
                listener.onDeleteUserClickListener2(data)
            }
        }
    }

    interface RowClickListener2{
        fun onDeleteUserClickListener2(user: UserEntity2)
        fun onItemClickListener2(user: UserEntity2)
    }
}