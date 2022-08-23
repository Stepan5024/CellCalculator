package bokarev.st.dbcalculationtest.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Calculation(

    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val _idClient: Int,
    val _idTypeCategory: Int,
    val Count: Int,

)
