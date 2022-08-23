package bokarev.st.dbcalculationtest.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Client(

    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val ClientName: String,
    val Tel: String,
    val IsNew: Boolean,
    val IsPurcharse: Boolean,
    val IsArchive: Boolean,

    )
