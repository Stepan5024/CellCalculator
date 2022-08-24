package bokarev.st.recyclerviewapptest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bokarev.st.recyclerviewapptest.databinding.ItemSchoolBinding

class SchoolAdapter(var Schools: List<School>) :
    RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder>() {

    inner class SchoolViewHolder(val binding: ItemSchoolBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSchoolBinding.inflate(layoutInflater, parent, false)
        return SchoolViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        holder.binding.apply {
            tvSchoolName.text = Schools[position].title
            swSchool.isChecked = Schools[position].isChecked
        }
    }

    override fun getItemCount(): Int {
        return Schools.size
    }

}