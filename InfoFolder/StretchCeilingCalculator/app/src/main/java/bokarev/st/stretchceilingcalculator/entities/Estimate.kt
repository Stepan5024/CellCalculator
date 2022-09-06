package bokarev.st.stretchceilingcalculator.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Estimate(

    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val _idClient: Int,
    val _idTypeCategory: Int,
    val Count: Double,
    val DateOfCreation: String,
    val DateOfEditing: String,
)
