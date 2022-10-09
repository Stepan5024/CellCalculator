package bokarev.st.stretchceilingcalculator.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TypeCategory(

    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val CategoryName: String,
    val UnitsOfMeasurement: String,
    val Price: Int,
    val _idTypeOfWork: Int,

)
