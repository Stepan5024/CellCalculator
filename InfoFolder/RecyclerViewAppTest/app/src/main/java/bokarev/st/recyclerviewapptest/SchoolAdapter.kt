package bokarev.st.recyclerviewapptest

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.recyclerviewapptest.databinding.ItemSchoolBinding


class SchoolAdapter(var Schools: List<School>, private val listener: RowClickListener) :
    RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder>() {

    inner class SchoolViewHolder(val binding: ItemSchoolBinding, private val listener: RowClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        private val tvSchoolName = binding.tvSchoolName
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val swSchool = binding.swSchool
        fun bind(data: School) {
            tvSchoolName.text = data.title

            swSchool.isChecked = data.isChecked


            tvSchoolName.setOnClickListener {
                //listener.onDeleteUserClickListener2(data)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSchoolBinding.inflate(layoutInflater, parent, false)
        return SchoolViewHolder(binding, listener)
    }


    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        holder.binding.apply {
            tvSchoolName.text = Schools[position].title
            swSchool.isChecked = Schools[position].isChecked
        }
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(Schools[position])
        }
        holder.bind(Schools[position])
    }

    override fun getItemCount(): Int {
        return Schools.size
    }


    interface RowClickListener {
        fun onDeleteUserClickListener(school: School)
        fun onItemClickListener(school: School)
    }
}