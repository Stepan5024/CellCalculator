package bokarev.st.dbcalculationtest.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TypeCategory(

    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val CategoryName: String,
    val Price: Int,
    val _idTypeOfWork: Int

)
