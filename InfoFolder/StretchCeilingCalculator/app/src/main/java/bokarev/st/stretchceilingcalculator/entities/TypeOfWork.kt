package bokarev.st.stretchceilingcalculator.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TypeOfWork(

    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val TypeOfWorkName: String,


)
